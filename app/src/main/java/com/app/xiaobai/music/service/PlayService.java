package com.app.xiaobai.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.app.xiaobai.music.MusicApp;
import com.app.xiaobai.music.R;
import com.app.xiaobai.music.bean.Music;
import com.app.xiaobai.music.bean.musicpath;
import com.app.xiaobai.music.config.Cookie;
import com.app.xiaobai.music.config.Dencry;
import com.app.xiaobai.music.config.LogDownloadListeners;
import com.app.xiaobai.music.config.Notifications;
import com.app.xiaobai.music.music.view.act.MusicPlayActivity;
import com.app.xiaobai.music.utils.CipherUtil;
import com.google.gson.Gson;
import com.lzx.starrysky.StarrySky;
import com.lzx.starrysky.common.PlaybackStage;
import com.lzx.starrysky.control.RepeatMode;
import com.lzx.starrysky.provider.SongInfo;
import com.lzx.starrysky.utils.TimerTaskManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.operators.completable.CompletableTakeUntilCompletable;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

import static com.app.xiaobai.music.MusicApp.music;

public class PlayService extends Service {
    private int count = 0;
    private int style = 2;
    private int types = 0;
    private int ids = 0;
    private Long seek = 0L;
    private List<Music> playingMusicList;
    private int id = 0;
    private TimerTaskManager mTimerTask;
    public PlayService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimerTask = new TimerTaskManager();

        StarrySky.with().playbackState().observe((LifecycleOwner) this, playbackStage -> {
            if (playbackStage == null) {
                return;
            }
            switch (Objects.requireNonNull(playbackStage.getStage())) {
                case PlaybackStage.NONE:
                    //空状态
                    Observable.just(1).subscribe(MusicPlayActivity.observerplay);
                    break;
                case PlaybackStage.START:
                    //开始播放
                    MusicPlayActivity.load = "start";
                    mTimerTask.startToUpdateProgress();
                    long duration = StarrySky.with().getDuration();
                    Observable.just(duration).subscribe(MusicPlayActivity.observerui);
                    break;
                case PlaybackStage.PAUSE:
                    //暂停
                    MusicPlayActivity.load = "pause";
                    mTimerTask.stopToUpdateProgress();
                    Observable.just(1).subscribe(MusicPlayActivity.observerplay);
                    break;
                case PlaybackStage.STOP:
                    //停止
                    MusicPlayActivity.load = "stop";
                    mTimerTask.stopToUpdateProgress();
                    Observable.just(1).subscribe(MusicPlayActivity.observerplay);
                    break;
                case PlaybackStage.COMPLETION:
                    //播放完成
                    mTimerTask.stopToUpdateProgress();
                    if (StarrySky.with().isSkipToNextEnabled()) {
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                        if (MusicApp.getPlay()) {
                            StarrySky.with().stopMusic();
                        }
                        StarrySky.with().skipToNext();
                    }
                    break;
                case PlaybackStage.BUFFERING:
                    //缓冲中
                    break;
                case PlaybackStage.ERROR:
                    //播放出错
                    mTimerTask.stopToUpdateProgress();
                    Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                    break;
                default:
                    break;
            }
        });


        StarrySky.with().prepare();




        //设置更新回调
        mTimerTask.setUpdateProgressTask(() -> {
            long position = StarrySky.with().getPlayingPosition();
            long buffered = StarrySky.with().getBufferedPosition();

            Observable.just(position).subscribe(MusicPlayActivity.observerseek);
            Observable.just(buffered).subscribe(MusicPlayActivity.observerseeks);
        });
        //开始获取进度，一般可以在 onPlayerStart 中调用


        StarrySky.with().setRepeatMode(RepeatMode.REPEAT_MODE_NONE,true);


        List<Music> music = MusicPlayActivity.playingMusicList;
        List<SongInfo> infolist = new ArrayList<>();
        for (int i = 0; i < music.size(); i++) {
            SongInfo info = new SongInfo();
            info.setSongId("0");
            info.setSongUrl("");
            infolist.add(info);
        }

