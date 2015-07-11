package com.raygo.cameravs;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;

public class HomeFragment extends Fragment {

    private View parentView;
    private ResideMenu resideMenu;
    private MenuActivity parentActivity;
    private Context parentContext;
	//layout
  	private Button btStart;
  	private CheckBox cbPreview;
  	private CheckBox cbAutoSave;
  	
	private Boolean isAutoSave;
	private Boolean isPreview;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);
        parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        parentContext = getActivity().getBaseContext();
        
        settings = parentActivity.getSharedPreferences("LocalSettings", 0);
        editor = settings.edit();
        setUpViews();
        return parentView;
    }

    private void setUpViews() {        
       
        // add gesture operation's ignored views
        FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);
        
        btStart = (Button)parentView.findViewById(R.id.btn_startservices);
        cbPreview = (CheckBox)parentView.findViewById(R.id.cb_showpreview);
        cbAutoSave = (CheckBox)parentView.findViewById(R.id.cb_autosave);
        
        InitShow();
        
        btStart.setOnClickListener(onclickListener);
        cbPreview.setOnCheckedChangeListener(oncheckListener);
        cbAutoSave.setOnCheckedChangeListener(oncheckListener);
    }
    
	//button click listener
    private OnClickListener onclickListener = new OnClickListener(){

		@Override
		public void onClick(View v1) {
			// TODO Auto-generated method stub			
			int id = v1.getId();
			if(id == R.id.btn_startservices)
			{   	
				Button bt = (Button)v1;
				Intent intent = new Intent();
				intent.setClass(parentContext, FVService.class);
				if(ServiceState())
				{				
					parentContext.stopService(intent);
					bt.setText(R.string.startfw);
				}
				else
				{
					editor.putBoolean("AutoSave", isAutoSave);
			        editor.putBoolean("Preview", isPreview);
			        editor.commit();
			        intent.putExtra("IsTimerTask", false);
					parentContext.startService(intent);
					bt.setText(R.string.stopfw);
				}
			}
		}
    	
    };
    
    ///checkbox CheckedChangeListener
    private OnCheckedChangeListener oncheckListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arbuttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			int id = arbuttonView.getId();
			if(id == R.id.cb_autosave)
			{
        		isAutoSave = isChecked;
			}
			else if(id == R.id.cb_showpreview)
			{
				isPreview = isChecked;
			}
		}  

    };

	//service is running or not
    public boolean ServiceState() {
    	try{
    		ActivityManager manager =(ActivityManager) parentContext.getSystemService(Context.ACTIVITY_SERVICE);
    		for (RunningServiceInfo services : manager.getRunningServices(100)) {  
    			if ("com.raygo.cameravs.FVService".equals(services.service.getClassName())) {  
    				return true;  
    			}  
    		}
    	}
        catch(SecurityException ex)
        {
        	return false; 
        }
        return false;  
    }
    
	//Init layout show
    private void InitShow(){
    	isPreview = settings.getBoolean("Preview", true);
        isAutoSave = settings.getBoolean("AutoSave", true);
        
        cbPreview.setChecked(isPreview);
        cbAutoSave.setChecked(isAutoSave);
        
        if(ServiceState())
        {
        	btStart.setText(R.string.stopfw);
        }
        else
        	btStart.setText(R.string.startfw);
    }
}
