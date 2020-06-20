package com.example.music.xiaobai;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.music.xiaobai.adapter.ViewPagerAdapter;
import com.example.music.xiaobai.common.Constants;
import com.example.music.xiaobai.music.model.MainModel;
import com.example.music.xiaobai.config.OkGoUpdateHttpUtil;
import com.example.music.xiaobai.music.view.act.StartPageActivity;
import com.example.music.xiaobai.music.view.fragment.FindFragment;
import com.example.music.xiaobai.music.view.fragment.HomeFragment;
import com.example.music.xiaobai.music.view.fragment.MyFragment;
import com.example.music.xiaobai.sql.config.Initialization;
import com.example.music.xiaobai.utils.CProgressDialogUtils;
import com.example.music.xiaobai.utils.HProgressDialogUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.service.DownloadService;
import com.vector.update_app.utils.AppUpdateUtils;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
            Manifest.permission.WAKE_LOCK

    };
    public static Boolean bool = true;

    private static final int PERMISSION_REQUESTED = 0;
    private static final int REQUEST_CODE = 1;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private ViewPager viewPager;
    private JPTabBar mTabbar;
    private static RelativeLayout relat1;
    private static RelativeLayout relat2;
    private static MaterialEditText et_name;
    private static TextView in_cancel;
    private static TextView in_deter;
    private int indexs = 0;
    private SharedPreferences sp;
    private String mUpdateUrl = Constants.URL + "Version/index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        mTabbar = findViewById(R.id.tabbar);
         relat1 = findViewById(R.id.relat1);
         relat2 = findViewById(R.id.relat2);
        et_name = findViewById(R.id.et_name);
        in_cancel = findViewById(R.id.in_cancel);
        in_deter = findViewById(R.id.in_deter);

        Initialization.setupDatabasePlaylist(this);
        Initialization.setupDatabaseDown(this);


        initView();
        craet(false);
        MainModel.Companion.homedata(this);

        //updateDiy();
    }



    @SuppressLint("CheckResult")
    private void initView(){
        RxView.clicks(in_deter)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    if(et_name.isNotEmpty()){
                        MainModel.Companion.addsonglist(this,et_name.getEditValue());
                        et_name.clear();
                        craet(false);
                    }else{
                        Toast.makeText(this, R.string.song_error_name, Toast.LENGTH_LONG).show();
                    }
                });
        RxView.clicks(in_cancel)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> {
                    craet(false);
                });

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
        if(bool){
            Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
            startActivity(intent);
        }
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = isHasInstallPermissionWithO(this);
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity(this);
            }
        }*/
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
            relat1.setVisibility(View.VISIBLE);
            relat2.setVisibility(View.VISIBLE);
        }else{
            relat1.setVisibility(View.GONE);
            relat2.setVisibility(View.GONE);
        }
    }


    public static String add(){
        return Objects.requireNonNull(et_name.getText()).toString();
    }


    /**
     * 自定义接口协议+自定义对话框+显示进度对话框
     *
     * @param
     */
    public void updateDiy() {

        isShowDownloadProgress = true;
        diyUpdate();
    }

    private void diyUpdate() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        SharedPreferences sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        Map<String, String> params = new HashMap<String, String>();

        params.put("appKey", token);
        params.put("appVersion", AppUpdateUtils.getVersionName(this));

        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(mUpdateUrl)

                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(true)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(path)

                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(jsonObject.optString("update"))
                                    //（必须）新版本号，
                                    .setNewVersion(jsonObject.optString("new_version"))
                                    //（必须）下载地址
                                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                                    //（必须）更新内容
                                    .setUpdateLog(jsonObject.optString("update_log"))
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(jsonObject.optString("target_size"))
                                    //是否强制更新，可以不设置
                                    .setConstraint(jsonObject.optBoolean("constraint"));
                                    //设置md5，可以不设置
                                   // .setNewMd5(jsonObject.optString("new_md5"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    /**
                     * 有新版本
                     *
                     * @param updateApp        新版本信息
                     * @param updateAppManager app更新管理器
                     */
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        //自定义对话框
                        showDiyDialog(updateApp, updateAppManager);
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }

                    /**
                     * 没有新版本
                     */
                    @Override
                    public void noNewApp(String error) {
                        Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 自定义对话框
     *
     * @param updateApp
     * @param updateAppManager
     */
    private void showDiyDialog(final UpdateAppBean updateApp, final UpdateAppManager updateAppManager) {
        String targetSize = updateApp.getTargetSize();
        String updateLog = updateApp.getUpdateLog();

        String msg = "";

        if (!TextUtils.isEmpty(targetSize)) {
            msg = "新版本大小：" + targetSize + "\n\n";
        }

        if (!TextUtils.isEmpty(updateLog)) {
            msg += updateLog;
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle(String.format("是否升级到%s版本？", updateApp.getNewVersion()))
                .setMessage(msg)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //显示下载进度
                        if (isShowDownloadProgress) {
                            updateAppManager.download(new DownloadService.DownloadCallback() {
                                @Override
                                public void onStart() {
                                    HProgressDialogUtils.showHorizontalProgressDialog(MainActivity.this, "下载进度", false);
                                }

                                /**
                                 * 进度
                                 *
                                 * @param progress  进度 0.00 -1.00 ，总大小
                                 * @param totalSize 总大小 单位B
                                 */
                                @Override
                                public void onProgress(float progress, long totalSize) {
                                    HProgressDialogUtils.setProgress(Math.round(progress * 100));
                                }

                                /**
                                 *
                                 * @param total 总大小 单位B
                                 */
                                @Override
                                public void setMax(long total) {

                                }


                                @Override
                                public boolean onFinish(File file) {
                                    HProgressDialogUtils.cancel();
                                    return true;
                                }

                                @Override
                                public void onError(String msg) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    HProgressDialogUtils.cancel();

                                }

                                @Override
                                public boolean onInstallAppAndAppOnForeground(File file) {
                                    return false;
                                }
                            });
                        } else {
                            //不显示下载进度
                            updateAppManager.download();
                        }


                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
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
