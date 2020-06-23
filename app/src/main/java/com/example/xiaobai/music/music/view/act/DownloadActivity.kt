package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.DownloadListAdapter
import com.example.xiaobai.music.bean.ApkModel
import com.example.xiaobai.music.music.contract.DownloadContract
import com.example.xiaobai.music.music.presenter.DownloadPresenter
import com.example.xiaobai.music.utils.FileUtils
import com.lzy.okgo.db.DownloadManager
import com.lzy.okserver.OkDownload
import kotlinx.android.synthetic.main.activity_download_list.*
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/20
 * @Description input description
 **/

@SuppressLint("Registered")
class DownloadActivity : BaseMvpActivity<DownloadContract.IPresenter>() , DownloadContract.IView {

    override fun registerPresenter() = DownloadPresenter::class.java
    private lateinit var context: Context

    private var adapter: DownloadListAdapter? = null
    private var apks = mutableListOf<ApkModel>()
    override fun getLayoutId(): Int {
      return R.layout.activity_download_list
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        initData()
        OkDownload.getInstance().folder = FileUtils.getMusicDir()

        OkDownload.getInstance().threadPool.setCorePoolSize(3)

        folder.text = String.format("下载路径: %s", OkDownload.getInstance().folder)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        val progressList =
            DownloadManager.getInstance().all
        OkDownload.restore(progressList)
        adapter = DownloadListAdapter(MusicApp.getDownmusic(),context)
        recyclerView.adapter = adapter

    }

    override fun initData() {


        val apk1 = ApkModel("爱奇艺", "http://121.29.10.1/f5.market.mi-img.com/download/AppStore/0b8b552a1df0a8bc417a5afae3a26b2fb1342a909/com.qiyi.video.apk",0)
        apks.add(apk1)
        val apk2 = ApkModel( "微信","http://116.117.158.129/f2.market.xiaomi.com/download/AppStore/04275951df2d94fee0a8210a3b51ae624cc34483a/com.tencent.mm.apk",0)
        apks.add(apk2)
        val apk3 = ApkModel("新浪微博","http://60.28.125.129/f1.market.xiaomi.com/download/AppStore/0ff41344f280f40c83a1bbf7f14279fb6542ebd2a/com.sina.weibo.apk",0)
        apks.add(apk3)
        MusicApp.setDownmusic(apks)
        adapter?.notifyDataSetChanged()

    }
}
