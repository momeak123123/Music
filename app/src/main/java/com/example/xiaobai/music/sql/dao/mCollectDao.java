package com.example.xiaobai.music.sql.dao;

import com.example.xiaobai.music.sql.bean.Collect;
import com.example.xiaobai.music.sql.config.Initialization;
import com.example.xiaobai.music.sql.gen.CollectDao;

import java.util.List;

public class mCollectDao {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param collect
     */
    public static void insert(Collect collect) {
        Initialization.getDaoInstantCollect().getCollectDao().insertOrReplace(collect);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void delete(Long id) {
        Initialization.getDaoInstantCollect().getCollectDao().deleteByKey(id);
    }

    /**
     * 删除数据
     *
     * @param
     */
    public static void deleteAll() {
        Initialization.getDaoInstantCollect().getCollectDao().deleteAll();
    }

    /**
     * 更新数据
     *
     * @param collect
     */
    public static void update(Collect collect) {
        Initialization.getDaoInstantCollect().getCollectDao().update(collect);
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Collect> query(Long playid) {
        return Initialization.getDaoInstantCollect().getCollectDao().queryBuilder().where(CollectDao.Properties.Playid.eq(playid)).list();
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Collect> querys(Long songid) {
        return Initialization.getDaoInstantCollect().getCollectDao().queryBuilder().where(CollectDao.Properties.Song_id.eq(songid)).list();
    }


    /**
     * 查询全部数据
     */
    public static List<Collect> queryAll() {
        return Initialization.getDaoInstantCollect().getCollectDao().loadAll();
    }

    /**
     * 分页
     */
    public static List<Collect> queryBuilder(Long playid ,int set , int lim) {

        return Initialization.getDaoInstantCollect().getCollectDao().queryBuilder().where(CollectDao.Properties.Playid.eq(playid)).offset(set).limit(lim).list();
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

    public static List<Collect> queryBuilder() {
        // 正序
        return Initialization.getDaoInstantCollect().getCollectDao().queryBuilder().orderAsc(CollectDao.Properties.Id).list();

        // 反序
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderDesc(UserDao.Properties.Id).list();

        // 多条件
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderAsc(UserDao.Properties.Id).orderDesc(UserDao.Properties.MemberSex).list();

    }
}
