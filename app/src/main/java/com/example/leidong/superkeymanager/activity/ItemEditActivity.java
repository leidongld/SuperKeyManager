package com.example.leidong.superkeymanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;
import com.example.leidong.superkeymanager.utils.InnerKeyboardUtil;

/**
 * Created by leidong on 2016/10/24.
 * 特定条目编辑界面
 */

public class ItemEditActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private static final String TAG = "ItemEditActivity";

    private EditText et_item_edit_name;
    private EditText et_item_edit_username;
    private EditText et_item_edit_password;
    private EditText et_item_edit_url;
    private EditText et_item_edit_pkg;
    private EditText et_item_edit_note;

    private Button bt_item_edit_finish;
    private Button bt_item_edit_view;

    private Switch switch_item_edit;

    private long id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        QuitActivities.getInstance().addActivity(this);

        //获取组件
        initWidgets();

        //Switch的改变
        switchChange();

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        SharedPreferences sp = getSharedPreferences(Constants.ITEM_SP_PARAMS, Context.MODE_PRIVATE);
        id = sp.getLong(Constants.ITEM_SP_ID, (long) -1);
    }

    /**
     * 获取组件
     */
    private void initWidgets() {
        et_item_edit_name = (EditText) findViewById(R.id.et_item_edit_name);
        et_item_edit_username = (EditText) findViewById(R.id.et_item_edit_username);
        et_item_edit_password = (EditText) findViewById(R.id.et_item_edit_password);
        et_item_edit_url = (EditText) findViewById(R.id.et_item_edit_url);
        et_item_edit_pkg = (EditText) findViewById(R.id.et_item_edit_pkg);
        et_item_edit_note = (EditText) findViewById(R.id.et_item_edit_note);
        et_item_edit_name.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_username.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_password.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_url.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_pkg.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_note.setOnTouchListener(ItemEditActivity.this);
        bt_item_edit_finish = (Button) findViewById(R.id.bt_item_edit_finish);
        bt_item_edit_view = (Button) findViewById(R.id.bt_item_edit_view);
        switch_item_edit = (Switch) findViewById(R.id.switch_item_edit);
        bt_item_edit_finish.setOnClickListener(ItemEditActivity.this);
        bt_item_edit_view.setOnClickListener(ItemEditActivity.this);
    }

    /**
     * 生成菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_edit_activity, menu);
        return true;
    }

    /**
     * 监控Switch状态的改变
     */
    private void switchChange() {
        switch_item_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_item_edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    switch_item_edit.setText("密码可见");
                } else {
                    et_item_edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    switch_item_edit.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 对按钮的点击监听
     * @param v 视图
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //对完成修改按钮的监控
            case R.id.bt_item_edit_finish:
                String newName = et_item_edit_name.getText().toString().trim();
                String newUsername = et_item_edit_username.getText().toString().trim();
                String newPassword = et_item_edit_password.getText().toString().trim();
                String newUrl = et_item_edit_url.getText().toString().trim();
                String newPkg = et_item_edit_pkg.getText().toString().trim();
                String newNote = et_item_edit_note.getText().toString().trim();
                boolean flag = isParamsNull(newName, newUsername, newPassword, newUrl, newPkg, newNote);
                if (id != (long)-1) {
                    if (flag) {
                        finish();
                    } else {
                        //更新数据
                        GreenDaoUtils.updateItem(id, newName, newUsername, newPassword, newUrl, newPkg, newNote);
                    }
                } else {
                    if (flag) {
                        finish();
                    } else {
                        GreenDaoUtils.insertItem(newName, newUsername, newPassword, newUrl, newPkg, newNote);
                        long newId = GreenDaoUtils.obtainNewestId();
                        SharedPreferences sp = getSharedPreferences(Constants.ITEM_SP_PARAMS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putLong(Constants.ITEM_SP_ID, newId);
                        editor.apply();
                    }
                }
                Toast.makeText(ItemEditActivity.this, "已完成条目的修改", Toast.LENGTH_LONG).show();
                break;

            //对返回条目查看按钮的监控
            case R.id.bt_item_edit_view:
                Intent viewIntent = new Intent(ItemEditActivity.this, ItemViewActivity.class);
                startActivity(viewIntent);
                finish();
                break;
        }
    }

    /**
     * 参数是否为空
     * @param newName 条目名
     * @param newUsername 用户名
     * @param newPassword 密码
     * @param newUrl URL
     * @param newPkg 包名
     * @param newNote 备注
     * @return 是否为空的标志
     */
    private boolean isParamsNull(String newName, String newUsername, String newPassword, String newUrl, String newPkg, String newNote) {
        if(newName.length() == 0 || newUsername.length() == 0 || newPassword.length() == 0){
            return true;
        }
        else if(newUrl.length() == 0 && newPkg.length() == 0){
            return true;
        }
        return false;
    }

    /**
     * 菜单点击操作
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //完成修改的点击
            case R.id.menu_item_edit_activity_finish:
                break;

            //删除该条目
            case R.id.menu_item_edit_activity_delete:
                GreenDaoUtils.deleteItem(id);
                finish();
                break;

            //返回查看界面的点击
            case R.id.menu_item_edit_activity_view:
                Intent viewIntent = new Intent(ItemEditActivity.this, ItemViewActivity.class);
                startActivity(viewIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * EditText的触摸触发安全键盘的弹出
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            et_item_edit_name.setShowSoftInputOnFocus(true);
            et_item_edit_username.setShowSoftInputOnFocus(true);
            et_item_edit_password.setShowSoftInputOnFocus(true);
            et_item_edit_url.setShowSoftInputOnFocus(true);
            et_item_edit_pkg.setShowSoftInputOnFocus(true);
            et_item_edit_note.setShowSoftInputOnFocus(true);
            switch (v.getId()) {
                case R.id.et_item_edit_name:
                    new InnerKeyboardUtil(this, et_item_edit_name).showKeyBoard();
                    break;
                case R.id.et_item_edit_username:
                    new InnerKeyboardUtil(this, et_item_edit_username).showKeyBoard();
                    break;
                case R.id.et_item_edit_password:
                    new InnerKeyboardUtil(this, et_item_edit_password).showKeyBoard();
                    break;
                case R.id.et_item_edit_url:
                    new InnerKeyboardUtil(this, et_item_edit_url).showKeyBoard();
                    break;
                case R.id.et_item_edit_pkg:
                    new InnerKeyboardUtil(this, et_item_edit_pkg).showKeyBoard();
                    break;
                case R.id.et_item_edit_note:
                    new InnerKeyboardUtil(this, et_item_edit_note).showKeyBoard();
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }
}

