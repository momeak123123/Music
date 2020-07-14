package com.example.xiaobai.music.sql.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.xiaobai.music.sql.gen.DaoMaster;
import com.example.xiaobai.music.sql.gen.DaoSession;

public class Initialization {

    private static DaoSession daoSessionsearch;
    private static DaoSession daoSessionplaylist;
    private static DaoSession daoSessiondown;
    private static DaoSession daoSessioncollect;


    /**
     * 配置数据库
     */
    public static void setupDatabaseSearch(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "Search.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSessionsearch = daoMaster.newSession();
    }

    public static DaoSession getDaoInstantSearch() {
        return daoSessionsearch;
    }


    /**
     * 配置数据库
     */
    public static void setupDatabasePlaylist(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "Playlist.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSessionplaylist = daoMaster.newSession();
    }

    public static DaoSession getDaoInstantPlaylist() {
        return daoSessionplaylist;
    }

    /**
     * 配置数据库
     */
    public static void setupDatabaseDown(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "Down.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSessiondown = daoMaster.newSession();
    }

    public static DaoSession getDaoInstantDown() {
        return daoSessiondown;
    }

    /**
     * 配置数据库
     */
    public static void setupDatabaseCollect(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "Collect.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSessioncollect = daoMaster.newSession();
    }

    public static DaoSession getDaoInstantCollect() {
        return daoSessioncollect;
    }

}
