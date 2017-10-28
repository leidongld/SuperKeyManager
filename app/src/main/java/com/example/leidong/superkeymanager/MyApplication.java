package com.example.leidong.superkeymanager;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.example.leidong.superkeymanager.gen.DaoMaster;
import com.example.leidong.superkeymanager.gen.DaoSession;
import com.example.leidong.superkeymanager.utils.UserDefault;

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
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate(){
        super.onCreate();

        context = getApplicationContext();

        instances = this;
        setDatabase();

        UserDefault.getUserDefaultInstance(getApplicationContext());
//        UserDefault.getUserDefaultInstance(null).save();
//        UserDefault.getUserDefaultInstance(null).save("AESKey", "");
//        UserDefault.getUserDefaultInstance(null).save("publicKey", "");

        /*HurlStack hurlStack = new HurlStack(null, HttpsUtils.initCertificates(bksFile, password, certificates));
        mRequestQueue = Volley.newRequestQueue(context, hurlStack);*/
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
        //mDaoMaster = new DaoMaster(mHelper.getEncryptedWritableDb("123456"));
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
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
