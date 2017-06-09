package com.example.leidong.superkeymanager.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

    ListView appListView = null;

    List<AppData> appDataList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);

        // init data
        appDataList = initAPPDataList();
        // init view
        appListView = (ListView) this.findViewById(R.id.app_ListView);
        appListView.setAdapter(new AppListAdapter(this, appDataList));
        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                AppListActivity.this.startActivity(getPackageManager()
                        .getLaunchIntentForPackage(
                                appDataList.get(arg2).appPackageName));

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
