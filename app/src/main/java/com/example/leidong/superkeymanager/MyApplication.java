package com.example.leidong.superkeymanager;

import android.app.Application;
import android.content.Context;

/**
 * Created by leidong on 2017/4/1.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();

        context = getApplicationContext();
    }

    /**
     * 获取Context
     * @return
     */
    public static Context getContext(){
        return context;
    }
}
