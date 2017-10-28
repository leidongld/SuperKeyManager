package com.example.leidong.superkeymanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.quit.QuitActivities;

/**
 * Created by leidong
 * on 2016/10/24
 */

public class MasterPasswordChangeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MasterPasswordChangeActivity";

    private EditText oldpassword;
    private EditText newpassword;
    private EditText confirmpassword;
    private Button bt_ok;

    private String s1;
    private String s2;
    private String s3;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_master_password);
        QuitActivities.getInstance().addActivity(MasterPasswordChangeActivity.this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件
        initViews();
        
        initActions();
    }

    private void initActions() {
        bt_ok.setOnClickListener(this);
    }

    /**
     * 获取组件
     */
    private void initViews() {
        oldpassword = (EditText) findViewById(R.id.oldPassword);
        newpassword = (EditText) findViewById(R.id.newPassword);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        bt_ok = (Button) findViewById(R.id.bt_ok);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_ok:
                s1 = oldpassword.getText().toString().trim();
                s2 = newpassword.getText().toString().trim();
                s3 = confirmpassword.getText().toString().trim();
                changeMasterPassword();
                break;
            default:
                break;
        }
    }

    private void changeMasterPassword() {
    }
}
