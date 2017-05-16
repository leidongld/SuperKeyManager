package com.example.leidong.superkeymanager.utils;

//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;

/**
 * Created by leidong on 2016/12/23.
 */

public class RSAUtil {
    // 数字签名，密钥算法
    private static final String RSA_KEY_ALGORITHM = "RSA";

    // 数字签名签名/验证算法
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    // RSA密钥长度
    private static final int KEY_SIZE = 2048;

    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";

    /**
     * 初始化RSA密钥对
     * @return RSA密钥对
     * @throws Exception 抛出异常
     */
    public static Map<String, String> initKey() throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator
                .getInstance(RSA_KEY_ALGORITHM);
        SecureRandom secrand = new SecureRandom();
        secrand.setSeed("hahaha".getBytes());// 初始化随机产生器
        keygen.initialize(KEY_SIZE, secrand); // 初始化密钥生成器
        KeyPair keys = keygen.genKeyPair();
        String pub_key = Base64.encodeToString(keys.getPublic().getEncoded(), Base64.DEFAULT);
        String pri_key = Base64.encodeToString(keys.getPrivate().getEncoded(), Base64.DEFAULT);
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put(PUBLIC_KEY, pub_key);
        keyMap.put(PRIVATE_KEY, pri_key);
        System.out.println("公钥：" + pub_key);
        System.out.println("私钥：" + pri_key);
        return keyMap;
    }

    /**
     * 得到公钥
     * @param keyMap RSA密钥对
     * @return 公钥
     * @throws Exception 抛出异常
     */
    public static String getPublicKey(Map<String, String> keyMap) throws Exception{
        return keyMap.get(PUBLIC_KEY);
    }

    /**
     * 得到私钥
     * @param keyMap RSA密钥对
     * @return 私钥
     * @throws Exception 抛出异常
     */
    public static String getPrivateKey(Map<String, String> keyMap) throws Exception{
        return keyMap.get(PRIVATE_KEY);
    }

    /**
     * 数字签名
     * @param data 待签名数据
     * @param pri_key 私钥
     * @return 签名
     * @throws Exception 抛出异常
     */
    public static String sign(byte[] data, String pri_key) throws Exception {
        // 取得私钥
        byte[] pri_key_bytes = Base64.decode(pri_key, Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key_bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        // 生成私钥
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initSign(priKey);
        // 更新
        signature.update(data);
        return Base64.encodeToString(signature.sign(), Base64.DEFAULT);
    }

    /**
     * RSA校验数字签名
     * @param data 数据
     * @param sign 签名
     * @param pub_key 公钥
     * @return 校验结果，成功为true，失败为false
     * @throws Exception 抛出异常
     */
    public boolean verify(byte[] data, byte[] sign, String pub_key) throws Exception {
        // 转换公钥材料
        // 实例化密钥工厂
        byte[] pub_key_bytes = Base64.decode(pub_key, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        // 初始化公钥
        // 密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key_bytes);
        // 产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initVerify(pubKey);
        // 更新
        signature.update(data);
        // 验证
        return signature.verify(sign);
    }

    /**
     * 公钥加密
     * @param data 待加密数据
     * @param pub_key 公钥
     * @return 密文
     * @throws Exception 抛出异常
     */
    private static byte[] encryptByPubKey(byte[] data, byte[] pub_key) throws Exception {
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     * @param data 待加密数据
     * @param pub_key 公钥
     * @return 密文
     * @throws Exception 抛出异常
     */
    public static String encryptByPubKey(String data, String pub_key) throws Exception {
        // 私匙加密
        byte[] pub_key_bytes = Base64.decode(pub_key, Base64.DEFAULT);
        byte[] enSign = encryptByPubKey(data.getBytes(), pub_key_bytes);
        return Base64.encodeToString(enSign, Base64.DEFAULT);
    }

    /**
     * 私钥加密
     * @param data 待加密数据
     * @param pri_key 私钥
     * @return 密文
     * @throws Exception 抛出异常
     */
    private static byte[] encryptByPriKey(byte[] data, byte[] pri_key) throws Exception {
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     * @param data 待加密数据
     * @param pri_key 私钥
     * @return 密文
     * @throws Exception 抛出异常
     */
    public static String encryptByPriKey(String data, String pri_key) throws Exception {
        // 私匙加密
        byte[] pri_key_bytes = Base64.decode(pri_key, Base64.DEFAULT);
        byte[] enSign = encryptByPriKey(data.getBytes(), pri_key_bytes);
        return Base64.encodeToString(enSign, Base64.DEFAULT);
    }

    /**
     * 公钥解密
     * @param data 待解密数据
     * @param pub_key 公钥
     * @return 明文
     * @throws Exception 抛出异常
     */
    private static byte[] decryptByPubKey(byte[] data, byte[] pub_key) throws Exception {
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     * @param data 待解密数据
     * @param pub_key 公钥
     * @return 明文
     * @throws Exception 抛出异常
     */
    public static String decryptByPubKey(String data, String pub_key) throws Exception {
        // 公匙解密
        byte[] pub_key_bytes = Base64.decode(pub_key, Base64.DEFAULT);
        byte[] design = decryptByPubKey(Base64.decode(data, Base64.DEFAULT), pub_key_bytes);
        return new String(design);
    }

    /**
     * 私钥解密
     * @param data 待解密数据
     * @param pri_key 私钥
     * @return 明文
     * @throws Exception 抛出异常
     */
    private static byte[] decryptByPriKey(byte[] data, byte[] pri_key) throws Exception {
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     * @param data 待解密数据
     * @param pri_key 私钥
     * @return 明文
     * @throws Exception 抛出异常
     */
    public static String decryptByPriKey(String data, String pri_key) throws Exception {
        // 私匙解密
        byte[] pri_key_bytes = Base64.decode(pri_key, Base64.DEFAULT);
        byte[] design = decryptByPriKey(Base64.decode(data, Base64.DEFAULT), pri_key_bytes);
        return new String(design);
    }
}
