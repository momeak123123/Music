package com.example.music.xiaobai.sql.dao;

import com.example.music.xiaobai.sql.bean.Search;
import com.example.music.xiaobai.sql.config.Initialization;
import com.example.music.xiaobai.sql.gen.SearchDao;

import java.util.List;

public class mSearchDao {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param search
     */
    public static void insert(Search search) {
        Initialization.getDaoInstantSearch().getSearchDao().insertOrReplace(search);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void delete(Long id) {
        Initialization.getDaoInstantSearch().getSearchDao().deleteByKey(id);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteAll() {
        Initialization.getDaoInstantSearch().getSearchDao().deleteAll();
    }

    /**
     * 更新数据
     *
     * @param search
     */
    public static void update(Search search) {
        Initialization.getDaoInstantSearch().getSearchDao().update(search);
    }

    /**
     * 查询条件为Type=TYPE_LOVE的数据
     *
     * @return
     */
    public static List<Search> query(Long searchid) {
        return Initialization.getDaoInstantSearch().getSearchDao().queryBuilder().where(SearchDao.Properties.Id.eq(searchid)).list();
    }


    /**
     * 查询全部数据
     */
    public static List<Search> queryAll() {
        return Initialization.getDaoInstantSearch().getSearchDao().loadAll();
    }

    /**
     * 分页
     */
    public static List<Search> queryBuilder(Long searchid,int set , int lim) {

        return Initialization.getDaoInstantSearch().getSearchDao().queryBuilder().where(SearchDao.Properties.Id.eq(searchid)).offset(set).limit(lim).list();
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

    public static List<Search> queryBuilder() {
        // 正序
        return Initialization.getDaoInstantSearch().getSearchDao().queryBuilder().orderAsc(SearchDao.Properties.Id).list();

        // 反序
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderDesc(UserDao.Properties.Id).list();

        // 多条件
        //Initialization.getDaoInstantMusic().getMusicDao().queryBuilder().orderAsc(UserDao.Properties.Id).orderDesc(UserDao.Properties.MemberSex).list();

    }
}
