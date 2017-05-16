package com.example.leidong.superkeymanager.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.database.ItemsColumn;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.service.SecureKeyboard;
import com.example.leidong.superkeymanager.utils.AESUtil;

/**
 * Created by leidong on 2016/10/24.
 * 特定条目查看界面
 */

public class ItemViewActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "ItemViewActivity";

    private SharedPreferences sharedPreferences;
    private String aesKey;

    private TextView tv_item_view_name;
    private TextView tv_item_view_username;
    private TextView tv_item_view_password;
    private TextView tv_item_view_url;
    private TextView tv_item_view_pkg;
    private TextView tv_item_view_note;
    private Button bt_item_view_edit;
    private Button bt_item_view_weblogin;
    private Button bt_item_view_items;
    private Switch switch_item_view;

    //数据库操作相关
    private Uri mUri;
    private Cursor mCursor;

    //按钮点击广播类
    public ButtonBroadcastReceiver buttonBroadcastReceiver;

    public NotificationManager mNotificationManager;

    //通知栏按钮点击事件对应的ACTION
    public final static String ACTION_BUTTON = "com.example.leidong.action.ButtonClick";
    public final static String INTENT_BUTTONID_TAG = "ButtonId";

    //用户名 按钮点击 ID
    public final static int BUTTON_USERNAME_ID = 1;

    //密码 按钮点击 ID
    public final static int BUTTON_PASSWORD_ID = 2;

    NotificationCompat.Builder mBuilder;
    int notifyId = 100;

    //对应条目的用户名
    private String stringUsername;
    //对应条目的密码
    private String stringPassword;
    //用户名对应的密文
    private String encryptedUsername;
    //密码对应的密文
    private String encryptedPassword;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUri = getIntent().getData();
        ItemViewActivity.this.setContentView(R.layout.activity_item_view);
        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件
        initView();

        //获取AES密钥
        sharedPreferences = getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
        aesKey = sharedPreferences.getString("AESKey", "");

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //初始化通知栏
        initNotify();

        //初始化广播接收
        initButtonReceiver();

        //Switch变化的处理
        switchChange();

        /**************************/
        //获取并保存原始条目信息
        mCursor = getContentResolver().query(mUri, ItemsColumn.ITEM_INFOS, null, null, null);

        mCursor.moveToFirst();
        if(mCursor != null){
            //读取并显示条目信息
            mCursor.moveToFirst();
            try {
                tv_item_view_name.setText(mCursor.getString(ItemsColumn.ITEM_NAME_COLUMN));
                tv_item_view_username.setText(AESUtil.decrypt(AESUtil.AES_KEY,mCursor.getString(ItemsColumn.ITEM_USERNAME_COLUMN)));
                tv_item_view_password.setText(AESUtil.decrypt(AESUtil.AES_KEY, mCursor.getString(ItemsColumn.ITEM_PASSWORD_COLUMN)));
                tv_item_view_url.setText(AESUtil.decrypt(AESUtil.AES_KEY ,mCursor.getString(ItemsColumn.ITEM_URL_COLUMN)));
                tv_item_view_pkg.setText(AESUtil.decrypt(AESUtil.AES_KEY ,mCursor.getString(ItemsColumn.ITEM_PACKAGE_COLUMN)));
                tv_item_view_note.setText(AESUtil.decrypt(AESUtil.AES_KEY ,mCursor.getString(ItemsColumn.ITEM_NOTE_COLUMN)));
                //拿到用户名和密码的密文
                encryptedUsername = mCursor.getString(ItemsColumn.ITEM_USERNAME_COLUMN);
                encryptedPassword = mCursor.getString(ItemsColumn.ITEM_PASSWORD_COLUMN);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            setTitle("错误信息！");
        }
    }

    /**
     * 初始化按键广播接收器
     */
    private void initButtonReceiver() {
        buttonBroadcastReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(buttonBroadcastReceiver, intentFilter);
    }

    /**
     * 初始化通知栏
     */
    private void initNotify() {
        mBuilder = new NotificationCompat.Builder(ItemViewActivity.this);
        mBuilder.setContentTitle("用户名密码填充")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.notification_icon);
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性:
     * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    private PendingIntent getDefalutIntent(int flagAutoCancel) {
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flagAutoCancel);
        return pendingIntent;
    }

    /**
     * Switch变化的处理
     */
    private void switchChange() {
        switch_item_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv_item_view_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    switch_item_view.setText("密码可见");
                }
                else{
                    tv_item_view_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    switch_item_view.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 获取组件
     */
    private void initView() {
        tv_item_view_name = (TextView)findViewById(R.id.tv_item_view_name);
        tv_item_view_username = (TextView)findViewById(R.id.tv_item_view_username);
        tv_item_view_password = (TextView)findViewById(R.id.tv_item_view_password);
        tv_item_view_url = (TextView)findViewById(R.id.tv_item_view_url);
        tv_item_view_pkg = (TextView)findViewById(R.id.tv_item_view_pkg);
        tv_item_view_note = (TextView)findViewById(R.id.tv_item_view_note);
        bt_item_view_edit = (Button)findViewById(R.id.bt_item_view_edit);
        bt_item_view_weblogin = (Button)findViewById(R.id.bt_item_view_weblogin);
        bt_item_view_items = (Button)findViewById(R.id.bt_item_view_items);
        switch_item_view = (Switch)findViewById(R.id.switch_item_view);
        bt_item_view_edit.setOnClickListener(ItemViewActivity.this);
        bt_item_view_weblogin.setOnClickListener(ItemViewActivity.this);
        bt_item_view_items.setOnClickListener(ItemViewActivity.this);
    }

    /**
     * 对按钮的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //编辑条目按钮的点击
            case R.id.bt_item_view_edit:
                startActivity(new Intent(Intent.ACTION_EDIT, mUri));
                finish();
                break;

            //准备登陆按钮的点击
            case R.id.bt_item_view_weblogin:
                //显示通知栏
                showButtonNotify();

                //向SecureKeyboard中传递用户名和密码的密文
                Intent intent = new Intent(ItemViewActivity.this, SecureKeyboard.class);
                intent.putExtra("encrypted_username", encryptedUsername);
                intent.putExtra("encrypted_password", encryptedPassword);
                startService(intent);

                //提示用户切换键盘
                new Thread(){
                    public synchronized void run(){
                        changeKeyboard();
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //url已经过注册，则进行网页登录
                        if(tv_item_view_url.length() > 0){
                            String url = tv_item_view_url.getText().toString();
                            Uri uri = Uri.parse(url);
                            Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(urlIntent);
                        }
                        //url为空，则进行app登录
                        else{
                            if(tv_item_view_pkg.length() == 0){
                                Intent appListIntent = new Intent(ItemViewActivity.this, AppListActivity.class);
                                startActivity(appListIntent);
                            }
                            else{
                                PackageManager packageManager = getPackageManager();
                                Intent appIntent = new Intent();
                                appIntent = packageManager.getLaunchIntentForPackage(tv_item_view_pkg.toString());
                                startActivity(appIntent);
                            }
                        }
                    }
                }.start();
                break;

            //返回条目列表按钮的点击
            case R.id.bt_item_view_items:
                Intent itemsIntent = new Intent(ItemViewActivity.this, ItemsActivity.class);
                startActivity(itemsIntent);
                finish();
                break;
        }
    }

    /**
     * 切换键盘
     */
    private void changeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.showInputMethodPicker();
        }
        else{
            Toast.makeText(ItemViewActivity.this, "不存在可切换的输入法！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 产生菜单项
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item_view_activity, menu);
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
            //编辑条目
            case R.id.menu_item_view_activity_edit:
                startActivity(new Intent(Intent.ACTION_EDIT, mUri));
                finish();
                break;

            //准备登陆
            case R.id.menu_item_view_activity_weblogin:
                showButtonNotify();
                //提示用户切换键盘
                changeKeyboard();
                //如果url有内容就执行网页登录
                if(tv_item_view_url.length() > 0) {
                    //打开对应的url
                    String url = tv_item_view_url.getText().toString();
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else{//如果url无内容，就执行app登录

                }
                break;

            //删除条目
            case R.id.menu_item_view_activity_delete:
                deleteItem();
                finish();
                break;

            //返回条目界面
            case R.id.menu_item_view_activity_items:
                Intent itemsIntent = new Intent(ItemViewActivity.this, ItemsActivity.class);
                startActivity(itemsIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除条目信息
     */
    private void deleteItem(){
        if(mCursor != null){
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            setResult(RESULT_CANCELED);
        }
    }

    /**
     * 广播监听按钮点击事件的内部类
     */
    private class ButtonBroadcastReceiver extends BroadcastReceiver{
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(ACTION_BUTTON)) {
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_USERNAME_ID:
                        Log.d(TAG, "用户名");
                        stringUsername = tv_item_view_username.getText().toString();
                        ClipboardManager clipboardManager1 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData1 = ClipData.newPlainText("username", stringUsername);
                        clipboardManager1.setPrimaryClip(clipData1);
                        Toast.makeText(ItemViewActivity.this, "用户名已复制到剪贴板", Toast.LENGTH_SHORT).show();
                        break;
                    case BUTTON_PASSWORD_ID:
                        Log.d(TAG, "密码");
                        stringPassword = tv_item_view_password.getText().toString();
                        ClipboardManager clipboardManager2 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData2 = ClipData.newPlainText("password", stringPassword);
                        clipboardManager2.setPrimaryClip(clipData2);
                        Toast.makeText(ItemViewActivity.this, "密码已复制到剪贴板", Toast.LENGTH_SHORT)
                                .show();
                        //点击通知栏中密码按钮后，该通知栏自动被清除
                        mNotificationManager.cancel(200);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 显示带按钮的通知栏
     */
    public void showButtonNotify() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_button);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notification, "");
        mRemoteViews.setTextViewText(R.id.method, "");
        mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);

        //点击的事件处理
        Intent buttonIntent = new Intent(ACTION_BUTTON);
		/* 用户名按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_USERNAME_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_username = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_username, intent_username);
		/* 密码按钮 */
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PASSWORD_ID);
        PendingIntent intent_password = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_password, intent_password);

        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setTicker("用户名/密码")
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.notification_icon);

        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(200, notify);
    }
}
