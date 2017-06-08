package com.example.leidong.superkeymanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.utils.AESClientServerUtil;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;

/**
 * Created by leidong on 2017/6/8.
 */

public class ItemAddActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_itemName, et_username, et_password, et_url, et_pkg, et_note;
    private Button button1, button2;

    private String AESKey;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        initWidgets();
        initActions();

        SharedPreferences sp = getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
        AESKey = sp.getString(Constants.AES_SP_AESKEY, "");
    }

    /**
     * 初始化动作
     */
    private void initActions() {
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
    }

    /**
     * 按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_item_edit_finish:
                    String itemName0 = et_itemName.getText().toString().trim();
                    String username0 = et_username.getText().toString().trim();
                    String password0 = et_password.getText().toString().trim();
                    String url0 = et_url.getText().toString().trim();
                    String pkg0 = et_pkg.getText().toString().trim();
                    String note0 = et_note.getText().toString().trim();
                    if(isParamsNull(itemName0, username0, password0, url0, pkg0, note0)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("警告");
                        builder.setMessage("请保证输入正确");
                        builder.setPositiveButton("重新输入", null);
                        builder.create().show();
                    }
                    else {
                        String itemName = null;
                        String username = null;
                        String password = null;
                        String url = null;
                        String pkg = null;
                        String note = null;
                        try {
                            itemName = AESClientServerUtil.encrypt(et_itemName.getText().toString().trim(), AESKey);
                            username = AESClientServerUtil.encrypt(et_username.getText().toString().trim(), AESKey);
                            password = AESClientServerUtil.encrypt(et_password.getText().toString().trim(), AESKey);
                            url = AESClientServerUtil.encrypt(et_url.getText().toString().trim(), AESKey);
                            pkg = AESClientServerUtil.encrypt(et_pkg.getText().toString().trim(), AESKey);
                            note = AESClientServerUtil.encrypt(et_note.getText().toString().trim(), AESKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
            default:
                break;
        }
    }

    /**
     * 判断输入是否合法
     * @param newName
     * @param newUsername
     * @param newPassword
     * @param newUrl
     * @param newPkg
     * @param newNote
     * @return
     */
    private boolean isParamsNull(String newName, String newUsername, String newPassword, String newUrl, String newPkg, String newNote) {
        //名称、用户名、密码三项不能为空
        if (newName.length() == 0 || newUsername.length() == 0 || newPassword.length() == 0) {
            return true;
        }
        //url和包名不能同时为空
        else if (newUrl.length() == 0 && newPkg.length() == 0) {
            return true;
        }
        return false;
    }
}
