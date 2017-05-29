package com.example.leidong.superkeymanager.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import com.example.leidong.superkeymanager.database.ItemsColumn;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.AESUtil;
import com.example.leidong.superkeymanager.utils.InnerKeyboardUtil;

/**
 * Created by leidong on 2016/10/24.
 * 特定条目编辑界面
 */

public class ItemEditActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private static final String TAG = "ItemEditActivity";

    private SharedPreferences sharedPreferences = null;
    private String aesKey;

    //标志常量位，用于标识当前是新建状态还是编辑状态
    private static final int STATE_INSERT = 1;//新建状态为1
    private static final int STATE_EDIT = 2;//编辑状态为2

    //当前处于新建状态还是编辑状态的标志位
    private int mState;
    private Cursor mCursor;
    private Uri mUri;

    private EditText et_item_edit_name;
    private EditText et_item_edit_username;
    private EditText et_item_edit_password;
    private EditText et_item_edit_url;
    private EditText et_item_edit_pkg;
    private EditText et_item_edit_note;

    private Button bt_item_edit_finish;
    private Button bt_item_edit_view;

    private Switch switch_item_edit;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        QuitActivities.getInstance().addActivity(this);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        //根据action的不同进行不同的操作
        switchAction(intent, action);

        setContentView(R.layout.activity_item_edit);

        //获取AES密钥
        sharedPreferences = getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
        aesKey = sharedPreferences.getString(Constants.AES_SP_AESKEY, "");

        //获取组件
        initView();

        //Switch的改变
        switchChange();

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获得并保存原始条目的信息
        mCursor = getContentResolver().query(mUri, ItemsColumn.ITEM_INFOS, null, null, null);
        mCursor.moveToFirst();
        if(mCursor != null){
            //读取并显示条目信息
            mCursor.moveToFirst();
            if(mState == STATE_EDIT){
                setTitle("编辑条目");
            }
            else if(mState == STATE_INSERT){
                setTitle("添加条目");
            }
            try {
                //从本地数据库中获得的条目参数
                int id = mCursor.getInt(ItemsColumn.ITEM_ID_COLUMN);
                String name = mCursor.getString(ItemsColumn.ITEM_NAME_COLUMN);
                String username = AESUtil.decrypt(AESUtil.AES_KEY, mCursor.getString(ItemsColumn.ITEM_USERNAME_COLUMN));
                String password = AESUtil.decrypt(AESUtil.AES_KEY, mCursor.getString(ItemsColumn.ITEM_PASSWORD_COLUMN));
                String url = AESUtil.decrypt(AESUtil.AES_KEY, mCursor.getString(ItemsColumn.ITEM_URL_COLUMN));
                String note = AESUtil.decrypt(AESUtil.AES_KEY, mCursor.getString(ItemsColumn.ITEM_PACKAGE_COLUMN));
                et_item_edit_name.setText(name);
                et_item_edit_username.setText(username);
                et_item_edit_password.setText(password);
                et_item_edit_url.setText(url);
                et_item_edit_pkg.setText(note);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            setTitle("错误信息");
        }
    }

    /**
     * 根据Action的不同进行不同的操作
     * @param intent
     * @param action
     */
    private void switchAction(Intent intent, String action) {
        if(Intent.ACTION_EDIT.equals(action)){
            //编辑条目
            mState = STATE_EDIT;
            mUri = intent.getData();
        }
        else if(Intent.ACTION_INSERT.equals(action)){
            //添加条目
            mState = STATE_INSERT;
            mUri = getContentResolver().insert(intent.getData(), null);
            if(mUri == null){
                finish();
                return ;
            }
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
        }
        //其他情况，退出
        else {
            finish();
            return ;
        }
    }

    /**
     * 监控Switch状态的改变
     */
    private void switchChange() {
        switch_item_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_item_edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    switch_item_edit.setText("密码可见");
                }
                else{
                    et_item_edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    switch_item_edit.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 获取组件
     */
    private void initView() {
        et_item_edit_name = (EditText)findViewById(R.id.et_item_edit_name);
        et_item_edit_username = (EditText)findViewById(R.id.et_item_edit_username);
        et_item_edit_password = (EditText)findViewById(R.id.et_item_edit_password);
        et_item_edit_url = (EditText)findViewById(R.id.et_item_edit_url);
        et_item_edit_pkg = (EditText)findViewById(R.id.et_item_edit_pkg);
        et_item_edit_note = (EditText)findViewById(R.id.et_item_edit_note);
        et_item_edit_name.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_username.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_password.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_url.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_pkg.setOnTouchListener(ItemEditActivity.this);
        et_item_edit_note.setOnTouchListener(ItemEditActivity.this);
        bt_item_edit_finish = (Button)findViewById(R.id.bt_item_edit_finish);
        bt_item_edit_view = (Button)findViewById(R.id.bt_item_edit_view);
        switch_item_edit = (Switch)findViewById(R.id.switch_item_edit);
        bt_item_edit_finish.setOnClickListener(ItemEditActivity.this);
        bt_item_edit_view.setOnClickListener(ItemEditActivity.this);
    }

    /**
     * 对按钮的点击监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //对完成修改按钮的监控
            case R.id.bt_item_edit_finish:
                String text = et_item_edit_name.getText().toString().trim();
                if (text.length() == 0){
                    //如果没有输入，则将原来的记录删除
                    setResult(RESULT_CANCELED);
                    deleteItem();
                    finish();
                }
                else{
                    //更新数据
                    updateItem();
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
     * 更新变更的信息
     */
    private void updateItem() {
        if(mCursor != null){
            mCursor.close();
            mCursor = null;
            ContentValues values = new ContentValues();
            try {
                values.put(ItemsColumn.ITEM_NAME, et_item_edit_name.getText().toString().trim());
                values.put(ItemsColumn.ITEM_USERNAME, AESUtil.encrypt(AESUtil.AES_KEY, et_item_edit_username.getText().toString().trim()));
                values.put(ItemsColumn.ITEM_PASSWORD, AESUtil.encrypt(AESUtil.AES_KEY, et_item_edit_password.getText().toString().trim()));
                values.put(ItemsColumn.ITEM_URL, AESUtil.encrypt(AESUtil.AES_KEY, et_item_edit_url.getText().toString().trim()));
                values.put(ItemsColumn.ITEM_PACKAGE, AESUtil.encrypt(AESUtil.AES_KEY, et_item_edit_pkg.getText().toString().trim()));
                values.put(ItemsColumn.ITEM_NOTE, AESUtil.encrypt(AESUtil.AES_KEY, et_item_edit_note.getText().toString().trim()));
            }catch (Exception e){
                e.printStackTrace();
            }
            //更新数据
            getContentResolver().update(mUri, values, null, null);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * 生成菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item_edit_activity, menu);
        return true;
    }

    /**
     * 菜单点击操作
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //完成修改的点击
            case R.id.menu_item_edit_activity_finish:
                break;

            //删除该条目
            case R.id.menu_item_edit_activity_delete:
                deleteItem();
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
     * 删除条目信息
     */
    private void deleteItem() {
        if(mCursor != null){
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            et_item_edit_name.setText("");
        }
    }

    /**
     * EditText的触摸触发安全键盘的弹出
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            et_item_edit_name.setShowSoftInputOnFocus(true);
            et_item_edit_username.setShowSoftInputOnFocus(true);
            et_item_edit_password.setShowSoftInputOnFocus(true);
            et_item_edit_url.setShowSoftInputOnFocus(true);
            et_item_edit_pkg.setShowSoftInputOnFocus(true);
            et_item_edit_note.setShowSoftInputOnFocus(true);
            switch (v.getId()){
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
