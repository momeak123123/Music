package com.example.xiaobai.music;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.xiaobai.music.bean.ApkModel;
import com.example.xiaobai.music.bean.Music;
import com.example.xiaobai.music.utils.NetWorkUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.xuexiang.xui.XUI;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.alterac.blurkit.BlurKit;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import update.UpdateAppUtils;

public class MusicApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static MusicApp sInstance;

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;


    public static Long ablumid = 0L;

    public static Bitmap startback;

    public static int position = 0;

    public static Boolean network = false;

    public static List<Music> music;

    public static Boolean play = false;


    public static Boolean getPlay() {
        return play;
    }

    public static void setPlay(Boolean play) {
        MusicApp.play = play;
    }

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        MusicApp.position = position;
    }

    public static List<Music> getMusic() {
        return music;
    }

    public static void setMusic(List<Music> music) {
        MusicApp.music = music;
    }

    public static Long getAblumid() {
        return ablumid;
    }

    public static void setAblumid(Long ablumid) {
        MusicApp.ablumid = ablumid;
    }

    public static Boolean getNetwork() {
        return network;
    }

    public static void setNetwork(Boolean network) {
        MusicApp.network = network;
    }

    public static Bitmap getStartback() {
        return startback;
    }

    public static void setStartback(Bitmap startback) {
        MusicApp.startback = startback;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sInstance = this;
        mContext = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateAppUtils.init(mContext);
                BlurKit.init(MusicApp.this);
                XUI.init(MusicApp.this); //初始化UI框架
                XUI.debug(false);  //开启UI框架调试日志
                OkGo.getInstance().init(MusicApp.this);//网络请求
                setNetwork(NetWorkUtils.isConnectedByState(mContext));
                initOkGo();
            }
        }).start();



        // 捕捉RxJava全局异常
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("异常处理"+throwable.getMessage());
            }
        });
    }

    public static synchronized MusicApp getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        //HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        // builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                 //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }

}
