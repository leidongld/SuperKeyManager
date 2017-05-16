package com.example.leidong.superkeymanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.quit.QuitActivities;

/**
 * Created by leido on 2016/10/24.
 */

public class MasterPasswordChangeActivity extends AppCompatActivity{
    private static final String TAG = "MasterPasswordChangeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_master_password);
        QuitActivities.getInstance().addActivity(MasterPasswordChangeActivity.this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件
        init();
    }

    /**
     * 获取组件
     */
    private void init() {
    }
}
