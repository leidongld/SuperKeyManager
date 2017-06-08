package com.example.leidong.superkeymanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.leidong.superkeymanager.MyApplication;
import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.beans.ItemBean;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.gen.ItemBeanDao;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.AESClientServerUtil;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leidong on 2016/10/17.
 * 条目列表界面
 */
public class ItemsActivity extends AppCompatActivity {
    private static final String TAG = "ItemsActivity";
    private static final String ITEM_ICON = "item_icon";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_NAME = "item_name";


    private ListView lv_items_activity_items;
    private ItemBeanDao itemBeanDao;
    private ArrayList<HashMap<String, Object>> itemDatas = new ArrayList<>();

    private long itemId;

    private String AESKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        //禁止截屏
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        QuitActivities.getInstance().addActivity(this);

        //获取控件
        initWidgets();

        SharedPreferences sp = getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
        AESKey = sp.getString(Constants.AES_SP_AESKEY, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        itemDatas = obtainItemDatas();
        Log.d(TAG, "<<<>>>\n" + itemDatas.get(0).get(ITEM_NAME));

        if (itemDatas.size() >= 0) {
            SimpleAdapter adapter = new SimpleAdapter(this, itemDatas, R.layout.item,
                    new String[]{ITEM_ICON, ITEM_ID, ITEM_NAME}, new int[]{R.id.item_icon, R.id.item_id, R.id.item_name});
            lv_items_activity_items.setAdapter(adapter);
        }

        //条目点击跳转到条目查看页面
        lv_items_activity_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemId = (long) itemDatas.get(position).get(ITEM_ID);
                Intent intent = new Intent(ItemsActivity.this, ItemViewActivity.class);
                intent.putExtra(Constants.item_id, itemId);
                startActivity(intent);
            }
        });

        //条目长按删除对应的条目
        lv_items_activity_items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemId = (long) itemDatas.get(position).get(ITEM_ID);
                GreenDaoUtils.deleteItem(itemId);
                itemDatas = obtainItemDatas();

                if (itemDatas.size() >= 0) {
                    SimpleAdapter adapter = new SimpleAdapter(MyApplication.getContext(), itemDatas, R.layout.item,
                            new String[]{ITEM_ICON, ITEM_ID, ITEM_NAME}, new int[]{R.id.item_icon, R.id.item_id, R.id.item_name});
                    lv_items_activity_items.setAdapter(adapter);
                }
                Toast.makeText(ItemsActivity.this, "条目已经删除！", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    /**
     * 得到Items的详细信息
     * @return
     */
    private ArrayList<HashMap<String, Object>> obtainItemDatas(){
        ArrayList<HashMap<String, Object>> itemDatas = new ArrayList<>();
        itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        List<ItemBean> itemList = itemBeanDao.loadAll();
        int length = itemList.size();
        for (int i = 0; i < length; i++) {

            HashMap<String, Object> map = new HashMap<>();
            map.put(ITEM_ICON, R.mipmap.app);
            map.put(ITEM_ID, itemList.get(i).getItemId());
            try {
                map.put(ITEM_NAME, AESClientServerUtil.decrypt(itemList.get(i).getItemItemname(), AESKey));
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemDatas.add(map);
        }
        return itemDatas;
    }

    /**
     * 获取控件
     */
    private void initWidgets() {
        lv_items_activity_items = (ListView) findViewById(R.id.lv_items_activity_items);
    }

    /**
     * 产生菜单
     *
     * @param menu 菜单
     * @return 产生结果
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items_activity, menu);
        return true;
    }

    /**
     * 菜单点击操作
     *
     * @param item 菜单条目
     * @return 点击结果
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //增加条目
            case R.id.menu_items_activity_add:
                Intent intent = new Intent(ItemsActivity.this, ItemAddActivity.class);
                startActivity(intent);
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
}
