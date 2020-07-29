package com.app.xiaobai.music.music.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.R
import com.app.xiaobai.music.SearchIndexActivity
import com.app.xiaobai.music.adapter.SearchListAdapter
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.Searchs
import com.app.xiaobai.music.bean.artistlist
import com.app.xiaobai.music.music.contract.SongContract
import com.app.xiaobai.music.music.model.SearchModel
import com.app.xiaobai.music.music.presenter.SongPresenter
import com.app.xiaobai.music.music.view.act.MusicPlayActivity
import com.app.xiaobai.music.sql.bean.Search
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.search_song_index.*
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
class SongFragment : BaseMvpFragment<SongContract.IPresenter>(), SongContract.IView {


    companion object {
        lateinit var observer: Observer<MutableList<Searchs>>
        lateinit var observers: Observer<Boolean>
    }

    private var add: Int = 1
    private lateinit var adapter: SearchListAdapter
    override fun registerPresenter() = SongPresenter::class.java
    override fun getLayoutId(): Int {
        return R.layout.search_song_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    override fun initData() {
        super.initData()

        observer = object : Observer<MutableList<Searchs>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(list: MutableList<Searchs>) {
                if (add == 1) {
                    initSearchList(list)
                } else {
                    adapter.add(list)
                }

            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
            }

        }

        observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(bool: Boolean) {
                if (bool) {
                    if (swipe_refresh_layout != null) {
                        swipe_refresh_layout.isRefreshing = false
                    }

                    Toast.makeText(
                        context,
                        R.string.error_connection,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {

            }

        }


    }

    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            when (SearchIndexActivity.type) {
                0 -> context?.let { SearchModel.qqdata(it, SearchIndexActivity.search, 1) }
                1 -> context?.let { SearchModel.kugoudata(it, SearchIndexActivity.search, 1) }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        when (SearchIndexActivity.type) {
            0 -> context?.let { SearchModel.qqdata(it, SearchIndexActivity.search, add) }
            1 -> context?.let { SearchModel.kugoudata(it, SearchIndexActivity.search, add) }
        }
        swipe_refresh_layout.isRefreshing = true

    }

    /**
     * 初始化
     */
    private fun initSearchList(datas: MutableList<Searchs>) {

        val manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = context?.let { SearchListAdapter(datas, it) }!!
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : SearchListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val music = mutableListOf<Music>()
                for (it in datas) {
                    music.add(it.music)
                }
                val json: String = Gson().toJson(music)
                val intent = Intent()
                context?.let { intent.setClass(it, MusicPlayActivity().javaClass) }
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
                    Log.d("MainActivity===", "=============最后一条")
                    add++
                    when (SearchIndexActivity.type) {
                        0 -> context?.let {
                            SearchModel.qqdata(
                                it,
                                SearchIndexActivity.search,
                                add
                            )
                        }
                        1 -> context?.let {
                            SearchModel.kugoudata(
                                it,
                                SearchIndexActivity.search,
                                add
                            )
                        }
                    }
                }
            }
        })
    }
}
