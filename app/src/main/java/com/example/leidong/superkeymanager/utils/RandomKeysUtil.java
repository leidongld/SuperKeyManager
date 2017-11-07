package com.example.leidong.superkeymanager.utils;

import android.inputmethodservice.Keyboard;

import com.example.leidong.superkeymanager.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Created by leidong on 2017/10/28
 */

public class RandomKeysUtil {
    private static final String TAG = "RandomKeysUtil";

    /**
     * 符号和数字键盘的打乱
     * @param keyboard 键盘
     */
    public static void randomSymbolAndNumKeys(Keyboard keyboard) {
        List<Keyboard.Key> keyList = keyboard.getKeys();
        //打乱数字按键
        shuffleKeys(keyList.subList(0, 10));
        //打乱符号按键
        shuffleKeys(keyList.subList(10, 47));
    }


    /**
     * 字母键盘的打乱
     * @param keyboard 键盘
     */
    public static void randomWordKeys(Keyboard keyboard){
        //待处理按键
        List<Keyboard.Key> keyList = keyboard.getKeys().subList(0, 29);
        //存储已经选中的单元避免重复
        HashSet<Integer> selectedKeydNum = new HashSet<>();
        //存储所有单元对应的域中所有的单元编号
        HashMap<Integer, ArrayList<Integer>> zoneParams = MyApplication.getZoneParams();
        //存储随机生成的域
        Stack<ArrayList<Integer>> selectedZones = new Stack<>();

        //生成单元区
        while(selectedKeydNum.size() < 28){
            //产生0-28的随机数，剔除掉已经选中的域中的单元
            int random = generateRandom(0, 28);
            while(selectedKeydNum.contains(random)){
                random = generateRandom(0, 28);
            }
            //得到中心单元编号为random的单元域
            ArrayList<Integer> zone = zoneParams.get(random);
            //单元区初始化
            ArrayList<Integer> tempList = new ArrayList<>();
            //单元区生成
            for(int i = 0; i < zone.size(); i++){
                if(!selectedKeydNum.contains(zone.get(i))) {
                    tempList.add(zone.get(i));
                }
                selectedKeydNum.add(zone.get(i));
            }
            //单元区加入单元区集合
            selectedZones.push(tempList);
        }

        //随机交换每个单元区的中心单元与紧邻元素
        for(int i = 0; i < selectedZones.size(); i++){
            ArrayList<Integer> zone = selectedZones.get(i);
            int zoneSize = zone.size();
            if(zoneSize > 1) {
                int core = zone.get(0);//中心单元
                int random = generateRandom(1, zoneSize-1);
                int temp = zone.get(random);//选出的域中的随机单元
                changeTwoKey(core, temp, keyList);
            }
        }
    }

    /**
     * 交换编号为core和temp的两个按键
     * @param key1
     * @param key2
     * @param keyList
     */
    private static void changeTwoKey(int key1, int key2, List<Keyboard.Key> keyList) {
        int code = keyList.get(key1).codes[0];
        CharSequence label = keyList.get(key1).label;

        keyList.get(key1).codes[0] = keyList.get(key2).codes[0];
        keyList.get(key1).label = keyList.get(key2).label;

        keyList.get(key2).codes[0] = code;
        keyList.get(key2).label = label;
    }

    /**
     * 在start和end范围内产生功能随机数
     * @param start 起点
     * @param end 终点
     * @return 随机数
     */
    private static int generateRandom(int start, int end) {
        int res = Integer.MIN_VALUE;
        while(res < start || res > end){
            res = (int) (Math.random() * (end+1));
            if(res < start){
                res = (int) (Math.random() * (end+1));
            }
        }
        return res;
    }


    /**
     * 随机打乱start到end之间的按键
     * @param keyList 全部按键的List
     */
    private static void shuffleKeys(List<Keyboard.Key> keyList) {
        int size = keyList.size();
        for (int i = 0; i < size; i++) {
            int random_a = (int)(Math.random()*(size));
            int random_b = (int)(Math.random()*(size));

            changeTwoKey(random_a, random_b, keyList);
        }
    }

