package com.example.music.config;

import android.content.Context;

import com.example.music.bean.Music;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;

public class LogDownloadListener extends DownloadListener {

    public LogDownloadListener(Music apk, Context context) {
        super("LogDownloadListener");

    }

    @Override
    public void onStart(Progress progress) {
        System.out.println("onStart: " + progress);

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

    }

    @Override
    public void onRemove(Progress progress) {
        System.out.println("onRemove: " + progress);
    }
}

