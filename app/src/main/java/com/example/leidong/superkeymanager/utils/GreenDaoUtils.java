package com.example.leidong.superkeymanager.utils;

import com.example.leidong.superkeymanager.MyApplication;
import com.example.leidong.superkeymanager.beans.ItemBean;
import com.example.leidong.superkeymanager.gen.ItemBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by leidong on 2017/5/30
 */

public class GreenDaoUtils {
    /**
     * 根据id查找item
     * @param id id
     * @return 查找到的条目
     */
    public static ItemBean queryItemBeanById(long id) {
        ItemBeanDao itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        QueryBuilder<ItemBean> qb = itemBeanDao.queryBuilder();
        qb.where(ItemBeanDao.Properties.ItemId.eq(id));
        List<ItemBean> list = qb.list();
        return list.get(0);
    }

    /**
     * 根据id删除条目
     * @param id id
     */
    public static void deleteItem(long id) {
        ItemBeanDao itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        itemBeanDao.deleteByKey(id);
    }

    /**
     * 根据id更新条目
     * @param id id
     * @param newName 新的条目名称
     * @param newUsername 新的条目用户名
     * @param newPassword 新的条目密码
     * @param newUrl 新的条目URL
     * @param newPkg 新的条目包名
     * @param newNote 新的条目备注
     */
    public static void updateItem(long id, String newName, String newUsername, String newPassword, String newUrl, String newPkg, String newNote) {
        ItemBeanDao itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        ItemBean itemBean = new ItemBean();
        itemBean.setItemId(id);
        itemBean.setItemItemname(newName);
        itemBean.setItemUsername(newUsername);
        itemBean.setItemPassword(newPassword);
        itemBean.setItemUrl(newUrl);
        itemBean.setItemPackagename(newPkg);
        itemBean.setItemNote(newNote);
        itemBeanDao.update(itemBean);
    }

    /**
     * 插入条目
     * @param newName 条目名称
     * @param newUsername 条目用户名
     * @param newPassword 条目密码
     * @param newUrl 条目URL
     * @param newPkg 条目包名
     * @param newNote 条目备注
     */
    public static void insertItem(String newName, String newUsername, String newPassword, String newUrl, String newPkg, String newNote) {
        ItemBeanDao itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        ItemBean itemBean = new ItemBean();
        itemBean.setItemItemname(newName);
        itemBean.setItemUsername(newUsername);
        itemBean.setItemPassword(newPassword);
        itemBean.setItemUrl(newUrl);
        itemBean.setItemPackagename(newPkg);
        itemBean.setItemNote(newNote);
        itemBeanDao.insert(itemBean);
    }

    /**
     * 得到最新的条目的id
     * @return 最新的id
     */
    public static long obtainNewestId() {
        ItemBeanDao itemBeanDao = MyApplication.getInstances().getDaoSession().getItemBeanDao();
        List<ItemBean> list = itemBeanDao.loadAll();
        long id = list.get(list.size()-1).getItemId();
        return id;
    }
}
