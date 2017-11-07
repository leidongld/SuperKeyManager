package com.example.leidong.superkeymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.leidong.superkeymanager.MyApplication;
import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.beans.ItemBean;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.gen.ItemBeanDao;
import com.example.leidong.superkeymanager.quit.QuitActivities;
import com.example.leidong.superkeymanager.utils.AESClientServerUtils;
import com.example.leidong.superkeymanager.utils.GreenDaoUtils;
import com.example.leidong.superkeymanager.utils.UserDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leidong on 2016/10/17.
 * 条目列表界面
 */
public class ItemsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ItemsActivity";
    private static final String ITEM_ICON = "item_icon";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_NAME = "item_name";
    private ListView lv_items_activity_items;
    private ImageView addButton;
    private ItemBeanDao itemBeanDao;
    private ArrayList<HashMap<String, Object>> itemDatas = new ArrayList<>();

    private long itemId;

    private String AESKey;

    private FingerprintManagerCompat fingerprintManagerCompat;
    private int pos = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        //禁止截屏
//        Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        QuitActivities.getInstance().addActivity(this);

        fingerprintManagerCompat = FingerprintManagerCompat.from(this);

        //获取控件
        initWidgets();

        //初始化动作
        initActions();

        AESKey = UserDefault.getUserDefaultInstance(null).load(Constants.AES_KEY, "");
    }

    /**
     * 初始化动作
     */
    private void initActions() {
        addButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        itemDatas = obtainItemDatas();

        if (itemDatas.size() >= 0) {
            SimpleAdapter adapter = new SimpleAdapter(this, itemDatas, R.layout.item,
                    new String[]{ITEM_ICON, ITEM_ID, ITEM_NAME}, new int[]{R.id.item_icon, R.id.item_id, R.id.item_name});
            lv_items_activity_items.setAdapter(adapter);
        }

        //条目点击跳转到条目查看页面
        lv_items_activity_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ItemsActivity.this, "请进行指纹认证", Toast.LENGTH_LONG).show();
                fingerprintManagerCompat.authenticate(null, 0, null, new FingerCallback(), null);
                pos = position;
//                itemId = (long) itemDatas.get(position).get(ITEM_ID);
//                Intent intent = new Intent(ItemsActivity.this, ItemViewActivity.class);
//                intent.putExtra(Constants.item_id, itemId);
//                startActivity(intent);
            }
        });

        //条目长按删除对应的条目
        lv_items_activity_items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemId = (long) itemDatas.get(position).get(ITEM_ID);
                GreenDaoUtils.deleteItem(itemId);
                //删除条目
                deleteItemToServer(itemId);
                itemDatas = obtainItemDatas();

                if (itemDatas.size() >= 0) {
                    SimpleAdapter adapter = new SimpleAdapter(MyApplication.getContext(), itemDatas, R.layout.item,
                            new String[]{ITEM_ICON, ITEM_ID, ITEM_NAME}, new int[]{R.id.item_icon, R.id.item_id, R.id.item_name});
                    lv_items_activity_items.setAdapter(adapter);

                    //如果items的条目数目为0，则使id从1开始自增
                }
                return true;
            }
        });
    }

    /**
     * 删除服务器端的对应条目
     * @param itemId 条目ID
     */
    private void deleteItemToServer(final long itemId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.ITEM_SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                String encryptedMySQLCommand = AESClientServerUtils.encrypt(Constants.DELETE_ITEM, AESKey);
                String encryptedItemId = AESClientServerUtils.encrypt(String.valueOf(itemId), AESKey);
                map.put(Constants.MYSQL_COMMAND, encryptedMySQLCommand);
                map.put(Constants.item_id, encryptedItemId);
                return map;
            }
        };
        requestQueue.add(request);
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
            map.put(ITEM_NAME, AESClientServerUtils.decrypt(itemList.get(i).getItemItemname(), AESKey));
            itemDatas.add(map);
        }
        return itemDatas;
    }

    /**
     * 获取控件
     */
    private void initWidgets() {
        lv_items_activity_items = (ListView) findViewById(R.id.lv_items_activity_items);
        addButton = (ImageView) findViewById(R.id.add_button);
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

    /**
     * 控件点击
     * @param v 视图
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_button:
                Intent intent = new Intent(ItemsActivity.this, ItemAddActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private class FingerCallback extends FingerprintManagerCompat.AuthenticationCallback {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Toast.makeText(ItemsActivity.this, "指纹认证错误了", Toast.LENGTH_LONG).show();
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(ItemsActivity.this, "指纹认证失败了", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult
                                                      result) {
            itemId = (long) itemDatas.get(pos).get(ITEM_ID);
                Intent intent = new Intent(ItemsActivity.this, ItemViewActivity.class);
                intent.putExtra(Constants.item_id, itemId);
                startActivity(intent);
        }
    }
}
