package com.example.leidong.superkeymanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.example.leidong.superkeymanager.utils.UserDefault;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leidong on 2016/10/17.
 * 认证登录界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private static final String TAG = "LoginActivity";

    //输入主密码的文本框
    private EditText et_login_activity_masterpassword;
    //登录按钮
    private Button bt_login_activity_login;
    //指纹按钮
    private Button bt_login_activity_fingerlogin;
    //离开按钮
    private Button bt_login_activity_exit;
    //switch控件，用于显示密码
    private Switch switch_login_activity_first;
    //允许输错密码的最大次数,设为5次
    private int ERROR_COUNTS = 3;
    //密码输错次数标记
    private int count = ERROR_COUNTS;

    private String masterpasswordDigestFromMySQL = "";
    private String loginPassword = "";
    private String loginPasswordDigest = "";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        QuitActivities.getInstance().addActivity(this);
        //禁止截屏
//        Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        init();

        //switch控件的处理
        switchChange();

        //第一次与服务器建立连接时要求服务器产生RSA密钥对并将密钥对发送到客户端保存
        if(!UserDefault.getUserDefaultInstance(null).load(Constants.isHasRSAPublicKey, false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //请求服务器产生RSA密钥对
                    generateRSAKeys();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    obtainPublicKeyFromServer();
                }
            }).start();
        }
        else{
            Toast.makeText(LoginActivity.this, "RSA公钥已存储于Android手机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从服务器得到RSA公钥
     */
    private void obtainPublicKeyFromServer() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.RSA_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        saveRSAPublicKeyToSP(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "从服务器得到RSA公钥错误", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.GET_RSA_PUBLIC_KEY);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     * 将得到的RSA公钥存储在SHaredPrefereences中
     * @param response
     */
    private void saveRSAPublicKeyToSP(String response) {
        //RSA公钥传递正确
        Log.d(TAG, "<<<>>>RSA公钥为：" + response);
        UserDefault.getUserDefaultInstance(null).save(Constants.RSA_PUBLIC_KEY, response);
        UserDefault.getUserDefaultInstance(null).save(Constants.isHasRSAPublicKey, true);
    }

    /**
     * 第一次与服务器建立连接，要求服务器产生RSA密钥对
     */
    private void generateRSAKeys() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.RSA_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        if(response.equals("true")) {
                            Toast.makeText(LoginActivity.this, TAG + "  RSA密钥已由服务器产生", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, TAG + "  RSA密钥未能成功生成", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, TAG + "  Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error");
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.GENERATE_RSA_KEYS);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     * switch控件的处理
     */
    private void switchChange() {
        switch_login_activity_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_login_activity_masterpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    switch_login_activity_first.setText("密码可见");
                }
                else{
                    et_login_activity_masterpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    switch_login_activity_first.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 获取组件并初始化
     */
    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        et_login_activity_masterpassword = (EditText)findViewById(R.id.et_login_activity_masterpassword);
        et_login_activity_masterpassword.setOnTouchListener(LoginActivity.this);
        bt_login_activity_login = (Button)findViewById(R.id.bt_login_activity_login);
        bt_login_activity_fingerlogin = (Button)findViewById(R.id.bt_login_activity_fingerlogin);
        bt_login_activity_exit = (Button)findViewById(R.id.bt_login_activity_exit);
        switch_login_activity_first = (Switch)findViewById(R.id.switch_login_activity_first);
        bt_login_activity_fingerlogin.setOnClickListener(LoginActivity.this);
        bt_login_activity_login.setOnClickListener(LoginActivity.this);
        bt_login_activity_exit.setOnClickListener(LoginActivity.this);
    }

    /**
     * 按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //点击登录按钮
            case R.id.bt_login_activity_login:
                loginPassword = et_login_activity_masterpassword.getText().toString().trim();
                /**获取主密码**/
                Uri uri = Uri.parse(UserProvider.USER_URI.toString());
                Cursor cursor = getContentResolver().query(uri, new String[]{UserColumn.MASTERPASSWORD}, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                String masterPassword = cursor.getString(cursor.getColumnIndex(UserColumn.MASTERPASSWORD));
                //拿到程序的当前时间
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                long lastError = sharedPreferences.getLong("lastError", 0L);
                long recentTime = System.currentTimeMillis();
                if(recentTime - lastError > Constants.LOCK_TIME){
                    try {
                        if(BCrypt.checkpw(loginPassword, masterPassword)){
                            masterpasswordVerificationFromMySQL(new VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    if(BCrypt.checkpw(loginPassword, result)){
                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }                              }
                            });
                        }
                        else {
                            if (count == 1) {
                                et_login_activity_masterpassword.setText("");
                                Toast.makeText(LoginActivity.this, "您已经" + ERROR_COUNTS +"次认证失败！登录锁启动！", Toast.LENGTH_LONG).show();
                                count = ERROR_COUNTS;
                                lastError = System.currentTimeMillis();
                                SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putLong("lastError", lastError);
                                editor.apply();
                            }
                            else{
                                et_login_activity_masterpassword.setText("");
                                count--;
                                Toast.makeText(LoginActivity.this, "您还有" + count + "次登录机会！", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    et_login_activity_masterpassword.setText("");
                    Toast.makeText(LoginActivity.this, "登录界面锁定中，请等待！", Toast.LENGTH_LONG).show();
                }
                break;

            //点击指纹按钮
            case R.id.bt_login_activity_fingerlogin:
                //跳转到指纹认证界面
                Intent fingerIntent = new Intent(LoginActivity.this, FingerPrintActivity.class);
                startActivity(fingerIntent);

            //点击离开按钮
            case R.id.bt_login_activity_exit:
                QuitActivities.getInstance().exit();
                break;
        }
    }

    /**
     * MySQL验证主密码是否通过
     * @return
     */
    private String masterpasswordVerificationFromMySQL(final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MASTAER_PASSWORD_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        masterpasswordDigestFromMySQL = response;
                        callback.onSuccess(masterpasswordDigestFromMySQL);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.GET_MASTERPASSWORD);
                return map;
            }
        };
        requestQueue.add(request);
        return masterpasswordDigestFromMySQL;
    }

    /**
     * 点击对应的EditText弹出InnerKeyboard安全键盘
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            et_login_activity_masterpassword.setShowSoftInputOnFocus(true);
            switch (v.getId()){
                case R.id.et_login_activity_masterpassword:
                    new InnerKeyboardUtils(this, et_login_activity_masterpassword).showKeyBoard();
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    private interface VolleyCallback{
        void onSuccess(String result);
    }
}
