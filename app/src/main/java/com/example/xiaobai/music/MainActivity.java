package com.example.xiaobai.music;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.xiaobai.music.adapter.ViewPagerAdapter;
import com.example.xiaobai.music.config.SoftKeyBoardListener;
import com.example.xiaobai.music.music.view.act.StartPageActivity;
import com.example.xiaobai.music.music.view.fragment.FindFragment;
import com.example.xiaobai.music.music.view.fragment.HomeFragment;
import com.example.xiaobai.music.music.view.fragment.MyFragment;
import com.example.xiaobai.music.service.LockService;
import com.example.xiaobai.music.service.MusicService;
import com.example.xiaobai.music.sql.config.Initialization;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BadgeDismissListener, OnTabSelectListener {

    private static Boolean dert = true;
    private HomeFragment home = new HomeFragment();
    private FindFragment find = new FindFragment();
    private MyFragment my = new MyFragment();

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
    private ViewPager viewPager;
    @SuppressLint("StaticFieldLeak")
    private static JPTabBar mTabbar;
    private int indexs = 0;
    private static MainActivity context;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        viewPager = findViewById(R.id.viewPager);
        mTabbar = findViewById(R.id.tabbar);

        Initialization.setupDatabasePlaylist(this);
        Initialization.setupDatabaseDown(this);
        Initialization.setupDatabaseCollect(this);

        initData();

        if(bool){
            Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
            startActivity(intent);
        }


    }

    private void initData() {
        //

        List<Fragment> list = new ArrayList<>();
        mTabbar.setTitles(R.string.tab1, R.string.tab2, R.string.tab3)
                .setNormalIcons(R.drawable.tab_icon_shouye_normal, R.drawable.tab_icon_faxian_normal, R.drawable.tab_icon_me_normal)
                //.setSelectedIcons(R.mipmap.tab1_selected, R.mipmap.tab2_selected, R.mipmap.tab3_selected)
                .generate();
        mTabbar.setGradientEnable(true);
        mTabbar.setPageAnimateEnable(true);
        mTabbar.setTabListener(this);
        list.add(home);
        list.add(find);
        list.add(my);
        new Thread(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), list));
                mTabbar.setContainer(viewPager);

            }
        }).start();
        //设置Badge消失的代理
        mTabbar.setDismissListener(this);
        mTabbar.setTabListener(this);
    }


    @Override
    public void onDismiss(int position) {

    }


    @Override
    public void onTabSelect(int index) {

    }

    @Override
    public boolean onInterruptSelect(int index) {
        indexs = index;
//        if(index==2){
//            //如果这里有需要阻止Tab被选中的话,可以return true
//            return true;
//        }
        return false;
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
        onKeyBoardListener();
        exit = false;
        viewPager.setCurrentItem(indexs);

        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }

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
            mTabbar.setVisibility(View.GONE);
        } else {
            if(!dert){
                mTabbar.setVisibility(View.VISIBLE);
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
        if(exit){
            System.exit(0);
        }else{
            exit = true;
            Toast.makeText(MainActivity.this,  "再按一次退出", Toast.LENGTH_SHORT).show();
        }

    }


}
