package com.example.leidong.superkeymanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.leidong.superkeymanager.MyApplication;
import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.beans.ItemBean;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.gen.ItemBeanDao;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.service.SecureKeyboard;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;

/**
 * Created by leidong on 2016/10/24.
 * 特定条目查看界面
 */
public class ItemViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ItemViewActivity";

    private ItemBeanDao itemBeanDao;
    private long id;

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
        setContentView(R.layout.activity_item_view);
        QuitActivities.getInstance().addActivity(this);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件
        initWidgets();

        //Switch变化的处理
        switchChange();

        SharedPreferences sp = getSharedPreferences(Constants.ITEM_SP_PARAMS, Context.MODE_PRIVATE);
        id = sp.getLong(Constants.ITEM_SP_ID, -1);

        itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        ItemBean itemBean = GreenDaoUtils.queryItemBeanById(id);

        String name = itemBean.getItemItemname();
        String username = itemBean.getItemUsername();
        String password = itemBean.getItemPassword();
        String url = itemBean.getItemUrl();
        String pkg = itemBean.getItemPackagename();
        String note = itemBean.getItemNote();

        tv_item_view_name.setText(name);
        tv_item_view_username.setText(username);
        tv_item_view_password.setText(password);
        tv_item_view_url.setText(url);
        tv_item_view_pkg.setText(pkg);
        tv_item_view_note.setText(note);
    }

    /**
     * Switch变化的处理
     */
    private void switchChange() {
        switch_item_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_item_view_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    switch_item_view.setText("密码可见");
                } else {
                    tv_item_view_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    switch_item_view.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 获取组件
     */
    private void initWidgets() {
        tv_item_view_name = (TextView) findViewById(R.id.tv_item_view_name);
        tv_item_view_username = (TextView) findViewById(R.id.tv_item_view_username);
        tv_item_view_password = (TextView) findViewById(R.id.tv_item_view_password);
        tv_item_view_url = (TextView) findViewById(R.id.tv_item_view_url);
        tv_item_view_pkg = (TextView) findViewById(R.id.tv_item_view_pkg);
        tv_item_view_note = (TextView) findViewById(R.id.tv_item_view_note);
        bt_item_view_edit = (Button) findViewById(R.id.bt_item_view_edit);
        bt_item_view_weblogin = (Button) findViewById(R.id.bt_item_view_weblogin);
        bt_item_view_items = (Button) findViewById(R.id.bt_item_view_items);
        switch_item_view = (Switch) findViewById(R.id.switch_item_view);
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
        switch (v.getId()) {
            //编辑条目按钮的点击
            case R.id.bt_item_view_edit:
                Intent intent1 = new Intent(ItemViewActivity.this, ItemEditActivity.class);
                startActivity(intent1);
                finish();
                break;

            //准备登陆按钮的点击
            case R.id.bt_item_view_weblogin:
                //向SecureKeyboard中传递用户名和密码的密文
                Intent intent = new Intent(ItemViewActivity.this, SecureKeyboard.class);
                intent.putExtra("encrypted_username", encryptedUsername);
                intent.putExtra("encrypted_password", encryptedPassword);
                startService(intent);

                //提示用户切换键盘
                new Thread() {
                    public synchronized void run() {
                        changeKeyboard();
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //url已经过注册，则进行网页登录
                        if (tv_item_view_url.length() > 0) {
                            String url = tv_item_view_url.getText().toString();
                            Uri uri = Uri.parse(url);
                            Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(urlIntent);
                        }
                        //url为空，则进行app登录
                        else {
                            if (tv_item_view_pkg.length() == 0) {
                                Intent appListIntent = new Intent(ItemViewActivity.this, AppListActivity.class);
                                startActivity(appListIntent);
                            } else {
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
        if (inputMethodManager != null) {
            inputMethodManager.showInputMethodPicker();
        } else {
            Toast.makeText(ItemViewActivity.this, "不存在可切换的输入法！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 产生菜单项
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_view_activity, menu);
        return true;
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //编辑条目
            case R.id.menu_item_view_activity_edit:
                Intent intent1 = new Intent(ItemViewActivity.this, ItemEditActivity.class);
                startActivity(intent1);
                finish();
                break;

            //准备登陆
            case R.id.menu_item_view_activity_weblogin:
                //提示用户切换键盘
                changeKeyboard();
                //如果url有内容就执行网页登录
                if (tv_item_view_url.length() > 0) {
                    //打开对应的url
                    String url = tv_item_view_url.getText().toString();
                    Uri uri = Uri.parse(url);
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent2);
                } else {//如果url无内容，就执行app登录

                }
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
}
