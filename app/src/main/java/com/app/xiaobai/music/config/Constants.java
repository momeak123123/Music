package com.app.xiaobai.music.config;

import android.annotation.SuppressLint;
import android.content.Context;

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


}
