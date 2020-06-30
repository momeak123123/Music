package com.example.xiaobai.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.xiaobai.music.bean.Banner;
import com.example.xiaobai.music.music.model.MusicPlayModel;
import com.example.xiaobai.music.music.view.act.StartPageActivity;
import com.example.xiaobai.music.utils.BitmapUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class StartActivity extends AppCompatActivity {

    public static Observer<Boolean> observer;
    private Disposable mdDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        SharedPreferences sp = getSharedPreferences("User", Context.MODE_PRIVATE);

        String ads = sp.getString("ads", "");


        mdDisposable = Flowable.intervalRange(0, 5, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        MusicPlayModel.Companion.asd(StartActivity.this);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(it);
                        finish();//关闭当前活动
                    }
                })
                .subscribe();
    }


    @Override
    protected void onResume() {
        super.onResume();
        observer = new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean bool) {

               if(bool){
                   mdDisposable.dispose();
                   Intent it = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(it);
                   finish();//关闭当前活动
               }

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
