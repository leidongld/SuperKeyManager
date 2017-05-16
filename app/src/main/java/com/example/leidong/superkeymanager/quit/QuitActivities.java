package com.example.leidong.superkeymanager.quit;

import android.app.Activity;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leidong on 2016/10/17.
 * 退出所有Activities
 * 使用方法：
 * 1.在Activity创建时，调用  QuitActivites.getInstance().addActivity();
 * 2.在触发退出时，调用  QuitActivities.getInstance().exit();
 */

public class QuitActivities {
    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static QuitActivities instance;
    //构造方法
    private QuitActivities(){}
    //实例化一次
    public synchronized static QuitActivities getInstance(){
        if (null == instance) {
            instance = new QuitActivities();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity:mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    //杀进程
    public void onLowMemory() {
        instance.onLowMemory();
        System.gc();
    }
}

