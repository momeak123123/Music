package com.app.xiaobai.music.sql.dao;

import com.app.xiaobai.music.sql.bean.Playlist;
import com.app.xiaobai.music.sql.config.Initialization;
import com.app.xiaobai.music.sql.gen.PlaylistDao;

import java.util.List;

public class mPlaylistDao {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param playlist
     */
    public static void insert(Playlist playlist) {
        Initialization.getDaoInstantPlaylist().getPlaylistDao().insertOrReplace(playlist);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void delete(Long id) {
        Initialization.getDaoInstantPlaylist().getPlaylistDao().deleteByKey(id);
    }

    /**
     * 删除数据
     *
     * @param
     */
    public static void deleteAll() {
        Initialization.getDaoInstantPlaylist().getPlaylistDao().deleteAll();
    }

    /**
     * 更新数据
     *
     * @param playlist
     */
    public static void update(Playlist playlist) {
        Initialization.getDaoInstantPlaylist().getPlaylistDao().update(playlist);
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Playlist> query(Long playid) {
        return Initialization.getDaoInstantPlaylist().getPlaylistDao().queryBuilder().where(PlaylistDao.Properties.Play_list_id.eq(playid)).list();
    }


    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Playlist> querys(String userid) {
        return Initialization.getDaoInstantPlaylist().getPlaylistDao().queryBuilder().where(PlaylistDao.Properties.User_id.eq(userid)).list();
    }


    /**
     * 查询全部数据
     */
    public static List<Playlist> queryAll() {
        return Initialization.getDaoInstantPlaylist().getPlaylistDao().loadAll();
    }

    /**
     * 分页
     */
    public static List<Playlist> queryBuilder(Long playid,int set , int lim) {

        return Initialization.getDaoInstantPlaylist().getPlaylistDao().queryBuilder().where(PlaylistDao.Properties.Play_list_id.eq(playid)).offset(set).limit(lim).list();
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

    public static List<Playlist> queryBuilder() {
        // 正序
        return Initialization.getDaoInstantPlaylist().getPlaylistDao().queryBuilder().orderAsc(PlaylistDao.Properties.Play_list_id).list();

        // 反序
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderDesc(UserDao.Properties.Id).list();

        // 多条件
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderAsc(UserDao.Properties.Id).orderDesc(UserDao.Properties.MemberSex).list();

    }
}
