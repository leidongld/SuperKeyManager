package com.example.leidong.superkeymanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.leidong.superkeymanager.utils.AESClientServerUtils;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;
import com.example.leidong.superkeymanager.utils.InnerKeyboardUtils;
import com.example.leidong.superkeymanager.utils.UserDefault;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leidong on 2017/6/8
 */

public class ItemAddActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {
    private static final String TAG = "ItemAddActivity";

    private EditText et_itemName, et_username, et_password, et_url, et_pkg, et_note;
    private Button button1, button2;
    private ImageView pkglist;
    private Switch mSwitch;

    private String AESKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        AESKey = UserDefault.getUserDefaultInstance(null).load(Constants.AES_KEY, "");

        initWidgets();
        initActions();

        switchChange();


        Toast.makeText(ItemAddActivity.this, AESKey, Toast.LENGTH_LONG).show();
    }

    /**
     * 初始化动作
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initActions() {
        et_itemName.setOnTouchListener(this);
        et_username.setOnTouchListener(this);
        et_password.setOnTouchListener(this);
        et_url.setOnTouchListener(this);
        et_pkg.setOnTouchListener(this);
        et_note.setOnTouchListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        et_itemName = (EditText) findViewById(R.id.et_item_edit_name);
        et_username = (EditText) findViewById(R.id.et_item_edit_username);
        et_password = (EditText) findViewById(R.id.et_item_edit_password);
        et_url = (EditText) findViewById(R.id.et_item_edit_url);
        et_pkg = (EditText) findViewById(R.id.et_item_edit_pkg);
        et_note = (EditText) findViewById(R.id.et_item_edit_note);
        button1 = (Button) findViewById(R.id.bt_item_edit_finish);
        button2 = (Button) findViewById(R.id.bt_item_edit_view);
        pkglist = (ImageView) findViewById(R.id.pkglist);
        pkglist.setOnClickListener(this);
        mSwitch = (Switch) findViewById(R.id.switch_item_edit);
    }

    /**
     * 生成菜单
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
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mSwitch.setText("密码可见");
                } else {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mSwitch.setText("密码不可见");
                }
            }
        });
    }

    /**
     * 按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_item_edit_finish:
                String itemName0 = et_itemName.getText().toString().trim();
                String username0 = et_username.getText().toString().trim();
                String password0 = et_password.getText().toString().trim();
                String url0 = et_url.getText().toString().trim();
                String pkg0 = et_pkg.getText().toString().trim();
                String note0 = et_note.getText().toString().trim();
                if (!isParamsLegal(itemName0, username0, password0, url0, pkg0, note0)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("警告");
                    builder.setMessage("请保证输入正确");
                    builder.setPositiveButton("重新输入", null);
                    builder.create().show();
                } else {
                    String itemName = AESClientServerUtils.encrypt(et_itemName.getText().toString().trim(), AESKey);
                    String username = AESClientServerUtils.encrypt(et_username.getText().toString().trim(), AESKey);
                    String password = AESClientServerUtils.encrypt(et_password.getText().toString().trim(), AESKey);
                    String url = AESClientServerUtils.encrypt(et_url.getText().toString().trim(), AESKey);
                    String pkg = AESClientServerUtils.encrypt(et_pkg.getText().toString().trim(), AESKey);
                    String note = AESClientServerUtils.encrypt(et_note.getText().toString().trim(), AESKey);
                    //将经过AES加密的条目信息传送到服务器保存
                    AddItemToServer(itemName, username, password, url, pkg, note);
                    GreenDaoUtils.insertItem(itemName, username, password, url, pkg, note);
                    Intent intent = new Intent(ItemAddActivity.this, ItemsActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.bt_item_edit_view:
                Intent intent = new Intent(ItemAddActivity.this, ItemsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.pkglist:
                Intent intent1 = new Intent(ItemAddActivity.this, AppListActivity.class);
                intent1.putExtra("ClassName", TAG);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    /**
     * 将经过AES加密的条目信息传送到服务器保存
     * @param itemName AES加密过的条目名
     * @param username AES加密过的用户名
     * @param password AES加密过的密码
     * @param url AES加密过的Url
     * @param pkg AES加密过的pkg
     * @param note AES加密过的note
     */
    private void AddItemToServer(final String itemName, final String username, final String password, final String url, final String pkg, final String note) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.ITEM_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            Toast.makeText(ItemAddActivity.this, "条目已经成功添加到服务器", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ItemAddActivity.this, TAG + "  " + "AddItemToServer Error", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                String encryptedMySQLCommand = AESClientServerUtils.encrypt(Constants.ADD_ITEM, AESKey);
                String encryptedItemId = AESClientServerUtils.encrypt(String.valueOf(0), AESKey);
                map.put(Constants.MYSQL_COMMAND, encryptedMySQLCommand);
                map.put(Constants.item_id, encryptedItemId);
                map.put(Constants.item_itemname, itemName);
                map.put(Constants.item_username, username);
                map.put(Constants.item_password, password);
                map.put(Constants.item_url, url);
                map.put(Constants.item_package_name, pkg);
                map.put(Constants.item_note, note);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     * 判断输入是否合法
     * @param newName 输入的条目名
     * @param newUsername 输入的用户名
     * @param newPassword 输入的密码
     * @param newUrl 输入的Url
     * @param newPkg 输入的pkg
     * @param newNote 输入的note
     * @return 返回驶入是否合法的标志
     */
    private boolean isParamsLegal(String newName, String newUsername, String newPassword, String newUrl, String newPkg, String newNote) {
        //名称、用户名、密码三项不能为空
        if (newName.length() == 0 || newUsername.length() == 0 || newPassword.length() == 0) {
            return false;
        }
        //url和包名不能同时为空
        else if (newUrl.length() == 0 && newPkg.length() == 0) {
            return false;
        }
        else if(newUrl.length() > 0 && !isUrlValid(newUrl)){
            return false;
        }
        else if(newUrl.length() > 0 && newPkg.length() > 0){
            return false;
        }
        return true;
    }

    /**
     * 判断Url是否合法
     * @param newUrl
     * @return
     */
    private boolean isUrlValid(String newUrl) {
        String[] schemas = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemas);
        return urlValidator.isValid(newUrl);
    }

    /**
     * EditText的触摸触发安全键盘的弹出
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            et_itemName.setShowSoftInputOnFocus(true);
            et_username.setShowSoftInputOnFocus(true);
            et_password.setShowSoftInputOnFocus(true);
            et_url.setShowSoftInputOnFocus(true);
            et_pkg.setShowSoftInputOnFocus(true);
            et_note.setShowSoftInputOnFocus(true);
            switch (v.getId()) {
                case R.id.et_item_edit_name:
                    new InnerKeyboardUtils(this, et_itemName).showKeyBoard();
                    break;
                case R.id.et_item_edit_username:
                    new InnerKeyboardUtils(this, et_username).showKeyBoard();
                    break;
                case R.id.et_item_edit_password:
                    new InnerKeyboardUtils(this, et_password).showKeyBoard();
                    break;
                case R.id.et_item_edit_url:
                    new InnerKeyboardUtils(this, et_url).showKeyBoard();
                    break;
                case R.id.et_item_edit_pkg:
                    new InnerKeyboardUtils(this, et_pkg).showKeyBoard();
                    break;
                case R.id.et_item_edit_note:
                    new InnerKeyboardUtils(this, et_note).showKeyBoard();
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }
}
