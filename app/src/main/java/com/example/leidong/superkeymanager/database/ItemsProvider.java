package com.example.leidong.superkeymanager.database;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
/**
 * Created by leidong on 2016/10/25.
 * 为条目数据库提供操作类
 */
public class ItemsProvider extends ContentProvider {
    //标签
    private static final String TAG = "ItemsProvider";
    //条目数据库帮助类
    private ItemsDBHelper itemsDBHelper;
    //数据库
    private SQLiteDatabase itemsDB;

    //数据库操作uri地址
    public static final String ITEMS_AUTHORITY = "com.example.leidong.superkeymanager.items";
    public static final String ITEMS_TABLE_NAME = "items";
    public static final Uri ITEMS_URI = Uri.parse("content://" + ITEMS_AUTHORITY + "/" + ITEMS_TABLE_NAME);

    //下面是自定义的类型
    public static final int ITEMS = 1;
    public static final int ITEM = 2;
    private static final UriMatcher uriMatcher;
    static{
        //没有匹配的信息
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //全部条目的信息
        uriMatcher.addURI(ITEMS_AUTHORITY, "items", ITEMS);
        //单独一个条目的信息
        uriMatcher.addURI(ITEMS_AUTHORITY, "items/#", ITEM);
    }

    /**
     * 取得数据库
     * @return
     */
    @Override
    public boolean onCreate() {
        //判断sd卡是否存在的标志
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        itemsDBHelper = new ItemsDBHelper(getContext());
        //去操作数据库
        itemsDB = itemsDBHelper.getWritableDatabase();
        return itemsDB != null;
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e(TAG + ":query"," in Query");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //设置要查询的数据表
        qb.setTables(ITEMS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            //构建where语句，定位到指定id的列
            case ITEM:
                qb.appendWhere(ItemsColumn._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }
        //查询
        Cursor cursor = qb.query(itemsDB, projection, selection, selectionArgs, null, null, sortOrder);
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
        switch (uriMatcher.match(uri)){
            //所有条目
            case ITEMS:
                return "vnd.android.cursor.dir/vnd.com.example.superkeymanager.itemsprovider";
            //特定条目
            case ITEM:
                return "vnd.android.cursor.item/vnd.com.example.superkeymanager.itemsprovider";
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
    public Uri insert(Uri uri, ContentValues values) {
        //判断URI地址是否合法
        if(uriMatcher.match(uri) != ITEMS){
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
        ContentValues contentValues;
        if(values != null){
            contentValues = new ContentValues(values);
            Log.e(TAG + "insert", "values is not null");
        }
        else{
            contentValues = new ContentValues();
        }
        //如果对应的名称没有值，则设置默认值为""
        if(contentValues.containsKey(ItemsColumn.ITEM_NAME) == false){
            contentValues.put(ItemsColumn.ITEM_NAME, "");
        }
        if(contentValues.containsKey(ItemsColumn.ITEM_USERNAME) == false){
            contentValues.put(ItemsColumn.ITEM_USERNAME, "");
        }
        if(contentValues.containsKey(ItemsColumn.ITEM_PASSWORD) == false){
            contentValues.put(ItemsColumn.ITEM_PASSWORD, "");
        }
        if(contentValues.containsKey(ItemsColumn.ITEM_URL) == false){
            contentValues.put(ItemsColumn.ITEM_URL, "");
        }
        if(contentValues.containsKey(ItemsColumn.ITEM_PACKAGE) == false){
            contentValues.put(ItemsColumn.ITEM_PACKAGE, "");
        }
        if(contentValues.containsKey(ItemsColumn.ITEM_NOTE) == false){
            contentValues.put(ItemsColumn.ITEM_NOTE, "");
        }
        Log.e(TAG + "insert", contentValues.toString());
        //插入数据
        long rowId = itemsDB.insert(ITEMS_TABLE_NAME, null, contentValues);
        if(rowId > 0){
            //将id值加入uri地址中
            Uri noteUri = ContentUris.withAppendedId(ITEMS_URI, rowId);
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
        switch (uriMatcher.match(uri)){
            //删除满足selection条件的行
            case ITEMS:
                count = itemsDB.delete(ITEMS_TABLE_NAME, selection, selectionArgs);
                break;

            case ITEM:
                //取得条目的id信息
                String itemID = uri.getPathSegments().get(1);
                //删除满足selection条件，并且id值为ITEM的记录
                count = itemsDB.delete(ITEMS_TABLE_NAME,
                        ItemsColumn._ID + "=" + itemID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
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
    @SuppressLint("LongLogTag")
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        Log.e(TAG + "update", values.toString());
        Log.e(TAG + "update", uri.toString());
        Log.e(TAG + "update :match", "" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)){
            //根据selection条件批量进行更新
            case ITEMS:
                Log.e(TAG + "update", ITEMS + "");
                count = itemsDB.update(ITEMS_TABLE_NAME, values, selection, selectionArgs);
                break;
            //更新指定行
            case ITEM:
                String itemID = uri.getPathSegments().get(1);
                Log.e(TAG + "update", ITEM + "");
                count = itemsDB.update(ITEMS_TABLE_NAME, values, ItemsColumn._ID + "=" + itemID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        //通知更改
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
