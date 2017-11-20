package com.example.leidong.superkeymanager.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.AESClientServerUtils;
import com.example.leidong.superkeymanager.utils.PasswordFormatUtils;
import com.example.leidong.superkeymanager.utils.UserDefault;

import java.util.HashMap;
import java.util.Map;

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
    private String AESKey;
    private String s1;
    private String s2;
    private String s3;
    private String tempMasterpassword;
    private String newMasterpassword;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_master_password);
        QuitActivities.getInstance().addActivity(MasterPasswordChangeActivity.this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        AESKey = UserDefault.getUserDefaultInstance(null).load(Constants.AES_KEY, "");

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
                oldPasswordIsRight(s1, new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean isOldMasterpasswordCorrect) {
                        //旧密码验证正确
                        if(isOldMasterpasswordCorrect){
                            if(!s2.equals(s3)){
                                Toast.makeText(MasterPasswordChangeActivity.this, "两次输入的新密码不一致，请保证输入一致", Toast.LENGTH_LONG).show();
                            }
                            else if(s2.length() <= Constants.MIN_MASTER_PASSWORD_LENGTH){
                                Toast.makeText(MasterPasswordChangeActivity.this, "密码长度太短，请保证密码长度大于" + Constants.MIN_MASTER_PASSWORD_LENGTH + "个字符", Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(PasswordFormatUtils.isContainAll(s2)){
                                    changeMasterPassword(s2);
                                }
                                else{
                                    Toast.makeText(MasterPasswordChangeActivity.this, "新的密码不符合密码格式，请保证新的密码由数字与大小写字母共同组成", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        //旧密码验证错误
                        else{
                            Toast.makeText(MasterPasswordChangeActivity.this, "旧密码错误，请输入正确的旧密码", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 判断旧的密码是否正确
     * @param s1
     * @return
     */
    private void oldPasswordIsRight(String s1, final VolleyCallback callback) {
        try {
            tempMasterpassword = AESClientServerUtils.encrypt(AESKey, s1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MASTAER_PASSWORD_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            callback.onSuccess(true);
                        }
                        else{
                            callback.onSuccess(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MasterPasswordChangeActivity.this, "发生错误", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.CHECK_MASTERPASSWORD);
                map.put(Constants.ENCRYPTED_MASTERPASSWORD, tempMasterpassword);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     *
     * @param s
     */
    private void changeMasterPassword(String s) {
        try {
            newMasterpassword = AESClientServerUtils.encrypt(AESKey, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MASTAER_PASSWORD_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setTitle("提示");
                            builder.setMessage("主密码已经修改成功");
                            builder.setPositiveButton("确定", null);
                            builder.create().show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.UPDATE_MASTERPASSWORD);
                map.put(Constants.ENCRYPTED_MASTERPASSWORD, newMasterpassword);
                return map;
            }
        };
        requestQueue.add(request);
    }

    interface VolleyCallback{
        void onSuccess(boolean isOldMasterpasswordCorrect);
    }
}
