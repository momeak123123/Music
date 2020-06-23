package com.example.xiaobai.music.sql.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Search {

    @Id(autoincrement = true)//设置自增长
    @Index(unique = true)//设置唯一性
    private Long id;

    private String txt;//内容

    private int state;//状态

    @Generated(hash = 1567670446)
    public Search(Long id, String txt, int state) {
        this.id = id;
        this.txt = txt;
        this.state = state;
    }

    @Generated(hash = 1644193961)
    public Search() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxt() {
        return this.txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
