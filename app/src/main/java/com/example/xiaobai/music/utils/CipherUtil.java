package com.example.xiaobai.music.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.xiaobai.music.config.Installation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {

    private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG"); // 获得一个随机数，传入的参数为默认方式。
        sr.setSeed(seed);  // 设置一个种子，这个种子一般是用户设定的密码。也可以是其它某个固定的字符串
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");  // 获得一个key生成器（AES加密模式）
        keyGen.init(128, sr);      // 设置密匙长度128位
        SecretKey key = keyGen.generateKey();  // 获得密匙
        return key.getEncoded();
    }

    private static byte[] encry(byte[] raw, byte[] input) throws Exception {  // 加密
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES"); // 根据上一步生成的密匙指定一个密匙（密匙二次加密？）
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");  // 获得Cypher实例对象
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);  // 初始化模式为加密模式，并指定密匙
        return cipher.doFinal(input);                         // 返回加密后的密文（byte数组)
    }

    private static byte[] decry(byte[] raw, byte[] encode) throws Exception{ // 解密
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);   // 解密的的方法差不多，只是这里的模式不一样
        return cipher.doFinal(encode);
    }



    // 加密
    public static String encryptString(Context context,File file) throws Exception{
        byte[] raw = getRawKey(Installation.id(context).getBytes());
        return getFile(encry(raw, getBytes(file)),file.getPath()+"xbm");
    }

    private static byte[] getBytes(File file){
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    // 解密
    public static String decryptString(Context context,String path) throws Exception{
        File file = new File(path);
        byte[] raw = getRawKey(Installation.id(context).getBytes());
        return getFile(decry(raw, getBytes(file)),path+".mp3");
    }

    /**
     * 根据byte数组，生成文件
     */
    private static String getFile(byte[] bfile, String path) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            file = new File(path);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return "";
    }

    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
