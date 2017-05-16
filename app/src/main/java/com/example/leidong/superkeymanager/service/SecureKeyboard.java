package com.example.leidong.superkeymanager.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.example.leidong.superkeymanager.R;
import com.example.leidong.superkeymanager.constants.Constants;
import com.example.leidong.superkeymanager.utils.AESUtil;

/**
 * Created by leidong on 2016/12/19.
 */

public class SecureKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener{
    private static final String TAG = "SecureKeyboard";

    private SharedPreferences sharedPreferences = getSharedPreferences(Constants.AES_SP_PARAMS, Context.MODE_PRIVATE);
    private String aesKey = sharedPreferences.getString("AESKey", "");

    private KeyboardView keyboardView;
    //keyboard1为数字和字母键盘、keyboard2位符号键盘
    private Keyboard keyboard1, keyboard2;
    private boolean upFlag = false;

    //对应条目的用户名
    private String encryptedUsername;
    //对应条目的密码
    private String encryptedPassword;

    //是否为字母键盘
    boolean isWord = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //取得用户名和密码的密文
        encryptedUsername = intent.getStringExtra("encrypted_username");
        encryptedPassword = intent.getStringExtra("encrypted_password");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 当键盘被创建会调用这个方法，初始化三个成员变量
     * @return
     */
    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView)getLayoutInflater().inflate(R.layout.secure_keyboard, null);
        keyboard1 = new Keyboard(this, R.xml.qwerty1);
        keyboard2 = new Keyboard(this, R.xml.qwerty2);
        keyboardView.setKeyboard(keyboard1);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    /**
     * 当按下按键的时候播放声音
     * @param keyCode
     */
    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            //空格的声音
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
                am.playSoundEffect(AudioManager.FX_KEY_CLICK);
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            //删除的声音
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            //默认的声音
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onPress(int primaryCode) {
        switch (primaryCode){
            case 1000:
                //Toast.makeText(this, "1000 onPress", Toast.LENGTH_LONG).show();
                break;
            case 1001:
                //Toast.makeText(this, "1001 onPress", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRelease(int primaryCode) {
        switch (primaryCode){
            case 1000:
                break;
            case 1001:
                break;
            default:
                break;
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                upFlag = !upFlag;
                keyboard1.setShifted(upFlag);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                if(isWord) {
                    keyboard2 = new Keyboard(this, R.xml.qwerty2);
                    keyboardView.setKeyboard(keyboard2);
                    isWord = false;
                }
                else{
                    keyboard1 = new Keyboard(this, R.xml.qwerty1);
                    keyboardView.setKeyboard(keyboard1);
                    isWord = true;
                }
                break;
            case Keyboard.KEYCODE_ALT:
                keyboard1 = new Keyboard(this, R.xml.qwerty1);
                keyboardView.setKeyboard(keyboard1);
                break;
            case 1000://使用安全键盘填入用户名
                String username = null;
                try {
                    username = AESUtil.decrypt(AESUtil.AES_KEY, encryptedUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ic.setComposingText(username, 30);
                break;
            case 1001://使用安全键盘填入密码
                String password = null;
                try {
                    password = AESUtil.decrypt(AESUtil.AES_KEY, encryptedPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ic.setComposingText(password, 30);
                ic.finishComposingText();
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && upFlag){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
