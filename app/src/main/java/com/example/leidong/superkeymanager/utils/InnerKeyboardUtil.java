package com.example.leidong.superkeymanager.utils;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.example.leidong.superkeymanager.R;

import java.util.List;

/**
 * Created by leido on 2016/12/22.
 */

public class InnerKeyboardUtil implements KeyboardView.OnKeyboardActionListener{
    private EditText editText;//当前需要添加内容的editText
    private KeyboardView keyboardView;//键盘视图
    private Keyboard keyboard1, keyboard2;//普通键盘和符号键盘
    private boolean upFlag = false;//英文字母大小写标志

    public InnerKeyboardUtil(Activity activity, EditText editText){
        this.editText = editText;

        //获取普通键盘和符号键盘
        keyboard1 = new Keyboard(activity.getApplicationContext(), R.xml.qwerty);
        keyboard2 = new Keyboard(activity.getApplicationContext(), R.xml.qwerty2);
        keyboardView = (KeyboardView)activity.findViewById(R.id.keyboard);

        //填充输入法布局
        keyboardView.setKeyboard(keyboard1);//普通键盘优先显示
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(this);
    }

    /**
     * 显示键盘
     */
    public void showKeyBoard(){
        int visibility = keyboardView.getVisibility();
        if(visibility == View.GONE || visibility == View.INVISIBLE){
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBoard(){
        int visibility = keyboardView.getVisibility();
        if(visibility == View.VISIBLE){
            keyboardView.setVisibility(View.GONE);
        }
    }

    /**
     * 切换大小写的功能
     */
    public void  changeKey(){
        List<Keyboard.Key> keyList = keyboard1.getKeys(); //获取字母键盘   所有字母
        if(upFlag){ //是大写默认
            upFlag = false;
            for(Keyboard.Key key:keyList){
                if(key.label!=null && isword(key.label.toString())){
                    key.label = key.label.toString().toLowerCase();//转换为小写
                    key.codes[0] =  key.codes[0] + 32; //转换为小写
                }
            }
        }else{ //小写   转为大写
            upFlag = true;
            for(Keyboard.Key key:keyList){
                if(key.label !=null && isword(key.label.toString())){
                    key.label = key.label.toString().toUpperCase();//转换为大写
                    key.codes[0] = key.codes[0] - 32;//
                }
            }
        }
    }

    /**
     * 判断普通键盘中的字母
     * @param str
     * @return
     */
    private boolean isword(String str){
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if(wordstr.indexOf(str.toLowerCase())>-1){
            return  true;
        }
        return  false;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        //添加监听事件：
        Editable edittable = editText.getText();//获取当前EditText的可编辑对象
        int start = editText.getSelectionStart();//
        if(primaryCode == Keyboard.KEYCODE_CANCEL){//完成按键
            hideKeyBoard();//隐藏键盘
        }else if (primaryCode == Keyboard.KEYCODE_DELETE){//回退
            if(edittable != null && edittable.length() > 0){
                if(start>0){
                    edittable.delete(start - 1,start);
                }
            }
        }else if(primaryCode == Keyboard.KEYCODE_SHIFT){//大小写  切换
            changeKey();//切换大小写
            keyboardView.setKeyboard(keyboard1);//设置为字母键盘
        }else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE){//数字和英文键盘切换
            if(isSymbol){//当前为符号键盘
                isSymbol = false;
                keyboardView.setKeyboard(keyboard1);// 设置为字母键盘
            }else{
                isSymbol = true;
                keyboardView.setKeyboard(keyboard2);//设置为符号键盘
            }
        }else if(primaryCode == 57419){  //go left
            if (start > 0) {
                editText.setSelection(start - 1);
            }
        }else if(primaryCode == 57421){ //go right
            if (start < editText.length()) {
                editText.setSelection(start + 1);
            }
        }else{
            edittable.insert(start,Character.toString((char)primaryCode));
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

    private boolean isSymbol = false;//是否是符号键盘
}
