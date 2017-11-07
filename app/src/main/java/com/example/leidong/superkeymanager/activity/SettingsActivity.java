package com.example.leidong.superkeymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.UserDefault;
import com.kenumir.materialsettings.MaterialSettings;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.StorageInterface;

/**
 * Created by leidong on 2016/10/17.
 * 设置界面
 */
public class SettingsActivity extends MaterialSettings{
    private static final String TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设置");

        addItem(new HeaderItem(this).setTitle("设置"));

        addItem(new CheckboxItem(this, "fingerprint").setTitle("使用指纹锁").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem checkboxItem, boolean b) {
                UserDefault.getUserDefaultInstance(null).setIsHasFingerprint(b);
            }
        }));

        addItem(new TextItem(this, "masterpassword").setTitle("更改主密码").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent intent = new Intent(SettingsActivity.this, MasterPasswordChangeActivity.class);
                startActivity(intent);
                finish();
            }
        }));

        addItem(new TextItem(this, "database").setTitle("数据库导出").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {

            }
        }));

        addItem(new TextItem(this, "desc").setTitle("app详情").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
                startActivity(intent);
                finish();
            }
        }));

    }

    @Override
    public StorageInterface initStorageInterface() {
        return null;
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
