package com.example.leidong.superkeymanager.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by leidong on 2017/5/29
 */
@Entity
public class ItemBean {
    @Id(autoincrement = true)
    private Long itemId;
    private String itemItemname;
    private String itemUsername;
    private String itemPassword;
    private String itemUrl;
    private String itemPackagename;
    private String itemNote;

    @Generated(hash = 956716770)
    public ItemBean(Long itemId, String itemItemname, String itemUsername,
            String itemPassword, String itemUrl, String itemPackagename,
            String itemNote) {
        this.itemId = itemId;
        this.itemItemname = itemItemname;
        this.itemUsername = itemUsername;
        this.itemPassword = itemPassword;
        this.itemUrl = itemUrl;
        this.itemPackagename = itemPackagename;
        this.itemNote = itemNote;
    }

    @Generated(hash = 95333960)
    public ItemBean() {
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemItemname() {
        return this.itemItemname;
    }

    public void setItemItemname(String itemItemname) {
        this.itemItemname = itemItemname;
    }

    public String getItemUsername() {
        return this.itemUsername;
    }

    public void setItemUsername(String itemUsername) {
        this.itemUsername = itemUsername;
    }

    public String getItemPassword() {
        return this.itemPassword;
    }

    public void setItemPassword(String itemPassword) {
        this.itemPassword = itemPassword;
    }

    public String getItemUrl() {
        return this.itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getItemPackagename() {
        return this.itemPackagename;
    }

    public void setItemPackagename(String itemPackagename) {
        this.itemPackagename = itemPackagename;
    }

    public String getItemNote() {
        return this.itemNote;
    }

    public void setItemNote(String itemNote) {
        this.itemNote = itemNote;
    }
}
