package com.example.music.sql.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.music.sql.gen.DaoMaster;
import com.example.music.sql.gen.DaoSession;

public class Initialization {

    private static DaoSession daoSessionmusic;


    /**
     * 配置数据库
     */
    /**
     * 配置数据库
     */
    public static void setupDatabaseMusic(Context context) {
        DaoMaster.DevOpenHelper helper4 = new DaoMaster.DevOpenHelper(context, "Music.db", null);
        //获取可写数据库
        SQLiteDatabase db4 = helper4.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster4 = new DaoMaster(db4);
        //获取Dao对象管理者
        daoSessionmusic = daoMaster4.newSession();
    }

    public static DaoSession getDaoInstantMusic() {
        return daoSessionmusic;
    }



}
