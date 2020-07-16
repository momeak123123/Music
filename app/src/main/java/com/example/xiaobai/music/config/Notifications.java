package com.example.xiaobai.music.config;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.xiaobai.music.MusicApp;
import com.example.xiaobai.music.R;
import com.example.xiaobai.music.music.view.act.MusicPlayActivity;
import com.example.xiaobai.music.utils.BitmapUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notifications extends android.app.Notification {

    private static final String PUSH_CHANNEL_ID = "xiaobai1089";
    private static final String PUSH_CHANNEL_NAME = "小白音乐";
    private static RemoteViews mRemoteViews;
    private static NotificationManager notificationManager = null;
    private static NotificationChannel channel;
    private static Context context;
    private static String title;
    private static String txt;
    private static Bitmap bitmap;
    private static android.app.Notification notification;
    private static int play;

    public static void init(int plays) {
        context = MusicApp.getAppContext();
        play = plays;
        title = MusicPlayActivity.t1;
        txt = MusicPlayActivity.t2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            channel.enableVibration(false);
            channel.enableLights(true);
            channel.setSound(null, null);
        }

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //向下兼容 用NotificationCompat.Builder构造notification对象
          /* notification = new NotificationCompat.Builder(mContext,"1089")
                    .setContentTitle(title)
                    .setContentText(txt)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();*/

        //处理点击Notification的逻辑 返回正在运行的activity
        new Thread() {
            @Override
            public void run() {

                bitmap = BitmapUtils.getBitmap(MusicPlayActivity.m);
                Intent resultIntent = new Intent(context, MusicPlayActivity.class);
                resultIntent.putExtra("album_id", 0L);
                resultIntent.putExtra("pos", 0);
                resultIntent.putExtra("list", "");
                resultIntent.putExtra("type", 0);
                resultIntent.setAction(Intent.ACTION_MAIN);
                resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0x004, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // 设定点击通知之后启动的内容，这个内容由方法中的参数：PendingIntent对象决定
                // 设置通知的优先级
                // 设置点击通知之后通知是否消失
                notification = new NotificationCompat.Builder(context, PUSH_CHANNEL_ID)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCustomContentView(getContentView())
                        .setCustomBigContentView(getContentViews())
                        .setContentIntent(resultPendingIntent) // 设定点击通知之后启动的内容，这个内容由方法中的参数：PendingIntent对象决定
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 设置通知的优先级
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setChannelId(PUSH_CHANNEL_ID)
                        .setOngoing(true)
                        .setSound(null)
                        .setVibrate(new long[]{0L})
                        .setAutoCancel(false) // 设置点击通知之后通知是否消失
                        .build();


                notification.flags |= android.app.Notification.FLAG_NO_CLEAR;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(1, notification);

            }
        }.start();

    }

    private static RemoteViews getContentView() {
        mRemoteViews = new RemoteViews("com.example.xiaobai.music", R.layout.activity_notification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.txt, txt);
        mRemoteViews.setImageViewBitmap(R.id.ima, bitmap);
        mRemoteViews.setImageViewResource(R.id.pre, R.drawable.shang);
        mRemoteViews.setImageViewResource(R.id.next, R.drawable.xia);
        if (play == 0) {
            mRemoteViews.setImageViewResource(R.id.play, R.drawable.play);
        } else {
            mRemoteViews.setImageViewResource(R.id.play, R.drawable.plays);
        }

        //主页面广播通知
        mRemoteViews.setOnClickPendingIntent(R.id.del, getActivityPendingIntent("del"));

        //跳转其他界面
        /*Intent notifyIntent = new Intent();
        notifyIntent.setClass(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notifyIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);*/


        mRemoteViews.setOnClickPendingIntent(R.id.pre, getActivityPendingIntent("pre"));
        mRemoteViews.setOnClickPendingIntent(R.id.play, getActivityPendingIntent("play"));
        mRemoteViews.setOnClickPendingIntent(R.id.next, getActivityPendingIntent("next"));


        return mRemoteViews;
    }

    private static RemoteViews getContentViews() {
        mRemoteViews = new RemoteViews("com.example.xiaobai.music", R.layout.activity_bignotification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.txt, txt);
        mRemoteViews.setImageViewBitmap(R.id.ima, bitmap);
        mRemoteViews.setImageViewResource(R.id.pre, R.drawable.shang);
        mRemoteViews.setImageViewResource(R.id.next, R.drawable.xia);
        if (play == 0) {
            mRemoteViews.setImageViewResource(R.id.play, R.drawable.play);
        } else {
            mRemoteViews.setImageViewResource(R.id.play, R.drawable.plays);
        }

        //主页面广播通知
        mRemoteViews.setOnClickPendingIntent(R.id.del, getActivityPendingIntent("del"));

        //跳转其他界面
        /*Intent notifyIntent = new Intent();
        notifyIntent.setClass(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notifyIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);*/


        mRemoteViews.setOnClickPendingIntent(R.id.pre, getActivityPendingIntent("pre"));
        mRemoteViews.setOnClickPendingIntent(R.id.play, getActivityPendingIntent("play"));
        mRemoteViews.setOnClickPendingIntent(R.id.next, getActivityPendingIntent("next"));


        return mRemoteViews;
    }

    /**
     * 获取一个Activity类型的PendingIntent对象
     */
    private static PendingIntent getActivityPendingIntent(String event) {
        Intent pIntent = new Intent(event);
        return PendingIntent.getBroadcast(context, R.string.app_name, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //清除通知栏消息
    public static void deleteNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(1);
        }

    }
}

