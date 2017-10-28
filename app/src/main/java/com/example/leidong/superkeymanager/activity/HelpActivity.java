package com.example.leidong.superkeymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.quit.QuitActivities;

/**
 * Created by leidong on 2016/10/17.
 * 帮助界面
 */
public class HelpActivity extends AppCompatActivity{
    private static final String TAG = "HelpActivity";

    private TextView tv_help_activity_infos;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);

        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
//        Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        init();
    }

    /**
     * 获取组件并初始化
     */
    private void init() {
        tv_help_activity_infos = (TextView)findViewById(R.id.tv_help_activity_infos);

        tv_help_activity_infos.setText("" +
                "使用步骤\n" +
                "1.注册您的主密码\n" +
                "主密码将作为您登陆app的唯一钥匙，请务必牢记！\n\n" +
                "2.认证登录app\n" +
                "使用主密码进行app的认证登录。\n\n" +
                "3.管理密码条目\n" +
                "您可以创建相应的登陆条目，本app支持对各条目的增删改查等操作。\n\n" +
                "4.登陆条目\n" +
                "进入对应的条目查看界面，点击登录跳转按钮，即可完成登陆认证信息的抓取和填充。");
        tv_help_activity_infos.setTextSize(30);
    }

    /**
     * 产生菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_help_activity, menu);
        return true;
    }

    /**
     * 菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //返回主菜单
            case R.id.menu_help_activity_main:
                Intent mainIntent = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;

            //退出应用
            case R.id.menu_help_activity_exit:
                QuitActivities.getInstance().exit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
