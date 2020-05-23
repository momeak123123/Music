package com.example.music.sql.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class Music {

    @Id(autoincrement = true)//设置自增长
    @Index(unique = true)//设置唯一性
    private Long id;
    private String file;
    private String name;
    private String time;
    @Generated(hash = 1036063906)
    public Music(Long id, String file, String name, String time) {
        this.id = id;
        this.file = file;
        this.name = name;
        this.time = time;
    }
    @Generated(hash = 1263212761)
    public Music() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFile() {
        return this.file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

  
}