    /**
     * 装载单元域参数
     * @param zoneParams
     */
    public static void loadZoneParams(HashMap<Integer, ArrayList<Integer>> zoneParams) {
        //位置0--->q
        zoneParams.put(0, new ArrayList<>(Arrays.asList(0, 1, 10)));

        //位置1--->w
        zoneParams.put(1, new ArrayList<>(Arrays.asList(1, 0, 2, 11)));

        //位置2--->e
        zoneParams.put(2, new ArrayList<>(Arrays.asList(2, 1, 3, 12)));

        //位置3--->r
        zoneParams.put(3, new ArrayList<>(Arrays.asList(3, 2, 4, 13)));

        //位置4--->t
        zoneParams.put(4, new ArrayList<>(Arrays.asList(4, 3, 5, 14)));

        // 位置5--->y
        zoneParams.put(5, new ArrayList<>(Arrays.asList(5, 4, 6, 15)));

        //位置6--->u
        zoneParams.put(6, new ArrayList<>(Arrays.asList(6, 5, 7, 16)));

        // 位置7--->i
        zoneParams.put(7, new ArrayList<>(Arrays.asList(7, 6, 8, 17)));

        // 位置8--->o
        zoneParams.put(8, new ArrayList<>(Arrays.asList(8, 7, 9, 18)));

        // 位置9--->p
        zoneParams.put(9, new ArrayList<>(Arrays.asList(9, 8, 19)));

        // 位置10--->a
        zoneParams.put(10, new ArrayList<>(Arrays.asList(10, 0, 11, 20)));

        // 位置11--->s
        zoneParams.put(11, new ArrayList<>(Arrays.asList(11, 1, 10, 12, 21)));

        // 位置12--->d
        zoneParams.put(12, new ArrayList<>(Arrays.asList(12, 2, 11, 13, 22)));

        // 位置13--->f
        zoneParams.put(13, new ArrayList<>(Arrays.asList(13, 3, 12, 14, 23)));

        // 位置14--->g
        zoneParams.put(14, new ArrayList<>(Arrays.asList(14, 4, 13, 15, 24)));

        // 位置15--->h
        zoneParams.put(15, new ArrayList<>(Arrays.asList(15, 5, 14, 16, 25)));

        // 位置16--->j
        zoneParams.put(16, new ArrayList<>(Arrays.asList(16, 6, 15, 17, 26)));

        // 位置17--->k
        zoneParams.put(17, new ArrayList<>(Arrays.asList(17, 7, 16, 18, 27)));

        // 位置18--->l
        zoneParams.put(18, new ArrayList<Integer>(Arrays.asList(18, 8, 17, 19, 28)));

        // 位置19--->,
        zoneParams.put(19, new ArrayList<Integer>(Arrays.asList(19, 9, 18)));

        // 位置20--->z
        zoneParams.put(20, new ArrayList<Integer>(Arrays.asList(20, 10, 21)));

        // 位置21--->x
        zoneParams.put(21, new ArrayList<Integer>(Arrays.asList(21, 11, 20, 22)));

        // 位置22--->c
        zoneParams.put(22, new ArrayList<Integer>(Arrays.asList(22, 12, 21, 23)));

        // 位置23--->v
        zoneParams.put(23, new ArrayList<Integer>(Arrays.asList(23, 13, 22, 24)));

        // 位置24--->b
        zoneParams.put(24, new ArrayList<Integer>(Arrays.asList(24, 14, 23, 25)));

        // 位置25--->n
        zoneParams.put(25, new ArrayList<Integer>(Arrays.asList(25, 15, 24, 26)));

        // 位置26--->m
        zoneParams.put(26, new ArrayList<Integer>(Arrays.asList(26, 16, 25, 27)));

        // 位置27--->.
        zoneParams.put(27, new ArrayList<Integer>(Arrays.asList(27, 17, 26, 28)));

        // 位置28--->/
        zoneParams.put(28, new ArrayList<Integer>(Arrays.asList(28, 18, 27)));
    }
}
