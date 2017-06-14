package com.example.leidong.superkeymanager.database;

import android.provider.BaseColumns;

/**
 * Created by leidong on 2016/10/20
 */

public class UserColumn implements BaseColumns {
    public UserColumn(){

    }

    //列名
    public static final String MASTERPASSWORD = "masterpassword";

    //列索引值
    public static final int MASTERPASSWORD_ID_COLUMN = 0;
    public static final int MASTERPASSWORD_COLUMN = 1;

    //查询结果
    public static final String[] MASTERPASSWORD_INFOS = {_ID, MASTERPASSWORD};
}
