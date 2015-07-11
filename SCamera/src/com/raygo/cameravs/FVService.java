package com.raygo.cameravs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FVService extends Service 
{
	//定义浮动窗口布局
	private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;	
	private SurfaceView mFloatView;

	private Camera mCamera;
	private int swidth, sheight;
	private File savefoder;
	private Boolean AutoSave;
	private Boolean AutoFocus;
	private long SavePeroid;
	private int PictureWidth;
	private int PictureHeight;
	private Boolean isSaving;
	private String savedFile = null;
	private String savepath = null;
	private Boolean isTimerTask = false;
	private Boolean isShow = true;
	private int StopHour;
	private int StopMinute;
   
	@Override
	public void onCreate()
	{
		super.onCreate();			
	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		isTimerTask = intent.getBooleanExtra("IsTimerTask", false);
		isSaving = false;
		if((mFloatLayout != null) & (mWindowManager != null))
		{
			mWindowManager.removeView(mFloatLayout);
		}
		getParas();
		createFloatView();
		
		Timer timer = new Timer();
		if(AutoSave)
		{
			if(AutoFocus)
				timer.schedule(tPaizhaoFocus, 5000, SavePeroid);
			else
				timer.schedule(tPaizhao, 5000, SavePeroid);	
		}
		if(isTimerTask)
		{
			Calendar rili = Calendar.getInstance();
			rili.setTimeInMillis(System.currentTimeMillis());
			int hour = rili.get(Calendar.HOUR_OF_DAY);
			int minute = rili.get(Calendar.MINUTE);
			long delaytime = 0;
			if ((hour < StopHour)) {
				delaytime = ((StopHour - hour) * 3600 + (StopMinute - minute) * 60) * 1000;
			} else if (hour == StopHour) {
				if (StopMinute > minute)
					delaytime = (( StopMinute - minute) * 60) * 1000;
				else
					delaytime = 10000;
			} else {
				delaytime = ((StopHour + 24 - hour) * 3600 + (StopMinute - minute) * 60) * 1000;
			}
			Date when = new Date();
			timer.schedule(tStopSelf, delaytime);
		}
	}
	
	//timer task to paizhao
	private TimerTask tPaizhao = new TimerTask() {
		@Override
		public void run() {
			if(mCamera != null){				
				mCamera.takePicture(null, null, new TakePictureCallback());
			}
		}
	};
	
	//timer task to paizhao with autofocus
	private TimerTask tPaizhaoFocus = new TimerTask() {
		@Override
		public void run() {
			if (mCamera != null) {
				mCamera.autoFocus(new Camera.AutoFocusCallback() {
					public void onAutoFocus(boolean success, Camera camera) {
						// TODO Auto-generated method stub
						if (success) {
							mCamera.takePicture(null, null,	new TakePictureCallback());
						}
					}
				});
			}
		}
	};

	// timer task to paizhao
	private TimerTask tStopSelf = new TimerTask() {
		@Override
		public void run() {
			stopSelf();
		}
	};
	
	//
	private void getParas()
	{
		SharedPreferences settings = getSharedPreferences("LocalSettings", 0);		
		if(isTimerTask)
		{
			isShow = false;
			AutoSave = true;
			SavePeroid = 30 *1000;
			StopMinute = settings.getInt("StopMinute",0);
			StopHour = settings.getInt("StopHour", 0);
		}
		else
		{
			isShow = settings.getBoolean("Preview",false);
			AutoSave = settings.getBoolean("AutoSave",false);
			SavePeroid = (settings.getInt("SaveTime", 15))*1000;
		}
		AutoFocus = settings.getBoolean("AutoFocus",false);
		int SaveSize = settings.getInt("SaveSize", 0);
		int SavePath = settings.getInt("SavePath", 0);
		int ShowSize = settings.getInt("ShowSize", 2);
		//保存目录
		if((SavePath == 1)&&(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)))
			savepath = "/mnt/sdcard2/SCamera/";
		else
			savepath = "/mnt/sdcard/SCamera/";		
		savefoder = new File(savepath);
		if (!savefoder.exists()) {
			savefoder.mkdirs();
		}
		//保存尺寸
		List<Size> sizes = null;
		int numl = 0;
		try{
			mCamera = Camera.open();
			Camera.Parameters parameters = mCamera.getParameters(); 
			sizes = parameters.getSupportedPictureSizes();
			numl = sizes.size();
		}catch(Exception e){
			Toast.makeText(FVService.this, "Open camera fail", Toast.LENGTH_SHORT).show();
		}
		if(numl > 4)
		{
	        switch(SaveSize)
	        {
	        case 0:
	        	PictureWidth = sizes.get(0).width;
	        	PictureHeight = sizes.get(0).height;
	        	break;
	        case 1:
	        	PictureWidth = sizes.get(1).width;
	        	PictureHeight = sizes.get(1).height;
	        	break;
	        case 2:
	        	PictureWidth = sizes.get(2).width;
	        	PictureHeight = sizes.get(2).height;
	        	break;
	        case 3:
	        	PictureWidth = sizes.get(3).width;
	        	PictureHeight = sizes.get(3).height;
	        	break;
	        case 4:
	        	PictureWidth = sizes.get(4).width;
	        	PictureHeight = sizes.get(4).height;
	        	break;
	        default:
	        	PictureWidth = sizes.get(0).width;
	        	PictureHeight = sizes.get(0).height;
	        	break;
	        }
		}
		else{
			PictureWidth = 640;
        	PictureHeight = 480;
		}
        switch(ShowSize)
        {
        case 0:
        	swidth = 300;
        	sheight = 450;
        	break;
        case 1:
        	swidth = 200;
        	sheight = 300;
        	break;
        case 2:
        	swidth = 100;
        	sheight = 150;
        	break;
        default:
        	swidth = 200;
        	sheight = 300;
        	break;
        }
        
        if(!isShow)
        {
        	swidth = 2;
        	sheight = 3;
        }
        
	}
	
	private void createFloatView()
	{
		wmParams = new WindowManager.LayoutParams();
		//获取WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		//设置window type
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT; 
		//设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888; 
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;// | LayoutParams.FLAG_NOT_TOUCHABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.CENTER | Gravity.TOP; 
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据  
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //移动窗口
        mFloatLayout.setOnTouchListener(new OnTouchListener(){
        	int lastX, lastY;  
            int paramX, paramY;
			@Override
			public boolean onTouch(View mview, MotionEvent event) {
				// TODO Auto-generated method stub
	    		switch (event.getAction()) {
	    		case MotionEvent.ACTION_DOWN: //捕获手指触摸按下动作
	    			lastX = (int) event.getRawX();
	                lastY = (int) event.getRawY();  
	                paramX = wmParams.x;  
	                paramY = wmParams.y;
	    			break;
	    		case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作           
	    			int dx = (int) event.getRawX() - lastX;  
	                int dy = (int) event.getRawY() - lastY;  
	                wmParams.x = paramX + dx;  
	                wmParams.y = paramY + dy;
	    			mWindowManager.updateViewLayout(mFloatLayout, wmParams);  //刷新显示
	    			break;
	    		}
				return true;
			}});
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), 
        					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //浮动窗口按钮
        mFloatView = (SurfaceView)mFloatLayout.findViewById(R.id.sfview);

        mFloatView.getHolder().setFixedSize(swidth, sheight);
        mFloatView.getHolder().addCallback(new SurfaceCallback());

	}
		
    //Save the pic
	private final class TakePictureCallback implements PictureCallback {
		
    	public void onPictureTaken(byte[] data, Camera camera) {
        	try {
    			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);    			
    			
    			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	        String date = dateFormat.format(new Date());
    	        String photoFile = date + ".png";
    			File file = new File(savefoder, photoFile);
    			
    			FileOutputStream outStream = new FileOutputStream(file);
    			bitmap.compress(CompressFormat.PNG, 100, outStream);
    			outStream.close();
    			
    			mCamera.stopPreview();
    			mCamera.startPreview();
    			savedFile = photoFile;
				isSaving = true;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
    };
    
    //Start the camera
    private final class SurfaceCallback implements SurfaceHolder.Callback {
    	
    	public void surfaceCreated(SurfaceHolder holder) { 
    		
    		if(mCamera != null){
    			try {
    				mCamera.setPreviewDisplay(holder);
    			} catch (IOException e) {
    				Log.e("Save Error:", e.toString());
    				mCamera = null;
    			}
    		}
    		else
    		{
    			Toast.makeText(FVService.this, "Open camera fail", Toast.LENGTH_SHORT).show();
    		}
    	} 
    	
    	public void surfaceDestroyed(SurfaceHolder holder) { 
    		if (mCamera != null) {
    			mCamera.setPreviewCallback(null);
    			mCamera.stopPreview();
    			mCamera.release();
    			mCamera = null;
			}
    	} 

    	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) { 
    		if (mCamera != null) {
    			// 获得相机参数
    			Camera.Parameters parameters = mCamera.getParameters();
    			// 旋转90度
				setDisplayOrientation(mCamera, 90);
				// 设置预览大小
				//parameters.setPreviewSize(width, height);
				//设置图片旋转				
				parameters.setRotation(90);
				//设置闪光灯				
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				// 设置照片大小
				
				parameters.setPictureSize(PictureWidth, PictureHeight);
				// 设置照片格式
				parameters.setPictureFormat(PixelFormat.JPEG);
				// 设置相机参数
				mCamera.setParameters(parameters);				
				// 开始预览
				mCamera.startPreview();
				Toast.makeText(FVService.this, "Start service success.", Toast.LENGTH_SHORT).show();
    		}
    	}
    	
    	protected void setDisplayOrientation(Camera camera, int angle) {
    		Method downPolymorphic;
    		try {
    			downPolymorphic = camera.getClass().getMethod(
    					"setDisplayOrientation", new Class[] { int.class });
    			if (downPolymorphic != null)
    				downPolymorphic.invoke(camera, new Object[] { angle });
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

      
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		isSaving = false;
		Toast.makeText(FVService.this, "Service is stopped.", Toast.LENGTH_SHORT).show();
		if(mFloatLayout != null)
		{
			mWindowManager.removeView(mFloatLayout);
		}
	}
	
}
