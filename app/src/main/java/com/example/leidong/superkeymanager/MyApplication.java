package com.example.leidong.superkeymanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.gen.DaoMaster;
import com.example.leidong.superkeymanager.gen.DaoSession;

/**
 * Created by leidong on 2017/4/1.
 */

public class MyApplication extends Application {
    private static Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static MyApplication instances;

    @Override
    public void onCreate(){
        super.onCreate();

        context = getApplicationContext();

        instances = this;
        setDatabase();

        SharedPreferences sp1 =getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp1.edit();
        editor1.putString(Constants.AES_SP_AESKEY, "");
        editor1.apply();
    }

    /**
     * 得到MyApplication
     * @return
     */
    public static MyApplication getInstances(){
        return instances;
    }

    /**
     * 获取Context
     * @return
     */
    public static Context getContext(){
        return context;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "items.db", null);
        mDaoMaster = new DaoMaster(mHelper.getEncryptedWritableDb("123456"));
        /*db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);*/
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 得到DaoSession
     * @return
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 得到Database
     * @return
     */
    public SQLiteDatabase getDb() {
        return db;
    }
}
