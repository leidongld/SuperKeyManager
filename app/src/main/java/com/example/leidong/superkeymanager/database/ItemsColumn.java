package com.example.leidong.superkeymanager.database;

import android.provider.BaseColumns;

/**
 * Created by leidong on 2016/10/20.
 */

public class ItemsColumn implements BaseColumns{
    //构造函数
    public ItemsColumn(){

    }

    //列名
    public static final String ITEM_NAME = "name";
    public static final String ITEM_USERNAME = "username";
    public static final String ITEM_PASSWORD = "password";
    public static final String ITEM_URL = "url";
    public static final String ITEM_PACKAGE = "package";
    public static final String ITEM_NOTE = "note";

    //索引值
    public static final int ITEM_ID_COLUMN = 0;
    public static final int ITEM_NAME_COLUMN = 1;
    public static final int ITEM_USERNAME_COLUMN = 2;
    public static final int ITEM_PASSWORD_COLUMN = 3;
    public static final int ITEM_URL_COLUMN = 4;
    public static final int ITEM_PACKAGE_COLUMN = 5;
    public static final int ITEM_NOTE_COLUMN = 6;

    //查找
    public static final String[] ITEM_INFOS = {
            _ID,
            ITEM_NAME,
            ITEM_USERNAME,
            ITEM_PASSWORD,
            ITEM_URL,
            ITEM_PACKAGE,
            ITEM_NOTE
    };
}
