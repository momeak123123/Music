package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.R
import com.app.xiaobai.music.adapter.SearchListAdapter
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.Searchs
import com.app.xiaobai.music.bean.artistlist
import com.app.xiaobai.music.music.contract.SearchListContract
import com.app.xiaobai.music.music.presenter.SearchListPresenter
import com.app.xiaobai.music.sql.config.Initialization
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.search_index.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListActivity : BaseMvpActivity<SearchListContract.IPresenter>(),
    SearchListContract.IView {

    companion object {
        lateinit var observer: Observer<JsonArray>
        lateinit var observers: Observer<Boolean>
    }
    private var add: Int = 0
    val musicall = mutableListOf<Searchs>()
    val datas = mutableListOf<Searchs>()
    private lateinit var search: String
    private lateinit var context: Context
    private lateinit var adapter: SearchListAdapter
    private var type = 0
    override fun registerPresenter() = SearchListPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.search_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        Initialization.setupDatabaseSearch(this)
        context = this
        top_title.text = getText(R.string.search)
    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        search = bundle?.get("txt") as String
        type = bundle.get("sear") as Int
        add=0

        when (type) {
            0 -> getPresenter().qqdata(context, search, add)
            1 -> getPresenter().kugoudata(context, search, add)
        }
        swipe_refresh_layout.isRefreshing = true
        initSearchList(musicall)

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            when (type) {
                0 -> getPresenter().qqdata(context, search, 0)
                1 -> getPresenter().kugoudata(context, search, 0)
            }
        })

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }


    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(list: JsonArray) {
                add++

                when (type) {
                    0 -> {
                        for (i in 0 until list.size()) {

                            val music = list.get(i)
                            var jsonObj: JsonObject? = null
                            if (music.isJsonObject) {
                                jsonObj = music.asJsonObject
                            }
                            val midest = jsonObj!!.get("mid").asString
                            var mid = ""
                            if (midest != "") {
                                mid =
                                    "http://symusic.top/music.php?source=tencent&types=url&mid=$midest&br=hq"
                            }

                            val title = jsonObj.get("title").asString
                            val id = jsonObj.get("id").asLong
                            val album = jsonObj.get("album").asJsonObject
                            val album_id = album.get("id").asLong
                            val album_name = album.get("name").asString
                            val album_pmid =
                                "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + album.get(
                                    "pmid"
                                ).asString + ".jpg"
                            val one = mutableListOf<artistlist>()
                            val singer = jsonObj.get("singer").asJsonArray
                            for (e in 0 until singer.size()) {
                                val artist = singer.get(e)
                                var jsonOs: JsonObject? = null
                                if (artist.isJsonObject) {
                                    jsonOs = artist.asJsonObject
                                }
                                one.add(
                                    artistlist(
                                        jsonOs!!.get("id").asLong,
                                        jsonOs.get("name").asString
                                    )
                                )
                            }

                            musicall.add(
                                Searchs(
                                    1,
                                    Music(
                                        title,
                                        album_name,
                                        album_id,
                                        id,
                                        "",
                                        one,
                                        album_pmid,
                                        0,
                                        mid
                                    )
                                )

                            )
                        }
                       adapter.add(musicall)
                    }
                    1 -> {
                        for (i in 0 until list.size()) {

                            val music = list.get(i)
                            var jsonObj: JsonObject? = null
                            if (music.isJsonObject) {
                                jsonObj = music.asJsonObject
                            }
                            val midhq = jsonObj!!.get("HQFileHash").asString
                            val midsq = jsonObj.get("SQFileHash").asString
                            var mid = ""
                            if (midhq != "") {
                                mid =
                                    "http://symusic.top/music.php?source=kugou&types=url&mid=$midhq&br=hq"
                            } else {
                                if (midsq != "") {
                                    mid =
                                        "http://symusic.top/music.php?source=kugou&types=url&mid=$midsq&br=sq"
                                }
                            }
                            val title = jsonObj.get("SongName").asString
                            var ids = jsonObj.get("ID").asString
                            if (ids == "") {
                                ids = "0"
                            }
                            val id = ids.toLong()
                            var album = jsonObj.get("AlbumID").asString
                            if (album == "") {
                                album = "0"
                            }
                            val album_id = album.toLong()
                            val album_name = jsonObj.get("AlbumName").asString
                            val album_pmid =
                                "http://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                            val one = mutableListOf<artistlist>()
                            val singer = Gson().fromJson<Array<Long>>(
                                jsonObj.get("SingerId").asJsonArray,
                                Array<Long>::class.java
                            ).toMutableList()
                            val ca = jsonObj.get("SingerName").asString
                            if (ca != "") {
                                val ea = ca.substring(4)
                                val da = ea.substring(0, ea.lastIndexOf('<'))
                                one.add(artistlist(singer[0], da))
                            } else {
                                one.add(artistlist(singer[0], ""))
                            }

                            musicall.add(
                                Searchs(
                                    2, Music(
                                        title,
                                        album_name,
                                        album_id,
                                        id,
                                        "",
                                        one,
                                        album_pmid,
                                        0,
                                        mid
                                    )
                                )

                            )
                        }


                        adapter.add(musicall)

                    }

                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
            }

        }

        observers =
            object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(data: Boolean) {
                    if (data) {
                        if (swipe_refresh_layout != null) {
                            swipe_refresh_layout.isRefreshing = false
                        }
                    }

                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {
                }

            }

    }


    /**
     * 初始化
     */
    private fun initSearchList(datas: MutableList<Searchs>) {

        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = SearchListAdapter(datas, this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : SearchListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val music = mutableListOf<Music>()
                for (it in datas) {
                    music.add(it.music)
                }
                val json: String = Gson().toJson(music)
                val intent = Intent()
                intent.setClass(context, MusicPlayActivity().javaClass)
                intent.putExtra("album_id", 3L)
                intent.putExtra("pos", position)
                intent.putExtra("list", json)
                intent.putExtra("type", 3)
                startActivity(intent)

            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //managerrecycler的布局管理器
                val lastVisibleItemPosition: Int = manager.findLastVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == adapter.itemCount - 1) {
                    Log.d("MainActivity===$add", "=============最后一条")

                    when (type) {
                        0 -> getPresenter().qqdata(context, search, add)
                        1 -> getPresenter().kugoudata(context, search, add)
                    }

                }
            }
        })
    }

}