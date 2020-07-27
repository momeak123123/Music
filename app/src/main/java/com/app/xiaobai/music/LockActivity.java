package com.app.xiaobai.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.app.xiaobai.music.music.view.act.MusicPlayActivity;
import com.app.xiaobai.music.music.view.custom.SlidingFinishLayout;
import com.jakewharton.rxbinding2.view.RxView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class LockActivity extends AppCompatActivity implements SlidingFinishLayout.OnSlidingFinishListener {

    @SuppressLint("StaticFieldLeak")
    private static TextView tvLockTime;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvLockDate;
    @SuppressLint("StaticFieldLeak")
    private static TextView songNameTv;
    @SuppressLint("StaticFieldLeak")
    private static TextView singerTv;
    @SuppressLint("StaticFieldLeak")
    private static ImageView iv_audio;
    @SuppressLint("StaticFieldLeak")
    private static ImageView playPauseIv;
    @SuppressLint("StaticFieldLeak")
    private static ImageView back;
    @SuppressLint("StaticFieldLeak")
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        fullScreen(this);
        setContentView(R.layout.activity_lock);
        MusicApp.setLock(true);
        context = this;
        initView();



        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    public void fullScreen(Activity activity) {

        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);



    }


    @SuppressLint("CheckResult")
    private void initView() {
        tvLockTime = findViewById(R.id.lock_time);
        tvLockDate = findViewById(R.id.lock_date);
        songNameTv = findViewById(R.id.tv_audio_name);
        singerTv = findViewById(R.id.tv_audio);
        iv_audio = findViewById(R.id.iv_audio);
        ImageView prevIv = findViewById(R.id.prevIv);
        playPauseIv = findViewById(R.id.playPauseIv);
        ImageView nextIv = findViewById(R.id.nextIv);
        back = findViewById(R.id.back);

        songNameTv.setText(MusicPlayActivity.t1);
        singerTv.setText(MusicPlayActivity.t2);
        playPauseIv.setImageResource(R.drawable.plays);
        Glide.with(context).load(MusicPlayActivity.m).placeholder(R.drawable.undetback).into(iv_audio);
        Glide.with(context).load(MusicPlayActivity.m).placeholder(R.drawable.undetback).apply(bitmapTransform(new BlurTransformation(25, 3))).into(back);

        RxView.clicks(prevIv)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Observable.just(1).subscribe(MusicPlayActivity.observerset);

                });
        RxView.clicks(playPauseIv)
                .throttleFirst(0, TimeUnit.SECONDS)
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
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    Observable.just(2).subscribe(MusicPlayActivity.observerset);

                });


        SlidingFinishLayout vLockRoot = findViewById(R.id.lock_root);
        vLockRoot.setOnSlidingFinishListener(this);
    }

    public static void data() {

        songNameTv.setText(MusicPlayActivity.t1);
        singerTv.setText(MusicPlayActivity.t2);
        playPauseIv.setImageResource(R.drawable.plays);
        Glide.with(context).load(MusicPlayActivity.m).placeholder(R.drawable.undetback).into(iv_audio);
        Glide.with(context).load(MusicPlayActivity.m).apply(bitmapTransform(new BlurTransformation(25, 3))).into(back);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        data();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.equals(action, Intent.ACTION_TIME_TICK)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm-M月dd日 E", Locale.CHINESE);
                String[] date = simpleDateFormat.format(new Date()).split("-");
                tvLockTime.setText(date[0]);
                tvLockDate.setText(date[1]);
            }
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
        MusicApp.setLock(false);
        finish();

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
