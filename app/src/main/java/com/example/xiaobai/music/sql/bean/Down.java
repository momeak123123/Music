package com.example.xiaobai.music.sql.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class Down {

    @Id(autoincrement = true)//设置自增长
    @Index(unique = true)//设置唯一性
    private Long id;
    private Long song_id;
    private String name;
    private String album_name;
    private Long album_id;
    private String uri;
    private String artist;
    private Long artist_id;
    private String pic_url;
    private String publish_time;
    private Long song_list_id;
    private String user_id;
    private String user;
    @Generated(hash = 383280572)
    public Down(Long id, Long song_id, String name, String album_name,
            Long album_id, String uri, String artist, Long artist_id,
            String pic_url, String publish_time, Long song_list_id, String user_id,
            String user) {
        this.id = id;
        this.song_id = song_id;
        this.name = name;
        this.album_name = album_name;
        this.album_id = album_id;
        this.uri = uri;
        this.artist = artist;
        this.artist_id = artist_id;
        this.pic_url = pic_url;
        this.publish_time = publish_time;
        this.song_list_id = song_list_id;
        this.user_id = user_id;
        this.user = user;
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
    public Long getAlbum_id() {
        return this.album_id;
    }
    public void setAlbum_id(Long album_id) {
        this.album_id = album_id;
    }
    public String getUri() {
        return this.uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public Long getArtist_id() {
        return this.artist_id;
    }
    public void setArtist_id(Long artist_id) {
        this.artist_id = artist_id;
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
    public Long getSong_list_id() {
        return this.song_list_id;
    }
    public void setSong_list_id(Long song_list_id) {
        this.song_list_id = song_list_id;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser() {
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }


}