package com.raygo.cameravs;


import java.util.Calendar;

import com.special.ResideMenu.ResideMenu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeTaskFragment extends Fragment {

	private View parentView;
    private ResideMenu resideMenu;
    private MenuActivity parentActivity;
    private Context parentContext;
    private SharedPreferences settings;
	private SharedPreferences.Editor editor;
    
	private CheckBox cbTimerTask;
	private CheckBox cbEveryday;
	private Button btComit;
	private ImageButton ibtSelectStime;
	private ImageButton ibtSelectEtime;
	private TextView tvStime, tvEtime;
	
	private Boolean isRunTimerTask;
	private Calendar rili;
	private int starthour, startminute;
	private int stophour, stopminute;
	private Boolean isEveryday = false ;
	private AlarmManager alarmManager =null;
	private PendingIntent pendingIntent =null;
	
	public final String MYACTION = "android.intent.action.STARTMYAP"; 
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	parentView = inflater.inflate(R.layout.bsettings, container, false);
        parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        parentContext = getActivity().getBaseContext();
        
        settings = parentActivity.getSharedPreferences("LocalSettings", 0);
        editor = settings.edit();
        
        setUpViews();
    	return parentView;
    }
    
    private void setUpViews() {        
        
        cbTimerTask = (CheckBox)parentView.findViewById(R.id.cb_timerdo);
        cbEveryday = (CheckBox)parentView.findViewById(R.id.cb_everydaydo);
        btComit = (Button)parentView.findViewById(R.id.btn_comitttask);
        ibtSelectStime = (ImageButton)parentView.findViewById(R.id.ibStartTime);
        ibtSelectEtime = (ImageButton)parentView.findViewById(R.id.ibStopTime);
        tvStime = (TextView)parentView.findViewById(R.id.tvStartTime);
        tvEtime = (TextView)parentView.findViewById(R.id.tvStopTime);
        
        InitShow();        
        
        cbTimerTask.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		@Override
    		public void onCheckedChanged(CompoundButton arbuttonView, boolean isChecked) {
    			// TODO Auto-generated method stub
    			isRunTimerTask = isChecked;
    		}
        });
        
        cbEveryday.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		@Override
    		public void onCheckedChanged(CompoundButton arbuttonView, boolean isChecked) {
    			// TODO Auto-generated method stub
    			isEveryday = isChecked;
    		}
        });
        
		btComit.setOnClickListener(onclickListener);
		ibtSelectStime.setOnClickListener(onclickListener);
		ibtSelectEtime.setOnClickListener(onclickListener);
    }
    
	//button click listener
	private OnClickListener onclickListener = new OnClickListener() {
		@Override
		public void onClick(View v1) {
			// TODO Auto-generated method stub
			int id = v1.getId();
			if (id == R.id.btn_comitttask)
			{
				Commit();
			} 
			else if (id == R.id.ibStartTime)
			{
				TimePickerDialog dialog = new TimePickerDialog(parentActivity, new OnTimeSetListener()
				{
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute)
					{
						starthour = hourOfDay;
						startminute = minute;
						String sStime = String.format("%02d:%02d", starthour, startminute);
				        tvStime.setText(sStime);
					}
				}, starthour, startminute, true);
				dialog.show();
			}
			else if(id == R.id.ibStopTime)
			{
				TimePickerDialog dialog = new TimePickerDialog(parentActivity, new OnTimeSetListener()
				{
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute)
					{
						stophour = hourOfDay;
						stopminute = minute;
						String sEtime = String.format("%02d:%02d", stophour, stopminute);
				        tvEtime.setText(sEtime);
					}
				}, stophour, stopminute, true);
				dialog.show();
			}
		}

	};

	//Init layout show
    private void InitShow(){
    	isRunTimerTask = settings.getBoolean("TimerTask", false);
    	cbTimerTask.setChecked(isRunTimerTask);
    	
    	starthour = settings.getInt("StartHour", 9);
    	startminute = settings.getInt("StartMinute", 0); 
    	String sStime = String.format("%02d:%02d", starthour, startminute);
        tvStime.setText(sStime);
        
        stophour = settings.getInt("StopHour", 18);
        stopminute = settings.getInt("StopMinute", 0); 
    	String sEtime = String.format("%02d:%02d", stophour, stopminute);
        tvEtime.setText(sEtime); 
        
    }
    
	//Commit the config
    private void Commit(){
    	if (isRunTimerTask) {
			editor.putBoolean("TimerTask", true);
			editor.putInt("StartHour", starthour);
			editor.putInt("StartMinute", startminute);
			editor.putInt("StopHour", stophour);
			editor.putInt("StopMinute", stopminute);
			editor.commit();
	        
			Intent intent = new Intent(MYACTION);
			intent.setClass(parentContext, MyReceiver.class);
			pendingIntent = PendingIntent.getBroadcast(parentActivity.getApplicationContext(), 0, intent,0);
			alarmManager = (AlarmManager) parentContext.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
			
			rili = Calendar.getInstance();
			rili.setTimeInMillis(System.currentTimeMillis());
			//set the alarm to the config time
			rili.set(Calendar.HOUR_OF_DAY, starthour);  
			rili.set(Calendar.MINUTE, startminute);
			rili.set(Calendar.SECOND, 0);
			if(isEveryday)
			{
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, rili.getTimeInMillis(), 24*3600*1000, pendingIntent);
		    }  
		    else  
		    {  
		    	alarmManager.set(AlarmManager.RTC_WAKEUP, rili.getTimeInMillis(), pendingIntent); 				    	    
		    }
		}
		else
		{
			editor.putBoolean("TimerTask", false);
			editor.commit();
			if(alarmManager != null)
			{
				Intent intent = new Intent(MYACTION);
				intent.setClass(parentContext, MyReceiver.class);
				pendingIntent = PendingIntent.getBroadcast(parentActivity.getApplicationContext(), 0, intent,0);
				alarmManager.cancel(pendingIntent);
			}
		}
    }

}
