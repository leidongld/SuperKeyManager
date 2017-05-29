package com.example.leidong.superkeymanager.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.database.ItemsColumn;
import com.example.leidong.superkeymanager.database.ItemsProvider;
import com.example.leidong.superkeymanager.quit.QuitActivities;

/**
 * Created by leidong on 2016/10/17.
 * 条目列表界面
 */
public class ItemsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "ItemsActivity";

    private ListView lv_items_activity_items;
    private static final int DELEItem = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //获取组件并初始化
        init();

        //为Intent绑定数据
        Intent intent = getIntent();
        if(intent.getData() == null){
            intent.setData(ItemsProvider.ITEMS_URI);
        }

        //查询、获得所有条目的数据
        Cursor cursor = getContentResolver().query(getIntent().getData(), ItemsColumn.ITEM_INFOS, null, null, null);

        //注册每个列表表示形式
        SimpleCursorAdapter adapter = null;
        adapter = new SimpleCursorAdapter(ItemsActivity.this, android.R.layout.simple_list_item_2, cursor, new String[] {ItemsColumn.ITEM_NAME}, new int[] {android.R.id.text1}, CursorAdapter.IGNORE_ITEM_VIEW_TYPE);
        lv_items_activity_items.setAdapter(adapter);
    }

    /**
     * 获取组件并初始化
     */
    private void init() {
        lv_items_activity_items = (ListView)findViewById(R.id.lv_items_activity_items);
        lv_items_activity_items.setOnCreateContextMenuListener(ItemsActivity.this);
        lv_items_activity_items.setOnItemClickListener(ItemsActivity.this);
    }

    /**
     * 产生菜单
     * @param menu 菜单
     * @return 产生结果
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_items_activity, menu);
        return true;
    }

    /**
     * 菜单点击操作
     * @param item 菜单条目
     * @return 点击结果
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //增加条目
            case R.id.menu_items_activity_add:
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
                return true;

            //返回主界面
            case R.id.menu_items_activity_main:
                Intent mainIntent = new Intent(ItemsActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;

            //退出应用
            case R.id.menu_items_activity_exit:
                QuitActivities.getInstance().exit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 长按触发的菜单
     * @param menu
     * @param view
     * @param menuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        AdapterView.AdapterContextMenuInfo info;
        try{
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        }
        catch (ClassCastException e){
            return;
        }
        //得到长按的数据项
        Cursor cursor = (Cursor)lv_items_activity_items.getAdapter().getItem(info.position);
        if(cursor == null){
            return;
        }
        menu.setHeaderTitle(cursor.getString(1));
        //添加删除菜单
        menu.add(0, DELEItem, 0, "删除该条目");
    }

    /**
     * 长按列表触发的函数
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info;
        try{
            //获得选中项的信息
            info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        }
        catch (ClassCastException e){
            return false;
        }

        switch(item.getItemId()){
            //删除操作
            case DELEItem:
                Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                getContentResolver().delete(noteUri, null, null);
                return true;
        }
        return false;
    }

    /**
     * 条目列表点击监听
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        //查看条目
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
