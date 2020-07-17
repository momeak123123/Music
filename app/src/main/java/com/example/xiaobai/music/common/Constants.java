package com.example.xiaobai.music.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.xiaobai.music.MusicApp;
import com.example.xiaobai.music.sql.bean.Down;
import com.example.xiaobai.music.sql.dao.mDownDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by momeak on 2020/6/8.
 */
public class Constants {

    public static final String URL = "http://202.81.235.34/";

    public static String Dates(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static Boolean Downnum(){

        SharedPreferences sp = MusicApp.getAppContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        if(sp.getString("down_date", "").equals(Dates())){
           int num = sp.getInt("down_num",0);
            return num < MusicApp.getMinute();
        }else{
            sp.edit().putInt("down_num", 0).apply();
            return true;
        }
    }


}
