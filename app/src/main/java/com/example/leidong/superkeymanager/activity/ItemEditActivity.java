package com.example.leidong.superkeymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.AESClientServerUtils;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;
import com.example.leidong.superkeymanager.utils.InnerKeyboardUtils;
import com.example.leidong.superkeymanager.utils.UserDefault;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.HashMap;
import java.util.Map;

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
    private ImageView pkglist;

    private Switch switch_item_edit;

    private long itemId;
    private String AESKey;

    private FingerprintManagerCompat fingerprintManagerCompat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        QuitActivities.getInstance().addActivity(this);

        fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        //获取组件
        initWidgets();

        //Switch的改变
        switchChange();

        //禁止截屏
//        Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        Intent intent = getIntent();
        itemId = intent.getLongExtra(Constants.item_id, 0);

        AESKey = UserDefault.getUserDefaultInstance(null).load(Constants.AES_KEY, "");
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
        pkglist = (ImageView) findViewById(R.id.pkglist);
        pkglist.setOnClickListener(this);
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
                Toast.makeText(ItemEditActivity.this, "请进行指纹认证", Toast.LENGTH_LONG).show();
//                fingerprintManagerCompat.authenticate(null, 0, null, new FingerCallback(), null);
//                String newName0 = et_item_edit_name.getText().toString().trim();
//                String newUsername0 = et_item_edit_username.getText().toString().trim();
//                String newPassword0 = et_item_edit_password.getText().toString().trim();
//                String newUrl0 = et_item_edit_url.getText().toString().trim();
//                String newPkg0 = et_item_edit_pkg.getText().toString().trim();
//                String newNote0 = et_item_edit_note.getText().toString().trim();
//                if (!isParamsLegal(newName0, newUsername0, newPassword0, newUrl0, newPkg0, newNote0)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("警告");
//                    builder.setMessage("输入数据格式不正确");
//                    builder.setPositiveButton("重新输入", null);
//                    builder.create().show();
//                } else {
//                    String newName = AESClientServerUtils.encrypt(newName0, AESKey);
//                    String newUsername = AESClientServerUtils.encrypt(newUsername0, AESKey);
//                    String newPassword = AESClientServerUtils.encrypt(newPassword0, AESKey);
//                    String newUrl = AESClientServerUtils.encrypt(newUrl0, AESKey);
//                    String newPkg = AESClientServerUtils.encrypt(newPkg0, AESKey);
//                    String newNote = AESClientServerUtils.encrypt(newNote0, AESKey);
//
//                    GreenDaoUtils.updateItem(itemId, newName, newUsername, newPassword, newUrl, newPkg, newNote);
//
//                    //修改数据库中的对应条目
//                    mofifyItemToServer(itemId, newName, newUsername, newPassword, newUrl, newPkg, newNote);
//
//                    Toast.makeText(ItemEditActivity.this, "已完成条目的修改", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(ItemEditActivity.this, ItemsActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
                break;
            //对返回条目查看按钮的监控
            case R.id.bt_item_edit_view:
                Intent viewIntent = new Intent(ItemEditActivity.this, ItemsActivity.class);
                startActivity(viewIntent);
                finish();
                break;
            case R.id.pkglist:
                Intent intent = new Intent(ItemEditActivity.this, AppListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 修改数据库中的对应条目
     * @param itemId 条目ID
     * @param newName 条目名称
     * @param newUsername 条目用户名
     * @param newPassword 条目密码
     * @param newPkg 条目包名
     * @param newNote 条目备注
     */
    private void mofifyItemToServer(final long itemId, final String newName, final String newUsername, final String newPassword, final String newUrl, final String newPkg, final String newNote) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.ITEM_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            Toast.makeText(ItemEditActivity.this, "对应条目已经修改完毕", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ItemEditActivity.this, TAG + "  mofifyItemToServer  onErrorResponse", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                String encryptedMySQLCommand = AESClientServerUtils.encrypt(Constants.MODIFY_ITEM, AESKey);
                String encryptedItemId = AESClientServerUtils.encrypt(String.valueOf(itemId), AESKey);
                map.put(Constants.MYSQL_COMMAND, encryptedMySQLCommand);
                map.put(Constants.item_id, encryptedItemId);
                map.put(Constants.item_itemname, newName);
                map.put(Constants.item_username, newUsername);
                map.put(Constants.item_password, newPassword);
                map.put(Constants.item_url, newUrl);
                map.put(Constants.item_package_name, newPkg);
                map.put(Constants.item_note, newNote);
                return map;
            }
        };
        requestQueue.add(request);
    }

    /**
     * 参数是否合法
     * @param newName     条目名
     * @param newUsername 用户名
     * @param newPassword 密码
     * @param newUrl      URL
     * @param newPkg      包名
     * @param newNote     备注
     * @return 是否为空的标志
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
     * @param newUrl 传入的Url
     * @return 是否合法的标志
     */
    private boolean isUrlValid(String newUrl) {
        String[] schemas = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemas);
        return urlValidator.isValid(newUrl);
    }

    /**
     * 菜单点击操作
     * @param item 条目
     * @return 返回
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //完成修改的点击
            case R.id.menu_item_edit_activity_finish:
                break;

            //删除该条目
            case R.id.menu_item_edit_activity_delete:
                GreenDaoUtils.deleteItem(itemId);
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
     * @param v 视图
     * @param event 事件
     * @return 返回
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
                    new InnerKeyboardUtils(this, et_item_edit_name).showKeyBoard();
                    break;
                case R.id.et_item_edit_username:
                    new InnerKeyboardUtils(this, et_item_edit_username).showKeyBoard();
                    break;
                case R.id.et_item_edit_password:
                    new InnerKeyboardUtils(this, et_item_edit_password).showKeyBoard();
                    break;
                case R.id.et_item_edit_url:
                    new InnerKeyboardUtils(this, et_item_edit_url).showKeyBoard();
                    break;
                case R.id.et_item_edit_pkg:
                    new InnerKeyboardUtils(this, et_item_edit_pkg).showKeyBoard();
                    break;
                case R.id.et_item_edit_note:
                    new InnerKeyboardUtils(this, et_item_edit_note).showKeyBoard();
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    private class FingerCallback extends FingerprintManagerCompat.AuthenticationCallback {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Toast.makeText(ItemEditActivity.this, "指纹认证错误了", Toast.LENGTH_LONG).show();
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(ItemEditActivity.this, "指纹认证失败了", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult
                                                      result) {
            fingerprintManagerCompat.authenticate(null, 0, null, new FingerCallback(), null);
            String newName0 = et_item_edit_name.getText().toString().trim();
            String newUsername0 = et_item_edit_username.getText().toString().trim();
            String newPassword0 = et_item_edit_password.getText().toString().trim();
            String newUrl0 = et_item_edit_url.getText().toString().trim();
            String newPkg0 = et_item_edit_pkg.getText().toString().trim();
            String newNote0 = et_item_edit_note.getText().toString().trim();
            if (!isParamsLegal(newName0, newUsername0, newPassword0, newUrl0, newPkg0, newNote0)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("警告");
                builder.setMessage("输入数据格式不正确");
                builder.setPositiveButton("重新输入", null);
                builder.create().show();
            } else {
                String newName = AESClientServerUtils.encrypt(newName0, AESKey);
                String newUsername = AESClientServerUtils.encrypt(newUsername0, AESKey);
                String newPassword = AESClientServerUtils.encrypt(newPassword0, AESKey);
                String newUrl = AESClientServerUtils.encrypt(newUrl0, AESKey);
                String newPkg = AESClientServerUtils.encrypt(newPkg0, AESKey);
                String newNote = AESClientServerUtils.encrypt(newNote0, AESKey);

                GreenDaoUtils.updateItem(itemId, newName, newUsername, newPassword, newUrl, newPkg, newNote);

                //修改数据库中的对应条目
                mofifyItemToServer(itemId, newName, newUsername, newPassword, newUrl, newPkg, newNote);

                Toast.makeText(ItemEditActivity.this, "已完成条目的修改", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ItemEditActivity.this, ItemsActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}

