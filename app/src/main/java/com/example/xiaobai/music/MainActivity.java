package com.example.xiaobai.music;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.example.xiaobai.music.adapter.ViewPagerAdapter;
import com.example.xiaobai.music.music.model.MainModel;
import com.example.xiaobai.music.music.model.MusicPlayModel;
import com.example.xiaobai.music.music.view.act.StartPageActivity;
import com.example.xiaobai.music.music.view.fragment.FindFragment;
import com.example.xiaobai.music.music.view.fragment.HomeFragment;
import com.example.xiaobai.music.music.view.fragment.MyFragment;
import com.example.xiaobai.music.sql.config.Initialization;
import com.example.xiaobai.music.R;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements BadgeDismissListener, OnTabSelectListener {

    private static final int REQUEST_INSTALL_PACKAGES = 10086;
    private boolean isShowDownloadProgress;
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
            Manifest.permission.CAMERA

    };
    public static Boolean bool = true;

    private static final int PERMISSION_REQUESTED = 0;
    private static final int REQUEST_CODE = 1;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private ViewPager viewPager;
    private static JPTabBar mTabbar;
    private static MaterialEditText et_name;
    private static TextView in_cancel;
    private static TextView in_deter;
    private int indexs = 0;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        mTabbar = findViewById(R.id.tabbar);
        et_name = findViewById(R.id.et_name);
        in_cancel = findViewById(R.id.in_cancel);
        in_deter = findViewById(R.id.in_deter);

        Initialization.setupDatabasePlaylist(this);
        Initialization.setupDatabaseDown(this);

        if(bool){
            Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
            startActivity(intent);

            Observable.timer(10, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable disposable) {

                        }

                        @Override
                        public void onNext(@NonNull Long number) {
                            MusicPlayModel.Companion.updateapp(getVersionName());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }


        MainModel.Companion.homedata(MainActivity.this);


    }



    private String getVersionName() {
        // 包管理器 可以获取清单文件信息
        PackageManager packageManager = getPackageManager();
        try {
            // 获取包信息
            // 参1 包名 参2 获取额外信息的flag 不需要的话 写0
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void initData() {
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
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), list));
        mTabbar.setContainer(viewPager);
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
        initData();

       /* blurLayout.setFPS(0);
        blurLayout.setBlurRadius(20);
        blurLayout.setDownscaleFactor(0.12f);
        blurLayout.startBlur();*/

    }

    @Override
    protected void onStop() {
        //blurLayout.pauseBlur();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setCurrentItem(indexs);

        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            boolean hasInstallPermission = isHasInstallPermissionWithO(this);
//            if (!hasInstallPermission) {
//                startInstallPermissionSettingActivity(this);
//            }
//        }
    }

    @RequiresApi (api = Build.VERSION_CODES.O)
    private boolean isHasInstallPermissionWithO(Context context){
        if (context == null){
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 开启设置安装未知来源应用权限界面
     * @param context
     */
    @RequiresApi (api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        if (context == null){
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        ((Activity)context).startActivityForResult(intent,REQUEST_INSTALL_PACKAGES);
    }
    
    public static void craet(Boolean bool){
        if(bool){
            mTabbar.setVisibility(View.VISIBLE);

        }else{
            mTabbar.setVisibility(View.GONE);
        }
    }



    public static String add(){
        return Objects.requireNonNull(et_name.getText()).toString();
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
    }

    @Override
    public void onBackPressed() {
       System.exit(0);
    }


}
