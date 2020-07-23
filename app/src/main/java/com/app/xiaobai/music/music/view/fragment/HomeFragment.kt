package com.app.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.adapter.HomeDetAdapter
import com.app.xiaobai.music.adapter.PlaySongAdapter
import com.app.xiaobai.music.bean.*
import com.app.xiaobai.music.music.contract.HomeContract
import com.app.xiaobai.music.music.presenter.HomePresenter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import mvp.ljb.kt.fragment.BaseMvpFragment


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeFragment : BaseMvpFragment<HomeContract.IPresenter>(), HomeContract.IView {

    private lateinit var lists: List<TopList>
    private var adapter: HomeDetAdapter? = null
    private lateinit var sp: SharedPreferences

    companion object {
        lateinit var adaptert: PlaySongAdapter
        lateinit var observer: Observer<JsonObject>

    }

    override fun registerPresenter() = HomePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    override fun initData() {
        super.initData()
        lists = listOf()
        sp = requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE)
        if (!sp.getString("list", "").equals("")) {

            val ads: List<Banner> = Gson().fromJson(
                sp.getString("ads", ""),
                object : TypeToken<List<Banner>>() {}.type
            )

            lists = Gson().fromJson(
                sp.getString("list", ""),
                object : TypeToken<List<TopList>>() {}.type
            )

            val album: List<Album> = Gson().fromJson(
                sp.getString("album", ""),
                object : TypeToken<List<Album>>() {}.type
            )

            val artist: List<Artists> = Gson().fromJson(
                sp.getString("artist", ""),
                object : TypeToken<List<Artists>>() {}.type
            )

            val song: List<Music> = Gson().fromJson(
                sp.getString("song", ""),
                object : TypeToken<List<Music>>() {}.type
            )

            initList(ads, lists, album, artist, song)
        } else {
            context?.let { getPresenter().homedata(it) }
        }
    }


    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            context?.let { getPresenter().homedata(it) }
        })



    }

    override fun onResume() {
        super.onResume()

        try {
            adapter!!.notifyItemChanged(0)
        } catch (e: Exception) {
        }

        if(MusicApp.network()==-1){
            Toast.makeText(
                context,
                getText(R.string.error_connection),
                Toast.LENGTH_SHORT
            ).show()
        }else{
            if (lists.isEmpty()) {
                initData()
            }
        }

        observer = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {
                val ads: List<Banner> = Gson().fromJson<Array<Banner>>(
                    data.getAsJsonArray("ads"),
                    Array<Banner>::class.java
                ).toList()

                val album: List<Album> = Gson().fromJson<Array<Album>>(
                    data.getAsJsonArray("album_list"),
                    Array<Album>::class.java
                ).toList()
                val artist: List<Artists> = Gson().fromJson<Array<Artists>>(
                    data.getAsJsonArray("hot_artist"),
                    Array<Artists>::class.java
                ).toList()
                val song: List<Music> = Gson().fromJson<Array<Music>>(
                    data.getAsJsonArray("hot_song"),
                    Array<Music>::class.java
                ).toList()
                val list: List<TopList> = Gson().fromJson<Array<TopList>>(
                    data.getAsJsonArray("top_list"),
                    Array<TopList>::class.java
                ).toList()

                initList(ads, list, album, artist, song)


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }



    }




    /**
     * 初始化排行榜
     */
    private fun initList(
        ads: List<Banner>,
        list: List<TopList>,
        album: List<Album>,
        artist: List<Artists>,
        song: List<Music>
    ) {
        if (swipe_refresh_layout != null) {
            swipe_refresh_layout.isRefreshing = false
        }
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        adapter = context?.let { HomeDetAdapter(it, ads, list, album, artist, song) }
        recyc_item.adapter = adapter
        adapter!!.setOnItemClickListener(object : HomeDetAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
            }
        })

    }


}


