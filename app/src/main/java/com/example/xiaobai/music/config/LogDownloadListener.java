package com.example.xiaobai.music.config;

import android.content.Context;
import android.widget.Toast;

import com.example.xiaobai.music.R;
import com.example.xiaobai.music.bean.Music;
import com.example.xiaobai.music.sql.bean.Down;
import com.example.xiaobai.music.sql.dao.mDownDao;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;
import java.util.List;

public class LogDownloadListener extends DownloadListener {

    private Music music;
    private Context context;
    private Long playid;
    private List<Down> down;
    private int type;
    public LogDownloadListener(Music musics, Context contexts,Long playids,List<Down> downs,int types) {
        super("LogDownloadListener");
        music = musics;
        context = contexts;
        playid = playids;
        down = downs;
        type = types;
    }

    @Override
    public void onStart(Progress progress) {
        System.out.println("onStart: " + progress);
        Toast.makeText(
                context,
                context.getText(R.string.download),
                Toast.LENGTH_SHORT
        ).show();
    }


    @Override
    public void onProgress(Progress progress) {
        //System.out.println("onProgress: " + progress);
    }

    @Override
    public void onError(Progress progress) {
        //System.out.println("onError: " + progress);
        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(File file, Progress progress) {

       if(type==0){
           for(int i=0;i<down.size();i++){
               down.get(i).setUri(file.getPath());
               down.get(i).setType(1);
               mDownDao.update(down.get(i));
           }
       }else{
           Down down = new Down();
           down.setAlbum_id(music.getAlbum_id());
           down.setAlbum_name(music.getAlbum_name());
           down.setArtist(music.getAll_artist().get(0).getName());
           down.setArtist_id(music.getAll_artist().get(0).getId());
           down.setName(music.getName());
           down.setPic_url(music.getPic_url());
           down.setPlayid(playid);
           down.setPublish_time(music.getPublish_time());
           down.setSong_id(music.getSong_id());
           down.setSong_list_id(music.getSong_list_id());
           down.setType(1);
           down.setUri(file.getPath());
           mDownDao.insert(down);
       }

        System.out.println("File: " + file.getPath());
    }

    @Override
    public void onRemove(Progress progress) {
        System.out.println("onRemove: " + progress);
    }
}

