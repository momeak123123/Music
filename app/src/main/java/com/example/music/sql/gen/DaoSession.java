package com.example.music.sql.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.example.music.sql.bean.Playlist;
import com.example.music.sql.bean.Search;
import com.example.music.sql.bean.Down;

import com.example.music.sql.gen.PlaylistDao;
import com.example.music.sql.gen.SearchDao;
import com.example.music.sql.gen.DownDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig playlistDaoConfig;
    private final DaoConfig searchDaoConfig;
    private final DaoConfig downDaoConfig;

    private final PlaylistDao playlistDao;
    private final SearchDao searchDao;
    private final DownDao downDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        playlistDaoConfig = daoConfigMap.get(PlaylistDao.class).clone();
        playlistDaoConfig.initIdentityScope(type);

        searchDaoConfig = daoConfigMap.get(SearchDao.class).clone();
        searchDaoConfig.initIdentityScope(type);

        downDaoConfig = daoConfigMap.get(DownDao.class).clone();
        downDaoConfig.initIdentityScope(type);

        playlistDao = new PlaylistDao(playlistDaoConfig, this);
        searchDao = new SearchDao(searchDaoConfig, this);
        downDao = new DownDao(downDaoConfig, this);

        registerDao(Playlist.class, playlistDao);
        registerDao(Search.class, searchDao);
        registerDao(Down.class, downDao);
    }
    
    public void clear() {
        playlistDaoConfig.clearIdentityScope();
        searchDaoConfig.clearIdentityScope();
        downDaoConfig.clearIdentityScope();
    }

    public PlaylistDao getPlaylistDao() {
        return playlistDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

    public DownDao getDownDao() {
        return downDao;
    }

}
