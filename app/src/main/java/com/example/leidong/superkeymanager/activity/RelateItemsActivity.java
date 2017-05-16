package com.example.leidong.superkeymanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.quit.QuitActivities;

/**
 * Created by leidong on 2016/10/23.
 * 数据库相关界面
 */
public class RelateItemsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RelateItemsActivity";

    private Button bt_relative_items_activity_secure;
    private Button getBt_relative_items_activity_change_password;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relative_items);

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
        bt_relative_items_activity_secure = (Button)findViewById(R.id.bt_relative_items_activity_secure);
        getBt_relative_items_activity_change_password = (Button)findViewById(R.id.bt_relative_items_activity_change_password);
        bt_relative_items_activity_secure.setOnClickListener(this);
        getBt_relative_items_activity_change_password.setOnClickListener(this);
    }

    /**
     * 按钮的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //备份数据条目
            case R.id.bt_relative_items_activity_secure:
                new AlertDialog.Builder(RelateItemsActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要备份数据条目吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //点击确定备份按钮的操作
                            }
                        })
                        .show();
                break;
            //修改主密码
            case R.id.bt_relative_items_activity_change_password:
                new AlertDialog.Builder(RelateItemsActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要修改主密码吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent changeMasterPasswordIntent = new Intent(RelateItemsActivity.this, MasterPasswordChangeActivity.class);
                                startActivity(changeMasterPasswordIntent);
                                finish();
                            }
                        })
                        .show();
                break;
        }
    }

    /**
     * 产生菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_relative_items_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //返回主界面
            case R.id.menu_relative_items_activity_main:
                Intent mainIntent = new Intent(RelateItemsActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;

            //退出应用
            case R.id.menu_relative_items_activity_exit:
                QuitActivities.getInstance().exit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
