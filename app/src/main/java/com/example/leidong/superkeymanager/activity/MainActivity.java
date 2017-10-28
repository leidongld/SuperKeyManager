package com.example.leidong.superkeymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.example.leidong.superkeymanager.utils.RSAUtils;
import com.example.leidong.superkeymanager.utils.UserDefault;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leidong on 2016/10/17.
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    //进入数据库按钮
    private Button bt_main_activity_login_database;
    //进入设置按钮
    private Button bt_main_activity_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
//        Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        initView();

        //判断sd卡是否存在的标志
        checkSDCard();

        //要求用户打开安全键盘
        if(!UserDefault.getUserDefaultInstance(null).load(Constants.isHasIME, false)){
            UserDefault.getUserDefaultInstance(null).save(Constants.isHasIME, true);
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //本地生成AES密钥并且将其发送到服务器端
        if(!UserDefault.getUserDefaultInstance(null).isHasAESKey()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //AES的本地静态存储作为一个创新点
                    generateAESKey();
                    storeAESKey();
                }
            }).start();
        }
        else{
            //这里是RSA公钥保存的位置
            String publicKey = UserDefault.getUserDefaultInstance(null).load(Constants.RSA_PUBLIC_KEY, "");
            String AESKey = UserDefault.getUserDefaultInstance(null).load(Constants.AES_KEY, "");
            Toast.makeText(MainActivity.this, "RSA公钥已存储于Android手机" + AESKey, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存AES密钥
     */
    private void storeAESKey() {
        //拿到本地AES密钥（验证无误）
        String AESKey = UserDefault.getUserDefaultInstance(null).load(Constants.AES_KEY, "");
        //拿到本地RSA公钥（验证无误）
        String RSAPublicKey = UserDefault.getUserDefaultInstance(null).load(Constants.RSA_PUBLIC_KEY, "");
        String encryptedAESKey;
        if(!AESKey.equals("") && !RSAPublicKey.equals("")) {//保证AES密钥和RSA公钥均正确拿到
            try {
                //得到经过RSA公钥加密的AES密钥，准备传给服务器
                encryptedAESKey = RSAUtils.encryptByPubKey(AESKey, RSAPublicKey);
                //传送经过RSA公钥加密的AES密钥密文到服务器进行保存
                passEncryptedAESKeyToServer(encryptedAESKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 传送经过RSA公钥加密的AES密钥密文到服务器进行保存
     * @param encryptedAESKey
     */
    private void passEncryptedAESKeyToServer(final String encryptedAESKey) {
        //已验证
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.RSA_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, TAG + "  AES密钥已经传送完毕", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, TAG + "  AES密钥已经传送失败", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put(Constants.MYSQL_COMMAND, Constants.ADD_AES);
                map.put(Constants.ENCRYPTED_AESKEY, encryptedAESKey);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     * 生成ASE密钥
     */
    private void generateAESKey() {
        try {
            String AESKey = AESClientServerUtils.generateKeyString();
            UserDefault.getUserDefaultInstance(null).save(Constants.AES_KEY, AESKey);
            UserDefault.getUserDefaultInstance(null).save(Constants.isHasAESKey, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断SD卡是否存在
     */
    private void checkSDCard() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if(!sdCardExist){
            //Toast.makeText(MainActivity.this, "SD card is not founded.", Toast.LENGTH_LONG).show();
        }
        else{
            //Toast.makeText(MainActivity.this, "SD card is founded.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取组件并初始化
     */
    private void initView() {
        bt_main_activity_login_database = (Button)findViewById(R.id.bt_main_activity_login_database);
        bt_main_activity_settings = (Button)findViewById(R.id.bt_main_activity_settings);
        bt_main_activity_login_database.setOnClickListener(this);
        bt_main_activity_settings.setOnClickListener(this);
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //进入数据库
            case R.id.menu_main_activity_items:
                Intent itemsIntent = new Intent(MainActivity.this, ItemsActivity.class);
                startActivity(itemsIntent);
                finish();
                break;
            //进入设置
            case R.id.menu_main_activity_settings:
                Toast.makeText(MainActivity.this, "settings", Toast.LENGTH_LONG).show();
                Intent intent_SettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent_SettingsActivity);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //点击“进入数据库”按钮
            case R.id.bt_main_activity_login_database:{
                Intent itemsIntent = new Intent(MainActivity.this, ItemsActivity.class);
                startActivity(itemsIntent);
                break;
            }
            //点击"进入设置"按钮
            case R.id.bt_main_activity_settings:{
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            }
        }
    }
}
