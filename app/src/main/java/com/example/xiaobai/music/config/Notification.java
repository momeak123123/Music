package com.example.xiaobai.music.config;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.xiaobai.music.MusicApp;
import com.example.xiaobai.music.R;
import com.example.xiaobai.music.music.view.act.MusicPlayActivity;

import java.net.URL;

public class Notification extends android.app.Notification {

    private static RemoteViews mRemoteViews;
    private static NotificationManager notificationManager = null;
    private static NotificationChannel channel;
    private static Context context;

    public static void init(String titles, String txts, Bitmap bitmaps,int types){
        context = MusicApp.getAppContext();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            //创建 通知通道  channelid和channelname是必须的（自己命名就好）
             channel = new NotificationChannel("1089", "小白音乐", NotificationManager.IMPORTANCE_DEFAULT);
        }
        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //向下兼容 用NotificationCompat.Builder构造notification对象
          /* notification = new NotificationCompat.Builder(mContext,"1089")
                    .setContentTitle(title)
                    .setContentText(txt)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();*/

        //处理点击Notification的逻辑 返回正在运行的activity

        Intent resultIntent = new Intent(context, MusicPlayActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0x004, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 设定点击通知之后启动的内容，这个内容由方法中的参数：PendingIntent对象决定
        // 设置通知的优先级
        // 设置点击通知之后通知是否消失
        android.app.Notification notification = new NotificationCompat.Builder(context, "1089")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(getContentView(titles, txts, bitmaps, types))
                .setCustomBigContentView(getContentViews(titles, txts, bitmaps, types))
                .setContentIntent(resultPendingIntent) // 设定点击通知之后启动的内容，这个内容由方法中的参数：PendingIntent对象决定
                .setPriority(NotificationCompat.PRIORITY_LOW) // 设置通知的优先级
                .setDefaults(NotificationCompat.FLAG_NO_CLEAR)
                .setSound(null)
                .setVibrate(new long[]{0L})
                .setAutoCancel(false) // 设置点击通知之后通知是否消失
                .build();



        notification.flags |= Notification.FLAG_NO_CLEAR;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification);
    }


    private static RemoteViews getContentView( String title, String txt, Bitmap bitmap,int type) {
        mRemoteViews = new RemoteViews("com.example.xiaobai.music", R.layout.activity_notification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.txt, txt);
        mRemoteViews.setImageViewBitmap(R.id.ima, bitmap);
        mRemoteViews.setImageViewResource(R.id.pre, R.drawable.shang);
        mRemoteViews.setImageViewResource(R.id.next, R.drawable.xia);
        if(type==0){
            mRemoteViews.setImageViewResource(R.id.play, R.drawable.play);
        }else{
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

    private static RemoteViews getContentViews( String title, String txt, Bitmap bitmap,int type) {
        mRemoteViews = new RemoteViews("com.example.xiaobai.music", R.layout.activity_bignotification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.txt, txt);
        mRemoteViews.setImageViewBitmap(R.id.ima, bitmap);
        mRemoteViews.setImageViewResource(R.id.pre, R.drawable.shang);
        mRemoteViews.setImageViewResource(R.id.next, R.drawable.xia);
        if(type==0){
            mRemoteViews.setImageViewResource(R.id.play, R.drawable.play);
        }else{
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
        if(notificationManager!=null){
            notificationManager.cancel(1);
        }

    }
}
