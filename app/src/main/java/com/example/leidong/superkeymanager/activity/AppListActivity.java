package com.example.leidong.superkeymanager.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.utils.AppData;
import com.example.leidong.superkeymanager.utils.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leidong on 2017/1/14
 */
public class AppListActivity extends AppCompatActivity{
    private static final String TAG = "AppListActivity";

    private String preClassName;

    ListView appListView = null;

    List<AppData> appDataList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);

        Intent intent = getIntent();
        preClassName = intent.getStringExtra("ClassName");

        // init data
        appDataList = initAPPDataList();
        // init view
        appListView = (ListView) this.findViewById(R.id.app_ListView);
        appListView.setAdapter(new AppListAdapter(this, appDataList));
        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AppListActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定复制该应用的包名吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setText(appDataList.get(position).appPackageName);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
    }

    // 获取应用列表信息
    public List<AppData> initAPPDataList() {

        List<AppData> appDataList = new ArrayList<AppData>();
        // 获取PackageManager的对象
        PackageManager pm = getPackageManager();
        // 得到系统安装的所有程序包的PackageInfo对象
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (PackageInfo pi : packs) {
            AppData appData = new AppData(pi.applicationInfo.loadLabel(pm),
                    pi.applicationInfo.packageName,
                    pi.applicationInfo.loadIcon(pm));
            appDataList.add(appData);

        }
        return appDataList;
    }
}
