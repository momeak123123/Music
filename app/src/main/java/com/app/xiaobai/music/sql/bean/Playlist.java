package com.app.xiaobai.music.sql.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class Playlist {

    @Id(autoincrement = true)//设置自增长
    @Index(unique = true)//设置唯一性
    private Long id;
    private Long play_list_id;
    private String name;
    private String pic_url;
    private String user_id;
    private String create_time;
    private String song_num;

    @Generated(hash = 874975770)
    public Playlist(Long id, Long play_list_id, String name, String pic_url,
            String user_id, String create_time, String song_num) {
        this.id = id;
        this.play_list_id = play_list_id;
        this.name = name;
        this.pic_url = pic_url;
        this.user_id = user_id;
        this.create_time = create_time;
        this.song_num = song_num;
    }
    @Generated(hash = 1160175056)
    public Playlist() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPlay_list_id() {
        return this.play_list_id;
    }
    public void setPlay_list_id(Long play_list_id) {
        this.play_list_id = play_list_id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPic_url() {
        return this.pic_url;
    }
    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getSong_num() {
        return this.song_num;
    }
    public void setSong_num(String song_num) {
        this.song_num = song_num;
    }
   

}
