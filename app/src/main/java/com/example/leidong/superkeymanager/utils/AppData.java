package com.example.leidong.superkeymanager.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by leidong on 2017/1/14.
 */
public class AppData {
    public CharSequence appName;
    public String appPackageName;
    public Drawable appIcon;

    public AppData(CharSequence appName, String packageName, Drawable icon) {
        this.appName = appName;
        this.appPackageName = packageName;
        this.appIcon = icon;
    }
}
