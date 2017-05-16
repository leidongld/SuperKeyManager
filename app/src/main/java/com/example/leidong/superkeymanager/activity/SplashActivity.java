package com.example.leidong.superkeymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.leidong.superkeymanager.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by leidong on 2016/10/17.
 * 欢迎界面，持续3秒
 */

public class SplashActivity extends Activity{
    private static final String TAG = "SplashActivity";

    private TextView tv_splash_activity_version;
    private ImageView iv_splash_activity_picture;
    private static final int SPLASH_TIME = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        init();

        LinearLayout rl_splash_activity = (LinearLayout)findViewById(R.id.rl_splash_activity);
        //填充版本号
        tv_splash_activity_version.setText(getVersion());

        //背景透明度3s内从0.3变为1.0
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setDuration(SPLASH_TIME);
        rl_splash_activity.startAnimation(alphaAnimation);

        //创建Timer对象
        Timer timer = new Timer();
        //创建TimerTask对象
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent registerIntent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        };
        timer.schedule(timerTask, SPLASH_TIME);
    }

    /**
     * 获取组件并初始化
     */
    private void init() {
        tv_splash_activity_version = (TextView)findViewById(R.id.tv_splash_activity_version);
        iv_splash_activity_picture = (ImageView)findViewById(R.id.iv_splash_activity_picture);
        iv_splash_activity_picture.setImageResource(R.drawable.splash_picture);
    }

    /**
     * 得到软件版本号
     * @return
     */
    public String getVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
