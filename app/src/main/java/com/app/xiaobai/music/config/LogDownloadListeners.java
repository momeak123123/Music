package com.app.xiaobai.music.config;

import android.app.PendingIntent;
import android.content.Intent;

import com.app.xiaobai.music.MusicApp;
import com.app.xiaobai.music.R;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;

public class LogDownloadListeners extends DownloadListener {


    public LogDownloadListeners() {
        super("LogDownloadListener");


    }

    @Override
    public void onStart(Progress progress) {
        System.out.println("onStart: " + progress);
    }


    @Override
    public void onProgress(Progress progress) {
        System.out.println("onProgress: " + progress);
    }

    @Override
    public void onError(Progress progress) {
        System.out.println("onError: " + progress);

        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(File file, Progress progress) {
        System.out.println("File: " + file.getPath());
        MusicApp.setUri(file.getPath());
        Intent pIntent = new Intent("uri");
        MusicApp.getAppContext().sendBroadcast(pIntent);
    }

    @Override
    public void onRemove(Progress progress) {
        System.out.println("onRemove: " + progress);
    }

}


