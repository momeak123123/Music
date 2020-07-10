package com.example.xiaobai.music.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.xiaobai.music.MusicApp;
import com.example.xiaobai.music.R;
import com.example.xiaobai.music.music.view.act.MusicPlayActivity;
import com.example.xiaobai.music.utils.BitmapUtils;

import java.io.File;


public class NotificationService extends Service {

    private Notification notification;

    public NotificationService() {
    }

    private static RemoteViews mRemoteViews;
    private static NotificationManager notificationManager = null;
    private static NotificationChannel channel;
    private static Context context;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();

        context = MusicApp.getAppContext();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            //创建 通知通道  channelid和channelname是必须的（自己命名就好）
            channel = new NotificationChannel("10898958", "小白音乐", NotificationManager.IMPORTANCE_DEFAULT);
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


    }

    private static RemoteViews getContentView(String title, String txt, Bitmap bitmap, int play) {
        mRemoteViews = new RemoteViews("com.example.xiaobai.music", R.layout.activity_notification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.txt, txt);
        mRemoteViews.setImageViewBitmap(R.id.ima, bitmap);
        mRemoteViews.setImageViewResource(R.id.pre, R.drawable.shang);
        mRemoteViews.setImageViewResource(R.id.next, R.drawable.xia);
        if(play==0){
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

    private static RemoteViews getContentViews( String title, String txt, Bitmap bitmap,int play) {
        mRemoteViews = new RemoteViews("com.example.xiaobai.music", R.layout.activity_bignotification);
        mRemoteViews.setTextViewText(R.id.title, title);
        mRemoteViews.setTextViewText(R.id.txt, txt);
        mRemoteViews.setImageViewBitmap(R.id.ima, bitmap);
        mRemoteViews.setImageViewResource(R.id.pre, R.drawable.shang);
        mRemoteViews.setImageViewResource(R.id.next, R.drawable.xia);
        if(play==0){
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int play = intent.getIntExtra("play",0);
        String  title = intent.getStringExtra("title");
        String txt = intent.getStringExtra("txt");
        int type = intent.getIntExtra("type",0);
        String bit = intent.getStringExtra("bitmap");

        if(type==0){
            new Thread(){
                @Override
                public void run() {
                    Bitmap bitmap = BitmapUtils.getBitmap(bit);
                    Intent resultIntent = new Intent(context, MusicPlayActivity.class);
                    resultIntent.setAction(Intent.ACTION_MAIN);
                    resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0x004, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // 设定点击通知之后启动的内容，这个内容由方法中的参数：PendingIntent对象决定
                    // 设置通知的优先级
                    // 设置点击通知之后通知是否消失
                    notification = new NotificationCompat.Builder(context, "1089")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setCustomContentView(getContentView(title, txt, bitmap, play))
                            .setCustomBigContentView(getContentViews(title, txt, bitmap, play))
                            .setContentIntent(resultPendingIntent) // 设定点击通知之后启动的内容，这个内容由方法中的参数：PendingIntent对象决定
                            .setPriority(NotificationCompat.PRIORITY_MAX) // 设置通知的优先级
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setSound(null)
                            .setChannelId("10898958")
                            .setOngoing(true)
                            .setVibrate(new long[]{0L})
                            .setAutoCancel(false) // 设置点击通知之后通知是否消失
                            .build();


                    notification.flags |= Notification.FLAG_NO_CLEAR;

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        notificationManager.createNotificationChannel(channel);
                    }
                    notificationManager.notify(1, notification);

                    startForeground(1,notification);
                }
            }.start();

        }else{
            deleteNotification();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
