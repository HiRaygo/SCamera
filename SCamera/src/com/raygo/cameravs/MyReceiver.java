package com.raygo.cameravs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Intent it=new Intent(context, FVService.class); 
		it.putExtra("IsTimerTask", true);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
		context.startService(it); 
	}

}
