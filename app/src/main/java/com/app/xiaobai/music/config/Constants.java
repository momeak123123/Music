package com.app.xiaobai.music.config;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

import com.app.xiaobai.music.MusicApp;
import com.app.xiaobai.music.music.model.MusicPlayModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by momeak on 2020/6/8.
 */
public class Constants {

    public static final String URL = "http://api.xiaobai.de/";

    static String Dates(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static Boolean Downnum(){
       return MusicApp.getMinute() > 0;
    }

    static void Downokgo(Context context){
        MusicPlayModel.Companion.downnum(context);
    }

    public static String getRAMTotalMemorySize(final Context context){
        //获得ActivityManager服务的对象
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.totalMem ;
        //字符类型转换
        String availMemStr = formateFileSize(context,memSize);
        return availMemStr ;

    }

    //调用系统函数，字符串转换 long -String KB/MB
    public static String formateFileSize(Context context,long size){
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
}
