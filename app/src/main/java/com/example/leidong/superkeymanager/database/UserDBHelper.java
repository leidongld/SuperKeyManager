package com.example.leidong.superkeymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leidong on 2016/10/17
 */

public class UserDBHelper extends SQLiteOpenHelper{
    //存放主密码的数据库名称SuperKeyManager1
    private static final String MASTER_PWD_DB_NAME = "SuperKeyManagerDB1.db";
    //存放主密码的数据表的名称MasterPassword
    public static final String MASTER_PWD_TABLE_NAME = "masterpassword";
    //存放主密码的数据库的版本
    private static final int MASTER_PWD_DB_VERSION = 3;
    //创建表的语句
    private static final String CREATE_MASTERPASSWORD_TABLE = "CREATE TABLE "
            + MASTER_PWD_TABLE_NAME + " ("
            + UserColumn._ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + UserColumn.MASTERPASSWORD + " text"
            + ")";

    //构造函数
    public UserDBHelper(Context context){
        super(context, MASTER_PWD_DB_NAME, null, MASTER_PWD_DB_VERSION);
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MASTERPASSWORD_TABLE);
    }

    //更新版本
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_PWD_TABLE_NAME);
        onCreate(db);
    }
}
