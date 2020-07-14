package com.example.xiaobai.music.config;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Date;

public class Cookie {
    //取现行时间戳
    public static String getTimeStamp(int type){
        String timeStamp = String.valueOf(new Date().getTime());
        if (type == 2) {
            return timeStamp.substring(0, 10);
        }
        return timeStamp;
    }
    //文本转字节
    public static byte[] String2Byte(String v, String encoding) {
        try {
            return v.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }
    //获取cookie
    public static String getCookie(){
        String timeStamp = getTimeStamp(2);
        byte[] timeByte = String2Byte(timeStamp,"UTF-8");
        return "kg_mid=" +  getMD5(timeByte);
    }

    private static String getMD5(byte[] b) {
        BigInteger bi;
        bi = new BigInteger(1, b);
        return bi.toString(16);
    }
}
