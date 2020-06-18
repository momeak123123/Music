package com.example.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.adapter.*
import com.example.music.bean.*
import com.example.music.music.contract.HomeContract
import com.example.music.music.presenter.HomePresenter
import com.example.music.music.view.act.*
import com.example.music.music.view.act.SongDetActivity.Companion.adapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.song_set.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeFragment : BaseMvpFragment<HomeContract.IPresenter>(), HomeContract.IView {
    private var bools: Boolean = false
    private var adapters: HomeListAdapter? = null
    private lateinit var sp: SharedPreferences


    override fun registerPresenter() = HomePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }


    override fun initData() {
        super.initData()

        sp = requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE)
        if (!sp.getString("ads", "").equals("")) {
            bools = true
            loadData()
        }

    }

    fun loadData() {

        if (!sp.getString("ads", "").equals("")) {

            val ads: List<Banner> = Gson().fromJson(
                sp.getString("ads", ""),
                object : TypeToken<List<Banner>>() {}.type
            )
            loadData1(ads)
        }

        if (!sp.getString("list", "").equals("")) {
            val list: List<TopList> = Gson().fromJson(
                sp.getString("list", ""),
                object : TypeToken<List<TopList>>() {}.type
            )
            loadData2(list)
        }

        if (!sp.getString("album", "").equals("")) {
            val album: List<Album> = Gson().fromJson(
                sp.getString("album", ""),
                object : TypeToken<List<Album>>() {}.type
            )
            loadData3(album)
        }

        if (!sp.getString("artist", "").equals("")) {
            val artist: List<Artists> = Gson().fromJson(
                sp.getString("artist", ""),
                object : TypeToken<List<Artists>>() {}.type
            )
            loadData4(artist)
        }

        if (!sp.getString("song", "").equals("")) {
            val song: List<Music> = Gson().fromJson(
                sp.getString("song", ""),
                object : TypeToken<List<Music>>() {}.type
            )
            loadData5(song)
        }

        if (swipe_refresh_layout != null) {
            swipe_refresh_layout.isRefreshing = false
        }
    }

    fun loadData1(list: List<Banner>) {
        if (list.isNotEmpty()) {
            val bannerdata = mutableListOf<BannerItem>()
            for (it in list) {
                val item1 = BannerItem()
                item1.imgUrl = it.url
                item1.title = ""
                bannerdata.add(item1)

            }
            initbanner(bannerdata)
        }
    }

    fun loadData2(list: List<TopList>) {
        val data_toplist = mutableListOf<TopList>()
        if (list.isNotEmpty()) {
            if (list.size > 6) {
                for (i in 0..5) {
                    data_toplist.add(list[i])
                }
            } else {
                for (i in list) {
                    data_toplist.add(i)
                }
            }

            initTopList(data_toplist)
        }
    }

    fun loadData3(album: List<Album>) {
        val data_ablum = mutableListOf<Album>()
        if (album.isNotEmpty()) {
            if (album.size > 6) {
                for (i in 0..5) {
                    data_ablum.add(album[i])
                }
            } else {
                for (i in album) {
                    data_ablum.add(i)
                }
            }
            initAlbumList(data_ablum)
        }
    }

    fun loadData4(artist: List<Artists>) {
        val data_artist = mutableListOf<Artists>()
        if (artist.isNotEmpty()) {
            if (artist.size > 8) {
                for (i in 0..7) {
                    data_artist.add(artist[i])
                }
            } else {
                for (i in artist) {
                    data_artist.add(i)
                }
            }
            initSingerList(data_artist)
        }
    }

    fun loadData5(song: List<Music>) {
        val data_song = mutableListOf<Music>()
        if (song.isNotEmpty()) {
            if (song.size > 8) {
                for (i in 0..7) {
                    data_song.add(song[i])
                }
            } else {
                for (i in song) {
                    data_song.add(i)
                }
            }
            initSongList(data_song)
        }
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { loadData() })

        RxView.clicks(music)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val json: String = Gson().toJson(MusicApp.getMusic())
                val intent = Intent()
                context?.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                intent.putExtra("pos", MusicApp.getPosition())
                intent.putExtra("list", json)
                startActivity(intent)

            }

        RxView.clicks(search)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { it1 -> intent.setClass(it1, SearchActivity().javaClass) }
                startActivity(intent)
            }
        RxView.clicks(more1)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumActivity().javaClass) }
                intent.putExtra("album_type", 0)
                startActivity(intent)
            }
        RxView.clicks(more2)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumActivity().javaClass) }
                intent.putExtra("album_type", 1)
                startActivity(intent)
            }
        RxView.clicks(more3)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, ArtistActivity().javaClass) }
                startActivity(intent)
            }
        RxView.clicks(more4)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {

            }
    }


    override fun onResume() {
        super.onResume()
        if (bools) {
            loadData()
        }
        try {
            if (MusicPlayActivity.wlMusic.isPlaying) {
                music.visibility = View.VISIBLE
            }


        } catch (e: Exception) {
        }
    }

    /**
     * 初始化轮播图
     */
    private fun initbanner(bannerdata: List<BannerItem>) {
        //设置banner样式
        banner.setSource(bannerdata)
            .setOnItemClickListener(BaseBanner.OnItemClickListener<BannerItem?> { _, _, position ->
                println(
                    position
                )
            })
            .setIsOnePageLoop(false).startScroll()
    }

    /**
     * 初始化排行榜
     */
    private fun initTopList(list: List<TopList>) {
        recyc_item1.layoutManager = GridLayoutManager(context, 3)
        recyc_item1.itemAnimator = DefaultItemAnimator()
        adapters = context?.let { HomeListAdapter(list, it) }
        recyc_item1.adapter = adapters
        adapters!!.setOnItemClickListener(object : HomeListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                intent.putExtra("album_id", list[position].from_id)
                intent.putExtra("album_type", list[position].from)
                intent.putExtra("album_time", list[position].update_time)
                intent.putExtra("palylist_name", list[position].name)
                intent.putExtra("info", list[position].info)
                intent.putExtra("cover", list[position].pic_url)
                intent.putExtra("type", 1)
                startActivity(intent)

            }
        })

    }


    /**
     * 初始化专辑
     */
    private fun initAlbumList(album: List<Album>) {
        recyc_item2.layoutManager = GridLayoutManager(context, 3)
        recyc_item2.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeAlbumAdapter(album, it) }
        recyc_item2.adapter = adapter
        adapter!!.setOnItemClickListener(object : HomeAlbumAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                intent.putExtra("album_id", album[position].album_id)
                intent.putExtra("album_type", album[position].type)
                intent.putExtra("album_time", 0L)
                intent.putExtra("palylist_name", album[position].name)
                intent.putExtra("info", album[position].info)
                intent.putExtra("cover", album[position].pic_url)
                intent.putExtra("type", 2)
                startActivity(intent)

            }
        })

    }

    /**
     * 初始化歌手
     */
    private fun initSingerList(artists: List<Artists>) {
        recyc_item3.layoutManager = GridLayoutManager(context, 4)
        recyc_item3.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeSingerAdapter(artists, it) }
        recyc_item3.adapter = adapter
        adapter!!.setOnItemClickListener(object : HomeSingerAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                context?.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                intent.putExtra("id", artists[position].artist_id)
                intent.putExtra("type", artists[position].type)
                startActivity(intent)

            }
        })

    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Music>) {
        recyc_item4.layoutManager = LinearLayoutManager(context)
        recyc_item4.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeSongAdapter(song, it) }
        recyc_item4.adapter = adapter
        adapter!!.setOnItemClickListener(object : HomeSongAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val json: String = Gson().toJson(song)
                val intent = Intent()
                context?.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                intent.putExtra("pos", position)
                intent.putExtra("list", json)
                startActivity(intent)

            }
        })

    }

}


