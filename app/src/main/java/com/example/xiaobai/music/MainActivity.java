package com.example.xiaobai.music;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.xiaobai.music.config.SoftKeyBoardListener;
import com.example.xiaobai.music.music.view.act.MusicPlayActivity;
import com.example.xiaobai.music.music.view.act.StartPageActivity;
import com.example.xiaobai.music.music.view.fragment.FindFragment;
import com.example.xiaobai.music.music.view.fragment.HomeFragment;
import com.example.xiaobai.music.music.view.fragment.MyFragment;
import com.example.xiaobai.music.music.view.fragment.ShareFragment;
import com.example.xiaobai.music.service.MusicService;
import com.example.xiaobai.music.sql.config.Initialization;
import com.next.easynavigation.view.EasyNavigationBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Boolean dert = true;

    protected String[] needPermissions = {
            Manifest.permission.VIBRATE,
            Manifest.permission.BROADCAST_STICKY,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.CAMERA,
            Manifest.permission.DISABLE_KEYGUARD,

    };
    public static Boolean bool = true;

    private static final int PERMISSION_REQUESTED = 0;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity context;
    private boolean exit = false;
    @SuppressLint("StaticFieldLeak")
    private static EasyNavigationBar navigationBar;
    public static long mCurrentPlayTime;
    //未选中icon
    private static int[] normalIcon = {R.drawable.tab_icon_shouye_normal, R.drawable.tab_icon_faxian_normal, R.drawable.tab_icon_fenxiang_normal, R.drawable.tab_icon_me_normal};
    //选中时icon
    private static int[] selectIcon = {R.drawable.tab_icon_shouye_normals, R.drawable.tab_icon_faxian_normals, R.drawable.tab_icon_fenxiang_normals, R.drawable.tab_icon_me_normals};

    private static List<Fragment> fragments = new ArrayList<>();
    private static Handler mHandler = new Handler();
    private boolean flag = true;
    private static RadiusImageView custom;
    public static ObjectAnimator mObjectAnimator;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Initialization.setupDatabasePlaylist(this);
        Initialization.setupDatabaseDown(this);
        Initialization.setupDatabaseCollect(this);
        view = LayoutInflater.from(this).inflate(R.layout.tabmiddle, null);
        custom = view.findViewById(R.id.Custom);
        initData();

        if (bool) {
            Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
            startActivity(intent);
        }
        setAnimation();

    }

    private void initData() {


        String tab1 = getString(R.string.tab1);
        String tab2 = getString(R.string.tab2);
        String tab3 = getString(R.string.tab3);
        String tab4 = getString(R.string.tab4);
        String[] tabText = {tab1, tab2, tab3, tab4};

        navigationBar = findViewById(R.id.navigationBar);

        fragments.add(new HomeFragment());
        fragments.add(new FindFragment());
        fragments.add(new ShareFragment());
        fragments.add(new MyFragment());

        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .canScroll(true)
                .iconSize(20)     //Tab图标大小
                .tabTextSize(10)   //Tab文字大小
                .tabTextTop(5)     //Tab文字距Tab图标的距离
                .normalTextColor(Color.parseColor("#9dadb4"))   //Tab未选中时字体颜色
                .selectTextColor(Color.parseColor("#06b7ff"))   //Tab选中时字体颜色
                .scaleType(ImageView.ScaleType.CENTER_INSIDE)  //同 ImageView的ScaleType
                .navigationBackground(Color.parseColor("#21272e"))   //导航栏背景色
                .mode(EasyNavigationBar.NavigationMode.MODE_ADD_VIEW)
                .addCustomView(view)
                .fragmentManager(getSupportFragmentManager())
                .setOnTabClickListener(new EasyNavigationBar.OnTabClickListener() {
                    @Override
                    public boolean onTabSelectEvent(View view, int position) {
                        return false;
                    }

                    @Override
                    public boolean onTabReSelectEvent(View view, int position) {
                        return false;
                    }

                })
                .smoothScroll(true)  //点击Tab  Viewpager切换是否有动画
                .canScroll(true)    //Viewpager能否左右滑动
                .centerLayoutHeight(100)   //包含加号的布局高度 背景透明  所以加号看起来突出一块
                .navigationHeight(60)  //导航栏高度
                .lineColor(Color.parseColor("#000000"))
                .centerLayoutRule(EasyNavigationBar.RULE_BOTTOM) //RULE_CENTER 加号居中addLayoutHeight调节位置 EasyNavigationBar.RULE_BOTTOM 加号在导航栏靠下
                .centerLayoutBottomMargin(5)   //加号到底部的距离
                .hasPadding(true)    //true ViewPager布局在导航栏之上 false有重叠
                .centerAlignBottom(false)  //加号是否同Tab文字底部对齐  RULE_BOTTOM时有效；
                .textSizeType(EasyNavigationBar.TextSizeType.TYPE_DP)  //字体单位 建议使用DP  可切换SP
                .setOnCenterTabClickListener(new EasyNavigationBar.OnCenterTabSelectListener() {
                    @Override
                    public boolean onCenterTabSelectEvent(View view) {
                        if(MusicApp.getBool()){
                            Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
                            intent.putExtra("album_id", 0L);
                            intent.putExtra("pos", 0);
                            intent.putExtra("list", "");
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this, getText(R.string.play_no_resource), Toast.LENGTH_SHORT).show();
                        }

                        return false;
                    }
                })
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
       // onKeyBoardListener();
        exit = false;

        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }

    }

    /**
     * 设置旋转的动画
     */
    public static void updoteAnimation(String ima) {
        Glide.with(context).load(ima).placeholder(R.drawable.undetback).into(custom);
    }

    /**
     * 设置旋转的动画
     */
    public void setAnimation() {
        if (mObjectAnimator == null) {
            mObjectAnimator = ObjectAnimator.ofFloat(custom, "rotation", 0, 360);
            mObjectAnimator.setDuration(7200);
            mObjectAnimator.setInterpolator(new LinearInterpolator());
            mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
    }

    /**
     * 暂停旋转
     */
    public static void stopAnimation() {
        mCurrentPlayTime = mObjectAnimator.getCurrentPlayTime();
        mObjectAnimator.pause();
    }

    /**
     * 开始旋转
     */
    public static void startAnimation() {
        mObjectAnimator.start();
    }

    /**
     * 开始旋转
     */
    public static void resumeAnimation() {
        mObjectAnimator.resume();
        mObjectAnimator.setCurrentPlayTime(mCurrentPlayTime);
    }

    //监听软件盘是否弹起
    private void onKeyBoardListener() {
        SoftKeyBoardListener.setListener(MainActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                craet(true);
            }

            @Override
            public void keyBoardHide(int height) {
                craetdert(false);
                craet(false);
            }
        });

    }


    public static void craet(Boolean bools) {
        if (bools) {
            navigationBar.setVisibility(View.GONE);
        } else {
            if (!dert) {
                navigationBar.setVisibility(View.VISIBLE);
                View view = context.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }


        }
    }

    public static void craetdert(Boolean bools) {
        dert = bools;
    }


    /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    private void checkPermissions(String[] permissions) {
        //获取权限列表
        java.util.List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(new String[0]),
                    PERMISSION_REQUESTED);
        }

    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private java.util.List<String> findDeniedPermissions(String[] permissions) {
        java.util.List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSION_REQUESTED) {
            if (!verifyPermissions(paramArrayOfInt)) {      //没有授权
                showMissingPermissionDialog();              //显示提示信息
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }


    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }


    protected void onDestroy() {
        super.onDestroy();

        Intent intentservice = new Intent(this, MusicService.class);
        stopService(intentservice);


    }

    @Override
    public void onBackPressed() {
        if (exit) {
            System.exit(0);
        } else {
            exit = true;
            Toast.makeText(MainActivity.this, getText(R.string.main_set), Toast.LENGTH_SHORT).show();
        }

    }


}