        StarrySky.with().playMusic(infolist, 0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("del");
        intentFilter.addAction("pre");
        intentFilter.addAction("play");
        intentFilter.addAction("next");
        intentFilter.addAction("uri");
        intentFilter.addAction("error");
        registerReceiver(broadcastReceiver, intentFilter);

        Intent intent = new Intent((Context) this, LockService.class);
        this.startService(intent);
        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= 26) {
            notificationChannel = new NotificationChannel("xiaobai1089", getText(R.string.app_name).toString(), NotificationManager.IMPORTANCE_MIN);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(this, "xiaobai1089")
                    .setContentTitle("This is content title")
                    .setContentText("This is content text")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setOnlyAlertOnce(true)
                    .build();

            this.startForeground(1, notification);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = Objects.requireNonNull(intent.getAction());
            if (string != null) {
                switch (string) {
                    case "del": {
                        stopForeground(true);
                        break;
                    }
                    case "pre": {
                        if (StarrySky.with().isSkipToPreviousEnabled()) {
                            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                            if (MusicApp.getPlay()) {
                                StarrySky.with().stopMusic();
                            }
                            StarrySky.with().skipToPrevious();
                        }
                        break;
                    }
                    case "play": {
                        Boolean bl = MusicApp.getPlay();
                        if (bl) {
                            StarrySky.with().pauseMusic();
                            Notifications.init(0);
                            Observable.just(3).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                        } else {
                            StarrySky.with().restoreMusic();
                            Notifications.init(1);
                            Observable.just(4).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                        }
                        break;
                    }
                    case "next": {
                        if (StarrySky.with().isSkipToNextEnabled()) {
                            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                            if (MusicApp.getPlay()) {
                                StarrySky.with().stopMusic();
                            }
                            StarrySky.with().skipToNext();
                        }
                        break;
                    }
                    case "uri": {

                        break;
                    }
                    case "error": {
                        Observable.just(5).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.Companion.getObserverplay());
                        break;
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        types = intent.getIntExtra("type", 0);
        ids = intent.getIntExtra("id", 0);
        count = intent.getIntExtra("count", 0);
        style = intent.getIntExtra("style", 0);
        seek = intent.getLongExtra("seek", 0);

        switch (types) {
            case 0: {
                try {
                    musicstart(ids);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
                if (StarrySky.with().isSkipToPreviousEnabled()) {
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                    if (MusicApp.getPlay()) {
                        StarrySky.with().stopMusic();
                    }
                    StarrySky.with().skipToPrevious();
                }
                break;
            }
            case 2: {
                if (StarrySky.with().isSkipToNextEnabled()) {
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                    if (MusicApp.getPlay()) {
                        StarrySky.with().stopMusic();
                    }
                    StarrySky.with().skipToNext();
                }

                break;
            }
            case 3: {
                StarrySky.with().pauseMusic();
                Notifications.init(0);
                Observable.just(3).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                break;
            }
            case 4: {
                StarrySky.with().restoreMusic();
                Notifications.init(1);
                Observable.just(4).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                break;
            }
            case 5: {
                StarrySky.with().seekTo(seek);
                break;
            }
            case 6: {
                switch (count) {
                    case 0:
                        StarrySky.with().setRepeatMode(RepeatMode.REPEAT_MODE_ONE, true);
                        break;
                    case 1:
                        StarrySky.with().setRepeatMode(RepeatMode.REPEAT_MODE_SHUFFLE, true);
                        break;
                    case 2:
                        StarrySky.with().setRepeatMode(RepeatMode.REPEAT_MODE_NONE, true);
                        break;
                }

                break;
            }
            case 7: {
                musicresme();
                break;
            }
            case 8: {
                try {
                    List<Music> music = MusicPlayActivity.playingMusicList;
                    List<SongInfo> infolist = new ArrayList<>();
                    for (int i = 0; i < music.size(); i++) {
                        SongInfo info = new SongInfo();
                        info.setSongId("0");
                        info.setSongUrl("");
                        infolist.add(info);
                    }
                    StarrySky.with().updatePlayList(infolist);
                    musicstart(ids);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void musicresme() {
        System.out.println("恢复");
        Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers);
        if (MusicApp.getPlay()) {
            Observable.just(StarrySky.with().getDuration()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerui);
        }

    }


    private void musicstart(int ids) throws Exception {
        System.out.println("切歌");
        if (MusicApp.getPlay()) {
            StarrySky.with().stopMusic();
        }
        MusicApp.setPosition(ids);
        id = ids;
        playingMusicList = MusicApp.getMusic();
        SongInfo info = new SongInfo();
        info.setSongId(String.valueOf(playingMusicList.get(ids).getSong_id()));
        info.setSongUrl(playingMusicList.get(ids).getUri());
        uriseat(info, playingMusicList.get(ids).getPublish_time(), this);
        Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observers);
    }

    private void uriseat(SongInfo info, String time, Context context) throws Exception {
        if (style == 1) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_connection),
                        Toast.LENGTH_SHORT
                ).show();
                Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
            } else {
                MusicPlayActivity.uri = info.getSongUrl();
                StarrySky.with().playMusicByInfo(info);
            }

        } else if (style == 3) {
            if (MusicApp.network() == -1) {
                Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_connection),
                        Toast.LENGTH_SHORT
                ).show();
                Observable.just(2).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
            } else {
                if (!time.equals("")) {
                    musicpath(info, time, Cookie.getCookie());
                } else {
                    Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(MusicPlayActivity.observerplay);
                }

            }
        } else if (style == 4) {
            info.setSongUrl(CipherUtil.decryptString(context, info.getSongUrl()));
            StarrySky.with().playMusicByInfo(info);
        }
    }

    private void musicpath(SongInfo info, String url, String cookie) {
        OkGo.<String>post(url)
                .params("cookie", cookie)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        /**
                         * 成功回调
                         */
                        try {
                            String ca = response.body().substring(7);
                            String da = ca.substring(0, ca.lastIndexOf('<'));
                            Gson gson = new Gson();
                            musicpath bean = gson.fromJson(da, musicpath.class);
                            String uri = Dencry.dencryptString(bean.getGeturl());
                            MusicPlayActivity.uri = uri;
                            info.setSongUrl(uri);
                            StarrySky.with().playMusicByInfo(info);
                            //prox(uri);

                        } catch (Exception ignored) {
                        }

                    }
                });

    }

    private void prox(String uri) {

        String path = this.getCacheDir().getAbsolutePath();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 20) {
                for (File f : files) {
                    try {
                        f.delete();
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        GetRequest request = OkGo.get(uri);
        OkDownload.request(uri, request)
                .priority(0)
                .folder(file.getAbsolutePath())
                .fileName(String.valueOf(System.currentTimeMillis()))
                .save()
                .register(new LogDownloadListeners())
                .start();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerTask.removeUpdateProgressTask();
        Intent lockservice = new Intent(this, LockService.class);
        stopService(lockservice);
        Notifications.deleteNotification();
    }
}
