package com.example.leidong.superkeymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leidong on 2016/10/17.
 */

public class ItemsDBHelper extends SQLiteOpenHelper{
    //存放条目的数据库名称
    private static final String ITEMS_DB_NAME = "SuperKeyManagerDB2.db";
    //存放条目的表的名称
    private static final String ITEMS_TABLE_NAME = "items";
    //数据库的版本
    private static final int ITEMS_DB_VERSION = 3;
    //创建表
    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE "
            + ITEMS_TABLE_NAME + " ("
            + ItemsColumn._ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + ItemsColumn.ITEM_NAME + " text,"
            + ItemsColumn.ITEM_USERNAME + " text,"
            + ItemsColumn.ITEM_PASSWORD + " text,"
            + ItemsColumn.ITEM_URL + " text,"
            + ItemsColumn.ITEM_PACKAGE + " text,"
            + ItemsColumn.ITEM_NOTE + " text"
            + ")";

    public ItemsDBHelper(Context context){
        super(context, ITEMS_DB_NAME, null, ITEMS_DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
        onCreate(db);
    }
}
