package com.example.xiaobai.music;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xiaobai.music.bean.Music;
import com.example.xiaobai.music.music.view.act.MusicPlayActivity;
import com.example.xiaobai.music.music.view.custom.SlidingFinishLayout;
import com.example.xiaobai.music.utils.BitmapUtils;
import com.jakewharton.rxbinding2.view.RxView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.alterac.blurkit.BlurKit;
import io.alterac.blurkit.BlurLayout;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class LockActivity extends AppCompatActivity implements SlidingFinishLayout.OnSlidingFinishListener {

    private TextView tvLockTime;
    private TextView tvLockDate;
    private TextView songNameTv;

    private TextView singerTv;

    private ImageView iv_audio;

    private ImageView prevIv;

    private ImageView playPauseIv;

    private ImageView nextIv;

    private ImageView back;

    public static Observer<Integer> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        fullScreen(this);
        setContentView(R.layout.activity_lock);
        initView();
        MusicPlayActivity.lock = "0";

    }

    public void fullScreen(Activity activity) {
        {

            Window window = activity.getWindow();
            View decorView = window.getDecorView();

            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }
    }

    @SuppressLint("CheckResult")
    private void initView() {
        tvLockTime = findViewById(R.id.lock_time);
        tvLockDate = findViewById(R.id.lock_date);
        songNameTv = findViewById(R.id.tv_audio_name);
        singerTv = findViewById(R.id.tv_audio);
        iv_audio = findViewById(R.id.iv_audio);
        prevIv = findViewById(R.id.prevIv);
        playPauseIv = findViewById(R.id.playPauseIv);
        nextIv = findViewById(R.id.nextIv);
        back = findViewById(R.id.back);

        RxView.clicks(prevIv)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Observable.just(1).subscribe(MusicPlayActivity.observerset);

                });
        RxView.clicks(playPauseIv)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if (MusicApp.getPlay()) {
                        Observable.just(0).subscribe(MusicPlayActivity.observerset);
                        playPauseIv.setImageResource(R.drawable.play);
                    } else {
                        Observable.just(3).subscribe(MusicPlayActivity.observerset);
                        playPauseIv.setImageResource(R.drawable.plays);
                    }
                });
        RxView.clicks(nextIv)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Observable.just(2).subscribe(MusicPlayActivity.observerset);

                });


        SlidingFinishLayout vLockRoot = findViewById(R.id.lock_root);
        vLockRoot.setOnSlidingFinishListener(this);
        data();
    }

    public void data() {
        songNameTv.setText(MusicPlayActivity.t1);
        singerTv.setText(MusicPlayActivity.t2);
        if (MusicApp.getPlay()) {
            playPauseIv.setImageResource(R.drawable.plays);
        } else {
            playPauseIv.setImageResource(R.drawable.play);
        }
        iv_audio.setImageBitmap(MusicPlayActivity.m1);
        back.setImageBitmap(MusicPlayActivity.m2);
    }


    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();
        Flowable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm-M月dd日 E", Locale.CHINESE);
                        String[] date = simpleDateFormat.format(new Date()).split("-");
                        tvLockTime.setText(date[0]);
                        tvLockDate.setText(date[1]);
                        data();
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * 滑动销毁锁屏页面
     */
    @Override
    public void onSlidingFinish() {
        finish();
        MusicPlayActivity.lock = "1";
    }

    @Override
    public void onBackPressed() {
    }

    //监听系统的物理按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        } else if (keyCode == KeyEvent.KEYCODE_HOME) {

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {

        }
        return true;
    }
}
