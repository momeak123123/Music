package com.example.music.sql.dao;


import com.example.music.sql.bean.Music;
import com.example.music.sql.config.Initialization;
import com.example.music.sql.gen.MusicDao;

import java.util.List;

public class mMusicDao {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param music
     */
    public static void insert(Music music) {
        Initialization.getDaoInstantMusic().getMusicDao().insertOrReplace(music);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void delete(Long id) {
        Initialization.getDaoInstantMusic().getMusicDao().deleteByKey(id);
    }

    /**
     * 更新数据
     *
     * @param music
     */
    public static void update(Music music) {
        Initialization.getDaoInstantMusic().getMusicDao().update(music);
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Music> query(Long musicid) {
        return Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().where(MusicDao.Properties.Id.eq(musicid)).list();
    }


    /**
     * 查询全部数据
     */
    public static List<Music> queryAll() {
        return Initialization.getDaoInstantMusic().getMusicDao().loadAll();
    }

    /**
     * 分页
     */
    public static List<Music> queryBuilder(Long musicid,int set , int lim) {

        return Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().where(MusicDao.Properties.Id.eq(musicid)).offset(set).limit(lim).list();
    }

    /**
     * 多表查询
     */
    /*public static List<User> queryBuilder(int set , int lim) {

        QueryBuilder<User> queryBuilder = My.getChatDao().getUserDao().queryBuilder();

        queryBuilder.join(Address.class, AddressDao.Properties.userId)
                .where(AddressDao.Properties.Street.eq("Sesame Street"));

        return queryBuilder.list();

    }*/

    /**
     * 排序
     */

    public static List<Music> queryBuilder() {
        // 正序
        return Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderAsc(MusicDao.Properties.Id).list();

        // 反序
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderDesc(UserDao.Properties.Id).list();

        // 多条件
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderAsc(UserDao.Properties.Id).orderDesc(UserDao.Properties.MemberSex).list();

    }
}
