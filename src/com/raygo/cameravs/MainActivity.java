package com.raygo.cameravs;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity 
{
	//定义浮动窗口布局
	private Intent intent;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private boolean isAutoSave;
	private boolean isPreview;
	private int iShowSize;
	private int iSaveSize;
	private int iSaveTime;
	private int iSavePath;
	
	//layout
	private Button btStart;
	private Spinner spShowsize;
	private Spinner spSavesize;
	private Spinner spSavetime;
	private Spinner spSavepath;
	private CheckBox cbPreview;
	private CheckBox cbAutoSave;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
        //SharedPreferences
        settings = getSharedPreferences("LocalSettings", 0);
        editor = settings.edit();
        InitFirstRunParas();
              
        btStart = (Button)findViewById(R.id.startservice);
        spShowsize = (Spinner)findViewById(R.id.spinner_previewsize);
        spSavesize = (Spinner)findViewById(R.id.spinner_savesize);
        spSavetime = (Spinner)findViewById(R.id.spinner_saveperoid);
        spSavepath = (Spinner)findViewById(R.id.spinner_savepath);
        cbPreview = (CheckBox)findViewById(R.id.cb_showpreview);
        cbAutoSave = (CheckBox)findViewById(R.id.cb_autosave);
        
        InitShow();
        
        //Service ON/off
        btStart.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Button bt = (Button)arg0;
				intent = new Intent(MainActivity.this, FVService.class);
				if(isServiceRunning())
				{				
					stopService(intent);
					//finish();
					bt.setText(R.string.startfw);
				}
				else
				{
					CommitParas();
					startService(intent);
					bt.setText(R.string.stopfw);
				}
			}
        });
        
        spShowsize.setOnItemSelectedListener(onselectListener);
        spSavesize.setOnItemSelectedListener(onselectListener);
        spSavetime.setOnItemSelectedListener(onselectListener);
        spSavepath.setOnItemSelectedListener(onselectListener);
        cbPreview.setOnCheckedChangeListener(oncheckListener);
        cbAutoSave.setOnCheckedChangeListener(oncheckListener);
        
    }
    
    ///checkbox 功能
    private OnCheckedChangeListener oncheckListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arbuttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			int id = arbuttonView.getId();
			switch(id)
			{
				case R.id.cb_autosave:
				{ 	    	
	        		isAutoSave = isChecked;
	        		break;
				}
				case R.id.cb_showpreview:
				{
					isPreview = isChecked;
	        		break;
				}
				default:
					break;
			}
		}  

    };
    
    //下拉框
    private OnItemSelectedListener onselectListener = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			//Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_LONG).show();
			int spid = parent.getId();
			switch(spid)
			{
				case R.id.spinner_previewsize:
					iShowSize = position;
                	break;
				case R.id.spinner_savesize:
					iSaveSize = position;
					break;
				case R.id.spinner_saveperoid:
					iSaveTime = position *10 + 10;
					break;
				case R.id.spinner_savepath:
					iSavePath = position;
                	break;
                default:
                	break;	
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    };

    //提交更改    
    private void CommitParas(){
    	
        editor.putBoolean("AutoSave", isAutoSave);
        editor.putBoolean("Preview", isPreview);
        editor.putInt("ShowSize", iShowSize);
        editor.putInt("SaveSize", iSaveSize);
        editor.putInt("SaveTime", iSaveTime);
        editor.putInt("SavePath", iSavePath);
        editor.commit();
   
    }
    
    //第一次启动
    private void InitFirstRunParas(){
        boolean user_first = settings.getBoolean("FIRST",true);
        if(user_first){//第一次
        	editor.putBoolean("FIRST", false);
        	editor.putBoolean("AutoSave", false);
        	editor.putBoolean("Preview", true);
        	editor.putInt("ShowSize", 1);
        	editor.putInt("SaveSize", 0);
        	editor.putInt("SaveTime", 10);
        	editor.putInt("SavePath",0);
        	editor.commit();
        }        
    }
    
    //初始化布局
    private void InitShow(){
    	isPreview = settings.getBoolean("Preview", true);
        isAutoSave = settings.getBoolean("AutoSave", true);
        iShowSize = settings.getInt("ShowSize", 1);
        iSaveSize = settings.getInt("SaveSize", 0);
        iSaveTime = settings.getInt("SaveTime", 5);
        iSavePath = settings.getInt("SavePath", 0);
        
        cbPreview.setChecked(isPreview);
        cbAutoSave.setChecked(isAutoSave);
        spShowsize.setSelection(iShowSize);
        spSavesize.setSelection(iSaveSize);
        spSavetime.setSelection((iSaveTime-10)/10);
        spSavepath.setSelection(iSavePath);
        
        if(isServiceRunning())
        	btStart.setText(R.string.stopfw);
        else
        	btStart.setText(R.string.startfw);
    }
    
    //服务是否运行
    private boolean isServiceRunning() {
    	try{
    		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  
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
    
    //退出时
	protected void onDestroy()
	{
		super.onDestroy();
	}
    
    
}