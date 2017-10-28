package com.example.leidong.superkeymanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.leidong.superkeymanager.constants.Constants;
import com.kenumir.materialsettings.storage.StorageInterface;

import java.util.Map;

/**
 * Created by leidong on 2017/10/28
 */

public class UserDefault  extends StorageInterface{
    private Context context;
    private static UserDefault userDefaultInstance;
    private SharedPreferences sharedPreferences;

    public UserDefault(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("UserDefault", Context.MODE_PRIVATE);
    }

    public static UserDefault getUserDefaultInstance(Context context){
        if(userDefaultInstance == null){
            userDefaultInstance = new UserDefault(context);
        }
        return userDefaultInstance;
    }


    @Override
    public void save(String s, Boolean aBoolean) {
        sharedPreferences.edit().putBoolean(s, aBoolean).apply();
    }

    @Override
    public boolean load(String s, Boolean aBoolean) {
        return sharedPreferences.getBoolean(s, aBoolean);
    }

    @Override
    public void save(String s, String s1) {
        sharedPreferences.edit().putString(s, s1).apply();
    }

    @Override
    public String load(String s, String s1) {
        return sharedPreferences.getString(s, s1);
    }

    @Override
    public void save(String s, Integer integer) {
        sharedPreferences.edit().putInt(s, integer).apply();
    }

    @Override
    public Integer load(String s, Integer integer) {
        return sharedPreferences.getInt(s, integer);
    }

    @Override
    public void save(String s, Float aFloat) {
        sharedPreferences.edit().putFloat(s, aFloat).apply();
    }

    @Override
    public Float load(String s, Float aFloat) {
        return sharedPreferences.getFloat(s, aFloat);
    }

    @Override
    public void save(String s, Long aLong) {
        sharedPreferences.edit().putLong(s, aLong).apply();
    }

    @Override
    public Long load(String s, Long aLong) {
        return sharedPreferences.getLong(s, aLong);
    }

    @Override
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public void setIsHasMasterPassword(boolean flag){
        sharedPreferences.edit().putBoolean(Constants.isHasMasterPassword, flag).apply();
    }

    public boolean isHasMasterPassword(){
        return sharedPreferences.getBoolean(Constants.isHasRSAPublicKey, false);
    }

    public void setIsHasFingerprint(boolean flag){
        sharedPreferences.edit().putBoolean(Constants.isHasFingerprint, flag).apply();
    }

    public boolean isHasFingerprint(){
        return sharedPreferences.getBoolean(Constants.isHasFingerprint, false);
    }

    public void setIsHasAESKey(boolean flag){
        sharedPreferences.edit().putBoolean(Constants.isHasAESKey, flag).apply();
    }

    public boolean isHasAESKey(){
        return sharedPreferences.getBoolean(Constants.isHasAESKey, false);
    }

    public void setIsHasRSAPublicKey(boolean flag){
        sharedPreferences.edit().putBoolean(Constants.isHasRSAPublicKey, flag).apply();
    }

    public boolean isHasRSAPublicKey(){
        return sharedPreferences.getBoolean(Constants.isHasRSAPublicKey, false);
    }

    public void setIsHasIME(boolean flag){
        sharedPreferences.edit().putBoolean(Constants.isHasIME, flag).apply();
    }

    public boolean isHasIME(){
        return sharedPreferences.getBoolean(Constants.isHasIME, false);
    }
}
