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

        SharedPreferences sp = getSharedPreferences(Constants.ITEM_SP_PARAMS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(Constants.ITEM_SP_ID, (long)-1);
        editor.apply();
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
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "leidong-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
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
