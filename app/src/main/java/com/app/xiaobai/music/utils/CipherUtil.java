package com.app.xiaobai.music.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import com.app.xiaobai.music.config.Installation;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {

    private static byte[] getRawKey(byte[] seed) {
        byte[] rawKey = InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(seed, 32);
        return rawKey;
    }
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
    }


    // 加密
    public static String encryptString(Context context,File file) throws Exception{
        byte[] raw = getRawKey(Installation.getUniqueID(context).getBytes());
        return getFile(encrypt(raw, readFile(file)),file.getPath());
    }

    //file文件读取成byte[]
    private static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    //关闭读取file
    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    // 解密
    public static String decryptString(Context context,String path) throws Exception{
        File file = new File(path);
        byte[] raw = getRawKey(Installation.getUniqueID(context).getBytes());
       //return getFile(decrypt(raw, readFile(file)),path);
        return getFiles(context,decrypt(raw, readFile(file)));

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





    /**
     *  根据byte数组，生成文件
     * @param bfile byte字节流
     * @return
     */
    private static String getFiles(Context context, byte[] bfile) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        String path = "";
        try {
            //创建临时文件的api参数 (文件前缀,文件后缀,存放目录)
            file = new File(Objects.requireNonNull(context.getCacheDir()).getAbsolutePath(),"test");
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            path = file.getPath();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建临时文件失败!" + e.getMessage());
            return "";
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
    }
}
