package com.example.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.HomeAlbumAdapter
import com.example.music.adapter.HomeListAdapter
import com.example.music.adapter.HomeSingerAdapter
import com.example.music.adapter.HomeSongAdapter
import com.example.music.bean.Album
import com.example.music.bean.Artists
import com.example.music.bean.Song
import com.example.music.bean.TopList
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.HomeContract
import com.example.music.music.presenter.HomePresenter
import com.example.music.music.view.act.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.fragment_home.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeFragment : BaseMvpFragment<HomeContract.IPresenter>(), HomeContract.IView {

    companion object {

    }

    var bannerdata = mutableListOf<BannerItem>()

    override fun registerPresenter() = HomePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }


    override fun initData() {
        super.initData()
        initbanner()

        val sp: SharedPreferences =
            requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE)
        val data_toplist = mutableListOf<TopList>()
        val data_ablum = mutableListOf<Album>()
        val data_artist = mutableListOf<Artists>()
        val data_song = mutableListOf<Song>()

        if (!sp.getString("list", "").equals("")) {
            val list: List<TopList> = Gson().fromJson(
                sp.getString("list", ""),
                object : TypeToken<List<TopList>>() {}.type
            )
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

        if (!sp.getString("album", "").equals("")) {
            val album: List<Album> = Gson().fromJson(
                sp.getString("album", ""),
                object : TypeToken<List<Album>>() {}.type
            )
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

        if (!sp.getString("artist", "").equals("")) {
            val artist: List<Artists> = Gson().fromJson(
                sp.getString("artist", ""),
                object : TypeToken<List<Artists>>() {}.type
            )
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

        if (!sp.getString("song", "").equals("")) {
            val song: List<Song> = Gson().fromJson(
                sp.getString("song", ""),
                object : TypeToken<List<Song>>() {}.type
            )
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

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(search)
            .throttleFirst(1,TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { it1 -> intent.setClass(it1, SearchActivity().javaClass) }
                startActivity(intent)
            }
        RxView.clicks(more1)
            .throttleFirst(1,TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumActivity().javaClass) }
                intent.putExtra("album_type",0)
                startActivity(intent)
            }
        RxView.clicks(more2)
            .throttleFirst(1,TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, MusicListActivity().javaClass) }
                intent.putExtra("album_type",1)
                startActivity(intent)
            }
        RxView.clicks(more3)
            .throttleFirst(1,TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, ArtistActivity().javaClass) }
                startActivity(intent)
            }
        RxView.clicks(more4)
            .throttleFirst(1,TimeUnit.SECONDS)
            .subscribe {

            }
    }


    override fun onResume() {
        super.onResume()
    }

    /**
     * 初始化轮播图
     */
    private fun initbanner() {
        //设置banner样式
        bannerdata.clear()
        bannerdata.addAll(getPresenter().imagesdata())
        banner.setSource(bannerdata)
            .setOnItemClickListener(BaseBanner.OnItemClickListener<BannerItem?> { _, _, position -> println(position) })
            .setIsOnePageLoop(false).startScroll()
        banner.setSource(bannerdata).startScroll()
    }

    /**
     * 初始化排行榜
     */
    private fun initTopList(list: List<TopList>) {
        recyc_item1.layoutManager = GridLayoutManager(context, 3)
        recyc_item1.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeListAdapter(list, it) }
        recyc_item1.adapter = adapter
        recyc_item1.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    /**
     * 初始化专辑
     */
    private fun initAlbumList(album: List<Album>) {
        recyc_item2.layoutManager = GridLayoutManager(context, 3)
        recyc_item2.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeAlbumAdapter(album, it) }
        recyc_item2.adapter = adapter
        recyc_item2.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    /**
     * 初始化歌手
     */
    private fun initSingerList(artists: List<Artists>) {
        recyc_item3.layoutManager = GridLayoutManager(context, 4)
        recyc_item3.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeSingerAdapter(artists, it) }
        recyc_item3.adapter = adapter
        recyc_item3.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val intent = Intent()
                        context?.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id",artists[position].artist_id)
                        intent.putExtra("type",artists[position].type)
                        startActivity(intent)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Song>) {
        recyc_item4.layoutManager = LinearLayoutManager(context)
        recyc_item4.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeSongAdapter(song, it) }
        recyc_item4.adapter = adapter
        recyc_item4.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val intent = Intent()
                        context?.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                        intent.putExtra("id",position)
                        startActivity(intent)
                        
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

}


