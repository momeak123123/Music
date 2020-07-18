package com.example.xiaobai.music.config;

import android.util.Base64;

import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Dencry {
    private static String decrypt(String str, String str2) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (Exception e) {
            e.printStackTrace();
            cipher = null;
        }
        if (cipher != null) {
            try {
                Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
                instance.init(2, new SecretKeySpec(str2.getBytes(), "AES"), new IvParameterSpec(str2.getBytes()));
                return new String(instance.doFinal(Base64.decode(str, 4)));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
    //取MD5
    private static String md5(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            int r4 = instance.getDigestLength();
            char[] cArr2 = new char[(r4 * 2)];
            int i = 0;
            for (byte b : instance.digest()) {
                int i2 = i + 1;
                cArr2[i] = cArr[(b >>> 4) & 15];
                i = i2 + 1;
                cArr2[i2] = cArr[b & 15];
            }
            return new String(cArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //解密字符串
    public static String dencryptString(String str) {
        String key = "vipmusic";
        return decrypt(str, Objects.requireNonNull(md5(key.getBytes())).substring(8, 24));
    }
}
