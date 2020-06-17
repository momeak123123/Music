package com.example.music.sql.bean;

import com.example.music.bean.artistlist;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Down {

    @Id(autoincrement = true)//设置自增长
    @Index(unique = true)//设置唯一性
    private Long id;
    private Long playid;
    private Long song_id;
    private String name;
    private String album_name;
    private String album_id;
    private String uri;
    private String all_artist;
    private String pic_url;
    private String publish_time;

    @Generated(hash = 1909933011)
    public Down(Long id, Long playid, Long song_id, String name, String album_name,
            String album_id, String uri, String all_artist, String pic_url,
            String publish_time) {
        this.id = id;
        this.playid = playid;
        this.song_id = song_id;
        this.name = name;
        this.album_name = album_name;
        this.album_id = album_id;
        this.uri = uri;
        this.all_artist = all_artist;
        this.pic_url = pic_url;
        this.publish_time = publish_time;
    }
    @Generated(hash = 1079473298)
    public Down() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPlayid() {
        return this.playid;
    }
    public void setPlayid(Long playid) {
        this.playid = playid;
    }
    public Long getSong_id() {
        return this.song_id;
    }
    public void setSong_id(Long song_id) {
        this.song_id = song_id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAlbum_name() {
        return this.album_name;
    }
    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }
    public String getAlbum_id() {
        return this.album_id;
    }
    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }
    public String getUri() {
        return this.uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getAll_artist() {
        return this.all_artist;
    }
    public void setAll_artist(String all_artist) {
        this.all_artist = all_artist;
    }
    public String getPic_url() {
        return this.pic_url;
    }
    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }
    public String getPublish_time() {
        return this.publish_time;
    }
    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

}
