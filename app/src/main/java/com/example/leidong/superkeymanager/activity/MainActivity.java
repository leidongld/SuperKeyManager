package com.example.leidong.superkeymanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.leidong.superkeymanager.utils.AESClientServerUtil;
import com.example.leidong.superkeymanager.utils.RSAUtil;

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
    //进入数据库相关操作界面
    private Button bt_main_activity_relate_items;
    //进入设置按钮
    private Button bt_main_activity_settings;
    //帮助按钮
    private Button bt_main_activity_help;
    //离开按钮
    private Button bt_main_activity_exit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        initView();

        //判断sd卡是否存在的标志
        checkSDCard();

        //要求用户打开安全键盘
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivity(intent);

        //这里是RSA公钥保存的位置
        final SharedPreferences sharedPreferences1 = getSharedPreferences(Constants.RSA_SP_PARAMS, Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferences2 = getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
        String publicKey = sharedPreferences1.getString(Constants.RSA_SP_PUBLICKEY, "");
        String aesKey = sharedPreferences2.getString(Constants.AES_SP_AESKEY, "");

        //第一次与服务器建立连接时要求服务器产生RSA密钥对并将密钥对发送到客户端保存
        if(aesKey.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //AES的本地静态存储作为一个创新点
                    generateAESKey(sharedPreferences2);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    storeAESKey(sharedPreferences1, sharedPreferences2);
                }
            }).start();
        }
        else{
            Toast.makeText(MainActivity.this, "RSA公钥已存储于Android手机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存AES密钥
     * @param sharedPreferences1 RSA公钥相关
     * @param sharedPreferences2 AES私钥相关
     */
    private void storeAESKey(SharedPreferences sharedPreferences1, SharedPreferences sharedPreferences2) {
        //拿到本地AES密钥（验证无误）
        String aesKey = sharedPreferences2.getString(Constants.AES_SP_AESKEY, "");
        //拿到本地RSA公钥（验证无误）
        String rsaPublicKey = sharedPreferences1.getString(Constants.RSA_SP_PUBLICKEY, "");
        String encryptedAESKey = "";
        if(!aesKey.equals("") && !rsaPublicKey.equals("")) {//保证AES密钥和RSA公钥均正确拿到
            try {
                //得到经过RSA公钥加密的AES密钥，准备传给服务器
                encryptedAESKey = RSAUtil.encryptByPubKey(aesKey, rsaPublicKey);
                //传送经过RSA公钥加密的AES密钥密文到服务器进行保存
                Log.d(TAG, "<<<AESKey>>>" + encryptedAESKey);
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
        Log.d(TAG, "<<<>>>经过加密的AES密钥：" + encryptedAESKey);
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
     * @param sharedPreferences
     */
    private void generateAESKey(SharedPreferences sharedPreferences) {
        try {
            String aesKey = AESClientServerUtil.generateKeyString();
            Log.d(TAG, "<<<>>>generateAESKey()  " + aesKey);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.AES_SP_AESKEY, aesKey);
            editor.apply();
            //AES密钥已经正确产生
            Log.d(TAG, "<<<>>>AES密钥为：" + aesKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            //Toast.makeText(MainActivity.this, TAG + "  RSA密钥已由服务器产生", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast.makeText(MainActivity.this, TAG + "  RSA密钥未能成功生成", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, TAG + "  Error", Toast.LENGTH_SHORT).show();
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
     * 从服务器得到RSA公钥
     * @param sharedPreferences
     */
    private void obtainPublicKeyFromServer(final SharedPreferences sharedPreferences) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.RSA_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        saveRSAPublicKeyToSP(response, sharedPreferences);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error");
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
     * @param sharedPreferences
     */
    private void saveRSAPublicKeyToSP(String response, SharedPreferences sharedPreferences) {
        //RSA公钥传递正确
        Log.d(TAG, "<<<>>>RSA公钥为：" + response);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.RSA_SP_PUBLICKEY, response);
        editor.apply();
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
        bt_main_activity_relate_items = (Button)findViewById(R.id.bt_main_activity_relative_items);
        bt_main_activity_settings = (Button)findViewById(R.id.bt_main_activity_settings);
        bt_main_activity_help = (Button)findViewById(R.id.bt_main_activity_help);
        bt_main_activity_exit = (Button)findViewById(R.id.bt_main_activity_exit);
        bt_main_activity_login_database.setOnClickListener(this);
        bt_main_activity_relate_items.setOnClickListener(this);
        bt_main_activity_settings.setOnClickListener(this);
        bt_main_activity_help.setOnClickListener(this);
        bt_main_activity_exit.setOnClickListener(this);
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
            //数据条目相关操作
            case R.id.menu_main_activity_relative_items:
                Intent relativeItemsIntent = new Intent(MainActivity.this, RelateItemsActivity.class);
                startActivity(relativeItemsIntent);
                finish();
                break;
            //进入设置
            case R.id.menu_main_activity_settings:
                Toast.makeText(MainActivity.this, "settings", Toast.LENGTH_LONG).show();
                Intent intent_SettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent_SettingsActivity);
                finish();
                break;
            //帮助
            case R.id.menu_main_activity_help:
                Toast.makeText(MainActivity.this, "helps", Toast.LENGTH_LONG).show();
                Intent intentHelpActivity = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intentHelpActivity);
                finish();
                break;
            //离开
            case R.id.menu_main_activity_exit:
                Toast.makeText(MainActivity.this, "exit", Toast.LENGTH_LONG).show();
                QuitActivities.getInstance().exit();
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
            case R.id.bt_main_activity_relative_items:{
                Intent relateItemsIntent = new Intent(MainActivity.this, RelateItemsActivity.class);
                startActivity(relateItemsIntent);
                break;
            }
            //点击"进入设置"按钮
            case R.id.bt_main_activity_settings:{
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            }
            //点击“帮助”按钮
            case R.id.bt_main_activity_help:{
                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            }
            //点击“离开”按钮
            case R.id.bt_main_activity_exit:{
                QuitActivities.getInstance().exit();
                break;
            }
        }
    }
}
