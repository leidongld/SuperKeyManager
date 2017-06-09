package com.example.leidong.superkeymanager.constants;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leidong on 2017/4/26
 */

public class Constants {
    //localhost地址
    public static final String LOCAL_HOST = "192.168.1.100:8080";

    //服务器上的主密码url
    public static final String MASTAER_PASSWORD_SERVER_URL = "http://" + LOCAL_HOST + "/SuperKeyManager/MasterPasswordServlet";
    //服务器上的条目url
    public static final String ITEM_SERVER_URL = "http://" + LOCAL_HOST + "/SuperKeyManager/ItemServlet";
    //服务器上的RSA url
    public static final String RSA_SERVER_URL = "http://" + LOCAL_HOST + "/SuperKeyManager/RSAServlet";

    //用来表示MySQL的命令标识
    public static final String MYSQL_COMMAND = "mysql command";

    //主密码最短长度限制
    public static final int MIN_MASTER_PASSWORD_LENGTH = 8;
    //登录时多次认证失败的等待时间60s
    public static final int LOCK_TIME = 60000;

    //Volley对masterpassword的操作
    public static final String REGISTER_MASTERPASSWORD = "register masterpassword";
    public static final String GET_MASTERPASSWORD = "get masterpassword";
    public static final String UPDATE_MASTERPASSWORD = "update masterpassword";
    public static final String CHECK_MASTERPASSWORD = "check masterpassword";
    public static final String master_password_id = "master_password_id";
    public static final String master_password = "master_password";
    public static final String master_password_digest = "master_password_digest";

    //Volley对items的操作
    public static final String ADD_ITEM = "add item";
    public static final String DELETE_ITEM = "delete item";
    public static final String MODIFY_ITEM = "modify item";
    public static final String GET_ITEM = "get item";
    public static final String FIND_ITEM = "find item";
    public static final String ITEM_SP_PARAMS = "item sp params";
    public static final String ITEM_SP_ID = "item sp id";
    public static final String ITEM_SP_PKG = "item sp pkg";

    public static final String item_id = "item_id";
    public static final String item_itemname = "item_itemname";
    public static final String item_username = "item_username";
    public static final String item_password = "item_password";
    public static final String item_package_name = "item_package_name";
    public static final String item_url = "item_url";
    public static final String item_note = "item_note";

    //RSA
    public static final String GENERATE_RSA_KEYS = "generate rsa keys";
    public static final String GET_RSA_PUBLIC_KEY = "get rsa public key";
    public static final String RSA_SP_PARAMS = "RSAParams";
    public static final String RSA_SP_PUBLICKEY = "RSAPublicKey";

    //AES
    public static final String AES_SP_PARAMS = "AESParams";
    public static final String AES_SP_AESKEY = "AESKey";
    public static final String ADD_AES = "add aes";
    public static final String ENCRYPTED_AESKEY = "encrypted_aeskey";


    /**
     * item_id : 1
     */

    @SerializedName("item_id")
    private int item_idX;

    public int getItem_idX() {
        return item_idX;
    }

    public void setItem_idX(int item_idX) {
        this.item_idX = item_idX;
    }
}
