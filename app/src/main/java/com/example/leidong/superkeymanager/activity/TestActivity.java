package com.example.leidong.superkeymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.utils.UserDefault;

/**
 * Created by leidong on 2017/7/26
 */

public class TestActivity extends Activity {
    private TextView aes;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        aes = (TextView) findViewById(R.id.aes);

        aes.setText(UserDefault.getUserDefaultInstance(null).load("AESKey", ""));
    }
}
