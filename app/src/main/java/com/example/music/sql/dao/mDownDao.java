package com.example.music.sql.dao;

import com.example.music.sql.bean.Down;
import com.example.music.sql.config.Initialization;
import com.example.music.sql.gen.DownDao;

import java.util.List;

public class mDownDao {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param down
     */
    public static void insert(Down down) {
        Initialization.getDaoInstantDown().getDownDao().insertOrReplace(down);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void delete(Long id) {
        Initialization.getDaoInstantDown().getDownDao().deleteByKey(id);
    }

    /**
     * 删除数据
     *
     * @param
     */
    public static void deleteAll() {
        Initialization.getDaoInstantDown().getDownDao().deleteAll();
    }

    /**
     * 更新数据
     *
     * @param down
     */
    public static void update(Down down) {
        Initialization.getDaoInstantDown().getDownDao().update(down);
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Down> query(Long playid) {
        return Initialization.getDaoInstantDown().getDownDao().queryBuilder().where(DownDao.Properties.Playid.eq(playid)).list();
    }


    /**
     * 查询全部数据
     */
    public static List<Down> queryAll() {
        return Initialization.getDaoInstantDown().getDownDao().loadAll();
    }

    /**
     * 分页
     */
    public static List<Down> queryBuilder(Long playid ,int set , int lim) {

        return Initialization.getDaoInstantDown().getDownDao().queryBuilder().where(DownDao.Properties.Playid.eq(playid)).offset(set).limit(lim).list();
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

    public static List<Down> queryBuilder() {
        // 正序
        return Initialization.getDaoInstantDown().getDownDao().queryBuilder().orderAsc(DownDao.Properties.Id).list();

        // 反序
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderDesc(UserDao.Properties.Id).list();

        // 多条件
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderAsc(UserDao.Properties.Id).orderDesc(UserDao.Properties.MemberSex).list();

    }
}
