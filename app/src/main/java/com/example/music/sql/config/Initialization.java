package com.example.music.sql.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.music.sql.gen.DaoMaster;
import com.example.music.sql.gen.DaoSession;

public class Initialization {

    private static DaoSession daoSessionsearch;


/**
 * 配置数据库
 */
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

}
