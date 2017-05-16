package com.example.leidong.superkeymanager.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by leidong on 2016/10/25.
 * 为主密码数据库提供操作类
 */

public class UserProvider extends ContentProvider {
    //标签
    private static final String TAG = "UserProvider";
    //主密码数据库帮助类
    private UserDBHelper userDBHelper;
    //数据库
    private SQLiteDatabase userDB;

    //数据库操作uri地址
    public static final String USER_AUTHORITY = "com.example.leidong.superkeymanager.user";
    public static final String MASTER_PWD_TABLE_NAME = "masterpassword";
    public static final Uri USER_URI = Uri.parse("content://" + USER_AUTHORITY + "/" + MASTER_PWD_TABLE_NAME);

    //下面是自定义的类型
    //public static final int USERS = 1;
    public static final int USER = 2;
    private static final UriMatcher uriMathch;
    static{
        //没有匹配的信息
        uriMathch = new UriMatcher(UriMatcher.NO_MATCH);
        //全部主密码的信息
        //uriMathch.addURI(USER_AUTHORITY, "masterpassword", USERS);
        //单独一个联系人的信息
        uriMathch.addURI(USER_AUTHORITY, "masterpassword/#", USER);
    }

    /**
     * 取得数据库
     * @return
     */
    @Override
    public boolean onCreate() {
        userDBHelper = new UserDBHelper(getContext());
        //去执行创建数据库
        userDB = userDBHelper.getWritableDatabase();
        return userDB == null ? false : true;
    }

    /**
     * 查询数据
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e(TAG + ":query"," in Query");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //设置要查询的数据表
        qb.setTables(MASTER_PWD_TABLE_NAME);

        switch (uriMathch.match(uri)){
            //构建where语句，定位到指定id的列
            case USER:
                qb.appendWhere(UserColumn._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }
        //查询
        Cursor cursor = qb.query(userDB, projection, selection, selectionArgs, null, null, sortOrder);
        //设置通知改变uri
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * URI类型转换
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMathch.match(uri)){
            //所有主密码
            /*case USERS:
                return "vnd.android.cursor.dir/vnd.com.example.superkeymanager.userprovider";*/
            //特定主密码
            case USER:
                return "vnd.android.cursor.item/vnd.com.example.superkeymanager.userprovider";
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

    /**
     * 插入数据
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //判断URI地址是否合法
        /*if(uriMathch.match(uri) != USERS){
            throw new IllegalArgumentException("Unknown URI" + uri);
        }*/
        ContentValues contentValues;
        if(values != null){
            contentValues = new ContentValues(values);
            Log.e(TAG + "insert", "values is not null");
        }
        else{
            contentValues = new ContentValues();
        }
        //如果对应的名称没有值，则设置默认值为""
        if(contentValues.containsKey(UserColumn.MASTERPASSWORD) == false){
            contentValues.put(UserColumn.MASTERPASSWORD, "");
        }
        Log.e(TAG + "insert", contentValues.toString());
        //插入数据
        long rowId = userDB.insert(MASTER_PWD_TABLE_NAME, null, contentValues);
        if(rowId > 0){
            //将id值加入uri地址中
            Uri noteUri = ContentUris.withAppendedId(USER_URI, rowId);
            //通知改变
            getContext().getContentResolver().notifyChange(noteUri, null);
            Log.e(TAG + "insert", noteUri.toString());
            return noteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    /**
     * 删除指定的数据列
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count;
        switch (uriMathch.match(uri)){
            //删除满足selection条件的行
            /*case USERS:
                count = userDB.delete(MASTER_PWD_TABLE_NAME, selection, selectionArgs);
                break;*/

            case USER:
                //取得主密码的id信息
                String masterpasswordID = uri.getPathSegments().get(1);
                //删除满足selection条件，并且id值为USER的记录
                count = userDB.delete(MASTER_PWD_TABLE_NAME,
                        UserColumn._ID + "=" +masterpasswordID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * 更新数据库
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        Log.e(TAG + "update", values.toString());
        Log.e(TAG + "update", uri.toString());
        switch (uriMathch.match(uri)){
            //根据selection条件批量进行更新
            /*case USERS:
                Log.e(TAG + "update", USERS + "");
                count = userDB.update(MASTER_PWD_TABLE_NAME, values, selection, selectionArgs);
                break;*/
            //更新指定行
            case USER:
                String masterpasswordID = uri.getPathSegments().get(1);
                Log.e(TAG + "update", USER + "");
                count = userDB.update(MASTER_PWD_TABLE_NAME, values, UserColumn._ID + "=" +masterpasswordID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        //通知更改
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
