package com.raygo.cameravs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class MenuActivity extends FragmentActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemTTask;
    private ResideMenuItem itemAbout;

    private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	public static boolean isServiceRunning;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        
        //SharedPreferences
        settings = getSharedPreferences("LocalSettings", 0);
        editor = settings.edit();
        InitFirstRunParas();
        
        setUpMenu();
        changeFragment(new HomeFragment());
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        //resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home,  R.string.menu1);
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, R.string.menu2);
        itemTTask  = new ResideMenuItem(this, R.drawable.icon_time, R.string.menu3);
        itemAbout = new ResideMenuItem(this, R.drawable.icon_about, R.string.menu4);        

        itemHome.setOnClickListener(this);
        itemTTask.setOnClickListener(this);
        itemAbout.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemTTask, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_LEFT);
        
        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        
        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new HomeFragment());
        }else if (view == itemTTask){
            changeFragment(new TimeTaskFragment());
        }else if (view == itemAbout){
            changeFragment(new AboutFragment());
        }else if (view == itemSettings){
            changeFragment(new SettingsFragment());
        }

        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access 
    public ResideMenu getResideMenu(){
        return resideMenu;
    }
     
    //app first run
    private void InitFirstRunParas(){
        boolean user_first = settings.getBoolean("FIRST",true);
        if(user_first){//µÚÒ»´Î
        	editor.putBoolean("FIRST", false);
        	editor.putBoolean("AutoSave", false);
        	editor.putBoolean("Preview", true);
        	editor.putBoolean("AutoFocus", false);
        	editor.putBoolean("TimerTask", false);
        	editor.putBoolean("IsNormalTask", false);
        	editor.putInt("ShowSize", 1);
        	editor.putInt("SaveSize", 0);
        	editor.putInt("SaveTime", 10);
        	editor.putInt("SavePath",0);
        	editor.commit();
        }        
    }   

}
