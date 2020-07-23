package com.app.xiaobai.music.sql.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.app.xiaobai.music.sql.bean.Collect;
import com.app.xiaobai.music.sql.bean.Down;
import com.app.xiaobai.music.sql.bean.Playlist;
import com.app.xiaobai.music.sql.bean.Search;

import com.app.xiaobai.music.sql.gen.CollectDao;
import com.app.xiaobai.music.sql.gen.DownDao;
import com.app.xiaobai.music.sql.gen.PlaylistDao;
import com.app.xiaobai.music.sql.gen.SearchDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig collectDaoConfig;
    private final DaoConfig downDaoConfig;
    private final DaoConfig playlistDaoConfig;
    private final DaoConfig searchDaoConfig;

    private final CollectDao collectDao;
    private final DownDao downDao;
    private final PlaylistDao playlistDao;
    private final SearchDao searchDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        collectDaoConfig = daoConfigMap.get(CollectDao.class).clone();
        collectDaoConfig.initIdentityScope(type);

        downDaoConfig = daoConfigMap.get(DownDao.class).clone();
        downDaoConfig.initIdentityScope(type);

        playlistDaoConfig = daoConfigMap.get(PlaylistDao.class).clone();
        playlistDaoConfig.initIdentityScope(type);

        searchDaoConfig = daoConfigMap.get(SearchDao.class).clone();
        searchDaoConfig.initIdentityScope(type);

        collectDao = new CollectDao(collectDaoConfig, this);
        downDao = new DownDao(downDaoConfig, this);
        playlistDao = new PlaylistDao(playlistDaoConfig, this);
        searchDao = new SearchDao(searchDaoConfig, this);

        registerDao(Collect.class, collectDao);
        registerDao(Down.class, downDao);
        registerDao(Playlist.class, playlistDao);
        registerDao(Search.class, searchDao);
    }
    
    public void clear() {
        collectDaoConfig.clearIdentityScope();
        downDaoConfig.clearIdentityScope();
        playlistDaoConfig.clearIdentityScope();
        searchDaoConfig.clearIdentityScope();
    }

    public CollectDao getCollectDao() {
        return collectDao;
    }

    public DownDao getDownDao() {
        return downDao;
    }

    public PlaylistDao getPlaylistDao() {
        return playlistDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

}