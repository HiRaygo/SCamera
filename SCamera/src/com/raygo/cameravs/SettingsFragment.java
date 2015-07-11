package com.raygo.cameravs;

import com.special.ResideMenu.ResideMenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.Spinner;

public class SettingsFragment extends Fragment {
	
	private View parentView;
    private ResideMenu resideMenu;
    private MenuActivity parentActivity;
    
    private SharedPreferences settings;
	private SharedPreferences.Editor editor;
    
	private Spinner spShowsize;
	private Spinner spSavesize;
	private Spinner spSavetime;
	private Spinner spSavepath;
	private CheckBox cbAutoFocus;
	private Button btComit;
	
	private boolean isAutoFocus;
	private int iShowSize;
	private int iSaveSize;
	private int iSaveTime;
	private int iSavePath;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	parentView = inflater.inflate(R.layout.asettings, container, false);
        parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        
        settings = parentActivity.getSharedPreferences("LocalSettings", 0);
        editor = settings.edit();
        setUpViews();
        
    	return parentView;
    }
    
    private void setUpViews() {        
        
        spShowsize = (Spinner)parentView.findViewById(R.id.spinner_previewsize);
        spSavesize = (Spinner)parentView.findViewById(R.id.spinner_savesize);
        spSavetime = (Spinner)parentView.findViewById(R.id.spinner_saveperoid);
        spSavepath = (Spinner)parentView.findViewById(R.id.spinner_savepath);
        cbAutoFocus = (CheckBox)parentView.findViewById(R.id.cb_autofocus);
        btComit = (Button)parentView.findViewById(R.id.btn_comitsave);
        
        InitShow();
        
        spShowsize.setOnItemSelectedListener(onselectListener);
        spSavesize.setOnItemSelectedListener(onselectListener);
        spSavetime.setOnItemSelectedListener(onselectListener);
        spSavepath.setOnItemSelectedListener(onselectListener);
        
        cbAutoFocus.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		@Override
    		public void onCheckedChanged(CompoundButton arbuttonView, boolean isChecked) {
    			// TODO Auto-generated method stub
    			isAutoFocus = isChecked;
    		}
        });
        
		btComit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				// TODO Auto-generated method stub
				editor.putBoolean("AutoFocus", isAutoFocus);
				editor.putInt("ShowSize", iShowSize);
				editor.putInt("SaveSize", iSaveSize);
				editor.putInt("SaveTime", iSaveTime);
				editor.putInt("SavePath", iSavePath);
				editor.commit();
			}
		});
    
    }
    
  //ÏÂÀ­¿ò
    private OnItemSelectedListener onselectListener = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			//Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_LONG).show();
			int spid = parent.getId();
			if(spid == R.id.spinner_previewsize)
			{
				iShowSize = position;
			}
			else if(spid == R.id.spinner_savesize)
			{
				iSaveSize = position;
			}
			else if(spid == R.id.spinner_saveperoid)
			{
				iSaveTime = position *10 + 5;
			}
			else if(spid == R.id.spinner_savepath)
			{
				iSavePath = position;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    };

	//Init layout show
    private void InitShow(){
    	isAutoFocus = settings.getBoolean("AutoFocus", false);
        iShowSize = settings.getInt("ShowSize", 1);
        iSaveSize = settings.getInt("SaveSize", 0);
        iSaveTime = settings.getInt("SaveTime", 5);
        iSavePath = settings.getInt("SavePath", 0);
        
        cbAutoFocus.setChecked(isAutoFocus);
        spShowsize.setSelection(iShowSize);
        spSavesize.setSelection(iSaveSize);
        spSavetime.setSelection((iSaveTime-5)/10);
        spSavepath.setSelection(iSavePath);
        
    }

}
