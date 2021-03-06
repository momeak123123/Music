package com.app.xiaobai.music.sql.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.app.xiaobai.music.sql.bean.Down;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DOWN".
*/
public class DownDao extends AbstractDao<Down, Long> {

    public static final String TABLENAME = "DOWN";

    /**
     * Properties of entity Down.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Song_id = new Property(1, Long.class, "song_id", false, "SONG_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Album_name = new Property(3, String.class, "album_name", false, "ALBUM_NAME");
        public final static Property Album_id = new Property(4, Long.class, "album_id", false, "ALBUM_ID");
        public final static Property Uri = new Property(5, String.class, "uri", false, "URI");
        public final static Property Artist = new Property(6, String.class, "artist", false, "ARTIST");
        public final static Property Artist_id = new Property(7, Long.class, "artist_id", false, "ARTIST_ID");
        public final static Property Pic_url = new Property(8, String.class, "pic_url", false, "PIC_URL");
        public final static Property Publish_time = new Property(9, String.class, "publish_time", false, "PUBLISH_TIME");
        public final static Property Song_list_id = new Property(10, Long.class, "song_list_id", false, "SONG_LIST_ID");
        public final static Property User_id = new Property(11, String.class, "user_id", false, "USER_ID");
        public final static Property User = new Property(12, String.class, "user", false, "USER");
    }


    public DownDao(DaoConfig config) {
        super(config);
    }
    
    public DownDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DOWN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SONG_ID\" INTEGER," + // 1: song_id
                "\"NAME\" TEXT," + // 2: name
                "\"ALBUM_NAME\" TEXT," + // 3: album_name
                "\"ALBUM_ID\" INTEGER," + // 4: album_id
                "\"URI\" TEXT," + // 5: uri
                "\"ARTIST\" TEXT," + // 6: artist
                "\"ARTIST_ID\" INTEGER," + // 7: artist_id
                "\"PIC_URL\" TEXT," + // 8: pic_url
                "\"PUBLISH_TIME\" TEXT," + // 9: publish_time
                "\"SONG_LIST_ID\" INTEGER," + // 10: song_list_id
                "\"USER_ID\" TEXT," + // 11: user_id
                "\"USER\" TEXT);"); // 12: user
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_DOWN__id ON \"DOWN\"" +
                " (\"_id\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DOWN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Down entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long song_id = entity.getSong_id();
        if (song_id != null) {
            stmt.bindLong(2, song_id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String album_name = entity.getAlbum_name();
        if (album_name != null) {
            stmt.bindString(4, album_name);
        }
 
        Long album_id = entity.getAlbum_id();
        if (album_id != null) {
            stmt.bindLong(5, album_id);
        }
 
        String uri = entity.getUri();
        if (uri != null) {
            stmt.bindString(6, uri);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(7, artist);
        }
 
        Long artist_id = entity.getArtist_id();
        if (artist_id != null) {
            stmt.bindLong(8, artist_id);
        }
 
        String pic_url = entity.getPic_url();
        if (pic_url != null) {
            stmt.bindString(9, pic_url);
        }
 
        String publish_time = entity.getPublish_time();
        if (publish_time != null) {
            stmt.bindString(10, publish_time);
        }
 
        Long song_list_id = entity.getSong_list_id();
        if (song_list_id != null) {
            stmt.bindLong(11, song_list_id);
        }
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(12, user_id);
        }
 
        String user = entity.getUser();
        if (user != null) {
            stmt.bindString(13, user);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Down entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long song_id = entity.getSong_id();
        if (song_id != null) {
            stmt.bindLong(2, song_id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String album_name = entity.getAlbum_name();
        if (album_name != null) {
            stmt.bindString(4, album_name);
        }
 
        Long album_id = entity.getAlbum_id();
        if (album_id != null) {
            stmt.bindLong(5, album_id);
        }
 
        String uri = entity.getUri();
        if (uri != null) {
            stmt.bindString(6, uri);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(7, artist);
        }
 
        Long artist_id = entity.getArtist_id();
        if (artist_id != null) {
            stmt.bindLong(8, artist_id);
        }
 
        String pic_url = entity.getPic_url();
        if (pic_url != null) {
            stmt.bindString(9, pic_url);
        }
 
        String publish_time = entity.getPublish_time();
        if (publish_time != null) {
            stmt.bindString(10, publish_time);
        }
 
        Long song_list_id = entity.getSong_list_id();
        if (song_list_id != null) {
            stmt.bindLong(11, song_list_id);
        }
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(12, user_id);
        }
 
        String user = entity.getUser();
        if (user != null) {
            stmt.bindString(13, user);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Down readEntity(Cursor cursor, int offset) {
        Down entity = new Down( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // song_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // album_name
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // album_id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // uri
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // artist
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // artist_id
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // pic_url
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // publish_time
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // song_list_id
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // user_id
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // user
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Down entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSong_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAlbum_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAlbum_id(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setUri(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setArtist(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setArtist_id(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setPic_url(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPublish_time(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setSong_list_id(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setUser_id(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setUser(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Down entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Down entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Down entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
