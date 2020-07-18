package com.example.xiaobai.music.music.view.act

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
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SearchListAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.Searchs
import com.example.xiaobai.music.bean.artistlist
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.presenter.SearchListPresenter
import com.example.xiaobai.music.sql.config.Initialization
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
    private var mShouldScroll = false

    //记录目标项位置
    private var mToPosition = 0
    private var mToPositions = 0
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


    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            swipe_refresh_layout.isRefreshing = false
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
                mToPositions = add*29

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
                        initSearchList(musicall)
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

                            initSearchList(musicall)

                        }


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


    private fun smoothMoveToPosition(mRecyclerView: RecyclerView, position: Int) {
        // 第一个可见位置
        val firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0))
        // 最后一个可见位置
        val lastItem =
            mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.childCount - 1))
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position)
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            val movePosition = position - firstItem
            if (movePosition >= 0 && movePosition < mRecyclerView.childCount) {
                val top = mRecyclerView.getChildAt(movePosition).top
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top)
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position)
            mToPosition = position
            mShouldScroll = true
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
        smoothMoveToPosition(recyclerView,mToPositions)
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
