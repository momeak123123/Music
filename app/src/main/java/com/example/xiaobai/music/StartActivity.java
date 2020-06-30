package com.example.xiaobai.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.xiaobai.music.bean.Banner;
import com.example.xiaobai.music.music.model.MusicPlayModel;
import com.example.xiaobai.music.utils.BitmapUtils;

import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class StartActivity extends AppCompatActivity {

    public static Observer<Bitmap> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicPlayModel.Companion.asd(this);
        observer = new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Bitmap bool) {

                MusicApp.setStartback(bool);
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                finish();//关闭当前活动
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        };
    }
}
