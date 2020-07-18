package com.example.xiaobai.music

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.config.Constants
import com.example.xiaobai.music.config.LogDownloadListener
import com.example.xiaobai.music.music.model.MainModel
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.view.act.LoginActivity
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.example.xiaobai.music.music.view.act.StartPageActivity
import com.example.xiaobai.music.music.view.fragment.FindFragment
import com.example.xiaobai.music.music.view.fragment.HomeFragment
import com.example.xiaobai.music.music.view.fragment.MyFragment
import com.example.xiaobai.music.music.view.fragment.ShareFragment
import com.example.xiaobai.music.service.MusicService
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.config.Initialization
import com.example.xiaobai.music.sql.dao.mCollectDao
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.example.xiaobai.music.utils.FilesUtils
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.next.easynavigation.view.EasyNavigationBar
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.imageview.RadiusImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.play_list.*
import kotlinx.android.synthetic.main.popule.*
import kotlinx.android.synthetic.main.popule.relat1
import kotlinx.android.synthetic.main.popule.relat2
import kotlinx.android.synthetic.main.song_add.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class IndexActivity : AppCompatActivity() {


    companion object {

        var bool = true
        lateinit var observer: Observer<Boolean>
        lateinit var observert: Observer<Music>
        @SuppressLint("StaticFieldLeak")
        var mCurrentPlayTime: Long = 0
        lateinit var iv_cover: RadiusImageView
        var mObjectAnimator: ObjectAnimator? = null
        /**
         * 设置旋转的动画
         */
        fun updoteAnimation(ima: String?) {

            Glide.with(MusicApp.getAppContext()).load(ima).placeholder(R.drawable.undetback)
                    .into(iv_cover)

        }

        /**
         * 暂停旋转
         */
        fun stopAnimation() {
            mCurrentPlayTime = mObjectAnimator!!.currentPlayTime
            mObjectAnimator?.pause()
        }

        /**
         * 开始旋转
         */
        fun startAnimation() {
            mObjectAnimator?.start()
        }

        /**
         * 开始旋转
         */
        fun resumeAnimation() {
            mObjectAnimator?.resume()
            mObjectAnimator?.currentPlayTime = mCurrentPlayTime
        }
    }


     private var needPermissions = arrayOf(
        Manifest.permission.VIBRATE,
        Manifest.permission.BROADCAST_STICKY,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.CAMERA,
        Manifest.permission.DISABLE_KEYGUARD
    )

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true

    @SuppressLint("StaticFieldLeak")
    private lateinit var context:Context
    private var exit = false



    //未选中icon
    private val normalIcon = intArrayOf(
        R.drawable.tab_icon_shouye_normal,
        R.drawable.tab_icon_faxian_normal,
        R.drawable.tab_icon_fenxiang_normal,
        R.drawable.tab_icon_me_normal
    )

    //选中时icon
    private val selectIcon = intArrayOf(
        R.drawable.tab_icon_shouye_normals,
        R.drawable.tab_icon_faxian_normals,
        R.drawable.tab_icon_fenxiang_normals,
        R.drawable.tab_icon_me_normals
    )

    private val fragments = mutableListOf<Fragment>()

    private lateinit var view: View

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        Initialization.setupDatabasePlaylist(this)
        Initialization.setupDatabaseDown(this)
        Initialization.setupDatabaseCollect(this)
        view = LayoutInflater.from(this).inflate(R.layout.tabmiddle, null)
        iv_cover = view.findViewById(R.id.iv_cover)
        initData()
        context = this
        if (bool) {
            val intent = Intent(context, StartPageActivity::class.java)
            startActivity(intent)
        }
        setAnimation()
    }

    private fun initData() {
        val tab1 = getString(R.string.tab1)
        val tab2 = getString(R.string.tab2)
        val tab3 = getString(R.string.tab3)
        val tab4 = getString(R.string.tab4)
        val tabText = arrayOf(tab1, tab2, tab3, tab4)
        fragments.add(HomeFragment())
        fragments.add(FindFragment())
        fragments.add(ShareFragment())
        fragments.add(MyFragment())
        navigationBar.titleItems(tabText)
            .normalIconItems(normalIcon)
            .selectIconItems(selectIcon)
            .fragmentList(fragments)
            .canScroll(true)
            .iconSize(20f) //Tab图标大小
            .tabTextSize(10) //Tab文字大小
            .tabTextTop(5) //Tab文字距Tab图标的距离
            .normalTextColor(Color.parseColor("#9dadb4")) //Tab未选中时字体颜色
            .selectTextColor(Color.parseColor("#06b7ff")) //Tab选中时字体颜色
            .scaleType(ImageView.ScaleType.CENTER_INSIDE) //同 ImageView的ScaleType
            .navigationBackground(Color.parseColor("#21272e")) //导航栏背景色
            .mode(EasyNavigationBar.NavigationMode.MODE_ADD_VIEW)
            .addCustomView(view)
            .fragmentManager(supportFragmentManager)
            .setOnTabClickListener(object : EasyNavigationBar.OnTabClickListener {
                override fun onTabSelectEvent(view: View, position: Int): Boolean {
                    return false
                }

                override fun onTabReSelectEvent(
                    view: View,
                    position: Int
                ): Boolean {
                    return false
                }
            })
            .smoothScroll(true) //点击Tab  Viewpager切换是否有动画
            .canScroll(true) //Viewpager能否左右滑动
            .centerLayoutHeight(100) //包含加号的布局高度 背景透明  所以加号看起来突出一块
            .navigationHeight(60) //导航栏高度
            .lineColor(Color.parseColor("#000000"))
            .centerLayoutRule(EasyNavigationBar.RULE_BOTTOM) //RULE_CENTER 加号居中addLayoutHeight调节位置 EasyNavigationBar.RULE_BOTTOM 加号在导航栏靠下
            .centerLayoutBottomMargin(5) //加号到底部的距离
            .hasPadding(true) //true ViewPager布局在导航栏之上 false有重叠
            .centerAlignBottom(false) //加号是否同Tab文字底部对齐  RULE_BOTTOM时有效；
            .textSizeType(EasyNavigationBar.TextSizeType.TYPE_DP) //字体单位 建议使用DP  可切换SP
            .setOnCenterTabClickListener {
                if (MusicApp.getBool()) {
                    val intent =
                        Intent(context, MusicPlayActivity::class.java)
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 0)
                    intent.putExtra("list", "")
                    intent.putExtra("type", 0)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.play_no_resource),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                false
            }
            .build()
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()

        RxView.clicks(list_dow)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE

            }

        RxView.clicks(play_list_back)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE

            }


        RxView.clicks(in_deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (et_name.isNotEmpty) {
                    context.let { MainModel.addsonglist(it, et_name.editValue) }
                    et_name.clear()
                    in_add.visibility = View.GONE

                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.song_error_name),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        RxView.clicks(in_cancel)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_add.visibility = View.GONE

            }

    }

    override fun onStop() {
        super.onStop()
    }


    override fun onResume() {
        super.onResume()
        //onKeyBoardListener();
        exit = false
        if (isNeedCheck) {
            checkPermissions(needPermissions)
        }

        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if(data){
                    in_add.visibility = View.VISIBLE
                }else{
                    in_add.visibility = View.GONE
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }

        observert = object : Observer<Music> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n", "CheckResult")
            override fun onNext(data: Music) {
                poplue.visibility = View.VISIBLE

                edit_song.text =
                    getText(R.string.album).toString() + ":" + data.album_name
                var srtist_name = ""
                for (it in data.all_artist) {
                    if (srtist_name != "") {
                        srtist_name += "/" + it.name
                    } else {
                        srtist_name = it.name
                    }

                }
                artist_txt.text = getText(R.string.item3s).toString() + ":" + srtist_name
                RxView.clicks(popule_back)
                    .throttleFirst(3, TimeUnit.SECONDS)
                    .subscribe {
                        poplue.visibility = View.GONE

                    }

                RxView.clicks(relat1)
                    .throttleFirst(3, TimeUnit.SECONDS)
                    .subscribe {

                    }
                RxView.clicks(relat2)
                    .throttleFirst(3, TimeUnit.SECONDS)
                    .subscribe {

                    }

                RxView.clicks(relat3)
                    .throttleFirst(3, TimeUnit.SECONDS)
                    .subscribe {
                        val sp = getSharedPreferences("User", Context.MODE_PRIVATE)
                        if (sp.getBoolean("login", false)) {
                            poplue.visibility = View.GONE
                            in_indel.visibility = View.VISIBLE
                            context.let { it1 -> Glide.with(it1).load("").into(del) }
                            in_title.text = getText(R.string.song_but)
                            val list: MutableList<Playlist> = mPlaylistDao.querys(sp.getString("userid","").toString())
                            val idmap = mutableListOf<Music>()
                            idmap.add(data)
                            initSongLists(list, idmap)

                        } else {
                            context.let { it1 ->
                                MaterialDialog.Builder(it1)
                                    .title("登录")
                                    .content("未登陆账号，是否登录")
                                    .positiveText(getText(R.string.carry))
                                    .negativeText(getText(R.string.cancel))
                                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                        val intent = Intent()
                                        context.let {
                                            intent.setClass(it1, LoginActivity().javaClass)
                                        }
                                        startActivity(intent)
                                    }
                                    .show()
                            }
                        }

                    }



                val downs = mDownDao.querys(data.song_id)
                if (downs.size > 0) {
                    imageView5.setImageResource(R.drawable.xaidel)
                    dolw.text = getText(R.string.delete)
                }else{
                    imageView5.setImageResource(R.drawable.ic_file_download)
                    dolw.text = getText(R.string.song_download)
                }

                val collects = mCollectDao.querys(data.song_id)
                if (collects.size > 0) {
                    del_txt.text = getText(R.string.song_collectsucc)
                }else{
                    del_txt.text = getText(R.string.song_collect)
                }

                RxView.clicks(relat4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        if (MusicApp.network() == -1) {
                            Toast.makeText(
                                MusicApp.getAppContext(),
                                getText(R.string.error_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (downs.size == 0) {
                                if (MusicApp.userlogin()) {
                                    context.let { it1 ->
                                        MaterialDialog.Builder(it1)
                                            .title(getText(R.string.download_song))
                                            .content(getText(R.string.download_playsong))
                                            .positiveText(getText(R.string.carry))
                                            .negativeText(getText(R.string.cancel))
                                            .positiveColorRes(R.color.colorAccentDarkTheme)
                                            .negativeColorRes(R.color.red)
                                            .onPositive { _: MaterialDialog?, _: DialogAction? ->

                                                if(Constants.Downnum()){
                                                    val request = OkGo.get<File>(data.uri)
                                                    OkDownload.request(
                                                        data.uri,
                                                        request
                                                    ) //
                                                        .priority(0)
                                                        .folder(it1.getExternalFilesDir("")!!.absolutePath+"/download")
                                                        .fileName("music" + data.song_id ) //
                                                        .save() //
                                                        .register(
                                                            LogDownloadListener(
                                                                data,
                                                                context,
                                                                0,
                                                                downs
                                                            )
                                                        ) //
                                                        .start()
                                                }else{
                                                    Toast.makeText(
                                                        context,
                                                        getText(R.string.download_num),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }



                                            }
                                            .show()
                                    }
                                } else {
                                    context.let { it1 ->
                                        MaterialDialog.Builder(it1)
                                            .title(getText(R.string.go))
                                            .content(getText(R.string.ungoset))
                                            .positiveText(getText(R.string.carry))
                                            .negativeText(getText(R.string.cancel))
                                            .positiveColorRes(R.color.colorAccentDarkTheme)
                                            .negativeColorRes(R.color.red)
                                            .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                                val intent = Intent()
                                                context.let {
                                                    intent.setClass(
                                                        it,
                                                        LoginActivity().javaClass
                                                    )
                                                }
                                                startActivity(intent)
                                            }
                                            .show()
                                    }
                                }

                            } else {
                                context.let { it1 ->
                                    MaterialDialog.Builder(it1)
                                        .title(getText(R.string.song_delsong))
                                        .content(getText(R.string.song_delsongs))
                                        .positiveColorRes(R.color.colorAccentDarkTheme)
                                        .negativeColorRes(R.color.red)
                                        .positiveText(getText(R.string.carry))
                                        .negativeText(getText(R.string.cancel))
                                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                            mDownDao.delete(downs[0].id)
                                            FilesUtils.delFile(downs[0].uri)
                                            Toast.makeText(
                                                context,
                                                getText(R.string.song_delsongsucc),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .show()
                                }
                            }


                        }
                    }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }
    }


    /**
     * 初始化歌曲
     */
    private fun initSongLists(
        song: MutableList<Playlist>,
        idmap: MutableList<Music>
    ) {
        in_list.layoutManager = LinearLayoutManager(context)
        in_list.itemAnimator = DefaultItemAnimator()
        HomeFragment.adaptert = context.let { PlaySongAdapter(song, it) }
        in_list.adapter = HomeFragment.adaptert
        HomeFragment.adaptert.setOnItemClickListener(object : PlaySongAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                MaterialDialog.Builder(context)
                    .title("添加音乐")
                    .content("是否将音乐加入此歌单")
                    .positiveText(getText(R.string.carry))
                    .negativeText(getText(R.string.cancel))
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                        val playlist: Playlist = mPlaylistDao.query(song[position].play_list_id)[0]
                        val playsong = mCollectDao.query(song[position].play_list_id)
                        val songs = mutableListOf<Music>()
                        songs.addAll(idmap)
                        if (playsong.size > 0) {
                            if (idmap.size > 0) {
                                for (sea in idmap) {
                                    for (det in playsong) {
                                        if (sea.song_id == det.song_id) {
                                            songs.remove(sea)
                                        }
                                    }
                                }
                                if (songs.size > 0) {
                                    val num = (playlist.song_num.toInt() + songs.size).toString()
                                    MusicPlayModel.addSong(
                                        context,
                                        songs,
                                        num,
                                        song[position].play_list_id, 2, position
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        getText(R.string.play_mode),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }

                        } else {
                            if (songs.size > 0) {
                                val num = (playlist.song_num.toInt() + songs.size).toString()
                                MusicPlayModel.addSong(
                                    context,
                                    songs,
                                    num,
                                    song[position].play_list_id, 2, position
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    getText(R.string.play_mode),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }
                    .show()

            }
        })
    }


    /**
     * 设置旋转的动画
     */
    @SuppressLint("ObjectAnimatorBinding")
    fun setAnimation() {
        if (mObjectAnimator == null) {
            mObjectAnimator = ObjectAnimator.ofFloat(iv_cover, "rotation", 0f, 360f)
            mObjectAnimator?.duration = 7200
            mObjectAnimator?.interpolator = LinearInterpolator()
            mObjectAnimator?.repeatCount = ValueAnimator.INFINITE
        }
    }



    /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    private fun checkPermissions(permissions: Array<String>) {
        //获取权限列表
        val needRequestPermissonList =
            findDeniedPermissions(permissions)
        if (needRequestPermissonList.isNotEmpty()) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(
                this,
                needRequestPermissonList.toTypedArray(),
                0
            )
        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private fun findDeniedPermissions(permissions: Array<String>): List<String> {
        val needRequestPermissonList: MutableList<String> =
            ArrayList()
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm
                )
            ) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, paramArrayOfInt: IntArray
    ) {
        if (requestCode == 0) {
            if (!verifyPermissions(paramArrayOfInt)) {      //没有授权
                showMissingPermissionDialog() //显示提示信息
                isNeedCheck = false
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private fun showMissingPermissionDialog() {
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(R.string.notifyTitle)
        builder.setMessage(R.string.notifyMsg)

        // 拒绝, 退出应用
        builder.setNegativeButton(
            R.string.cancel
        ) { _, _ -> finish() }
        builder.setPositiveButton(
            R.string.setting
        ) { _, _ -> startAppSettings() }
        builder.setCancelable(false)
        builder.show()
    }


    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private fun startAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        )
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        val intentservice = Intent(this, MusicService::class.java)
        stopService(intentservice)
    }

    override fun onBackPressed() {
        if (exit) {
            System.exit(0)
        } else {
            exit = true
            Toast.makeText(context, getText(R.string.main_set), Toast.LENGTH_SHORT).show()
        }
    }

}
