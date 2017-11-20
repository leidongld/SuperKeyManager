package com.example.leidong.superkeymanager.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.database.UserColumn;
import com.example.leidong.superkeymanager.database.UserProvider;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.BCrypt;
import com.example.leidong.superkeymanager.utils.InnerKeyboardUtils;
import com.example.leidong.superkeymanager.utils.PasswordFormatUtils;
import com.example.leidong.superkeymanager.utils.UserDefault;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leidong on 2016/10/17.
 * 用户注册界面
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private static final String TAG = "RegisterActivity";

    //主密码输入框
    private EditText et_register_activity_masterpassword;
    //主密码确认输入框
    private EditText et_register_activity_confirm_masterpassword;
    //switch
    private Switch switch_register_activity_first;
    //注册提交按钮
    private Button bt_register_activity_register;
    //离开按钮
    private Button bt_register_activity_exit;
    //主密码的长度最短限制
    private static final int KEY_LENGTH = Constants.MIN_MASTER_PASSWORD_LENGTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(UserDefault.getUserDefaultInstance(null).load(Constants.isHasMasterPassword, false)){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_register);
        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
//        Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        initView();

        //Switch操作
        switchChange();
    }

    /**
     * Switch改变的操作
     */
    private void switchChange() {
        switch_register_activity_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_register_activity_masterpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_register_activity_confirm_masterpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    switch_register_activity_first.setText("密码可见");
                }
                else{
                    et_register_activity_masterpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_register_activity_confirm_masterpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    switch_register_activity_first.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 获取组件并初始化
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        et_register_activity_masterpassword = (EditText)findViewById(R.id.et_register_activity_masterpassword);
        et_register_activity_confirm_masterpassword = (EditText)findViewById(R.id.et_register_activity_confirm_masterpassword);
        et_register_activity_masterpassword.setOnTouchListener(RegisterActivity.this);
        et_register_activity_confirm_masterpassword.setOnTouchListener(RegisterActivity.this);
        bt_register_activity_register = (Button)findViewById(R.id.bt_register_activity_register);
        bt_register_activity_exit = (Button)findViewById(R.id.bt_register_activity_exit);
        switch_register_activity_first = (Switch)findViewById(R.id.switch_register_activity_first);
        bt_register_activity_register.setOnClickListener(RegisterActivity.this);
        bt_register_activity_exit.setOnClickListener(RegisterActivity.this);
    }

    /**
     * 按钮的点击事件
     * @param v 视图
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //点击提交注册按钮
            case R.id.bt_register_activity_register:
                UserDefault.getUserDefaultInstance(null).save(Constants.isHasMasterPassword, true);
                final String masterPasswd = et_register_activity_masterpassword.getText().toString().trim();
                String conMasterPasswd = et_register_activity_confirm_masterpassword.getText().toString().trim();
                    //两次输入的主密码不相同或长度不够
                    if (!masterPasswd.equals(conMasterPasswd)//两次不相等
                            || masterPasswd.length() < KEY_LENGTH//长度不够，小于8
                            || conMasterPasswd.length() < KEY_LENGTH//长度不够，小于8
                            || !PasswordFormatUtils.isContainAll(masterPasswd))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("提示");
                        builder.setIcon(R.drawable.warning);
                        builder.setMessage("密码格式不正确！\n请检查两次输入的密码是否一致，并保证密码大于8位且包含大小写字母及数字！");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                et_register_activity_masterpassword.setText(null);
                                et_register_activity_confirm_masterpassword.setText(null);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.create().show();
                    }
                    //两次输入的主密码相同
                    else {
                        UserDefault.getUserDefaultInstance(null).setIsHasMasterPassword(true);
                        addMasterPasswordToMySQL(masterPasswd);
                        Uri uri = Uri.parse(UserProvider.USER_URI.toString());
                        Cursor cursor = getContentResolver().query(uri, UserColumn.MASTERPASSWORD_INFOS, null, null, null);
                        cursor.moveToFirst();
                        ContentValues values = new ContentValues();
                        values.put(UserColumn._ID, 1);
                        try {
                            values.put(UserColumn.MASTERPASSWORD, BCrypt.hashpw(masterPasswd, BCrypt.gensalt()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getContentResolver().insert(uri, values);

                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                break;

            //点击离开按钮
            case R.id.bt_register_activity_exit:
                QuitActivities.getInstance().exit();
                break;
        }
    }

    /**
     * 添加主密码到MySQL数据库中
     * @param masterPasswd 主密码
     */
    private void addMasterPasswordToMySQL(final String masterPasswd) {
        //主密码注册到服务器上
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.MASTAER_PASSWORD_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this, "主密码已经传入服务器", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, TAG + "  Error", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.REGISTER_MASTERPASSWORD);
                map.put(Constants.master_password_id, "1");
                //传输的安全隐患
                map.put(Constants.master_password, masterPasswd);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * 点击Activity中的EditText时弹出自定义的安全键盘
     * @param v 视图
     * @param event 事件
     * @return 返回值
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            et_register_activity_masterpassword.setShowSoftInputOnFocus(true);
            et_register_activity_confirm_masterpassword.setShowSoftInputOnFocus(true);
            et_register_activity_masterpassword.setInputType(InputType.TYPE_NULL);
            et_register_activity_confirm_masterpassword.setInputType(InputType.TYPE_NULL);
            switch (v.getId()){
                case R.id.et_register_activity_masterpassword:
                    new InnerKeyboardUtils(this, et_register_activity_masterpassword).showKeyBoard();
                    break;
                case R.id.et_register_activity_confirm_masterpassword:
                    new InnerKeyboardUtils(this, et_register_activity_confirm_masterpassword).showKeyBoard();
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }
}
