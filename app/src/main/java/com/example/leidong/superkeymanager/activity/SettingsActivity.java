package com.example.leidong.superkeymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.quit.QuitActivities;

/**
 * Created by leidong on 2016/10/17.
 * 设置界面
 */
public class SettingsActivity extends AppCompatActivity{
    private static final String TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        init();
    }

    /**
     * 获取组件并初始化
     */
    private void init() {

    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_settings_actiity, menu);
        return true;
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //回主界面
            case R.id.menu_settings_activity_main:
                Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            break;

            //离开应用
            case R.id.menu_settings_activity_exit:
                QuitActivities.getInstance().exit();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
