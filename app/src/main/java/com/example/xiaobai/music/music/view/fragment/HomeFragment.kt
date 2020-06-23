package com.example.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.xiaobai.music.MainActivity
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.*
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.config.LogDownloadListener
import com.example.xiaobai.music.music.contract.HomeContract
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.presenter.HomePresenter
import com.example.xiaobai.music.music.view.act.*
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.in_indel
import kotlinx.android.synthetic.main.fragment_home.poplue
import kotlinx.android.synthetic.main.fragment_home.swipe_refresh_layout
import kotlinx.android.synthetic.main.play_list.*
import kotlinx.android.synthetic.main.popule.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeFragment : BaseMvpFragment<HomeContract.IPresenter>(), HomeContract.IView {

    private lateinit var data_song: MutableList<Music>
    private lateinit var bannerdata: MutableList<BannerItem>
    private var adapters: HomeListAdapter? = null
    private lateinit var sp: SharedPreferences

    companion object {
        lateinit var adaptert: PlaySongAdapter
        lateinit var observert: Observer<Int>
    }

    override fun registerPresenter() = HomePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }


    override fun initData() {
        super.initData()
        bannerdata = mutableListOf<BannerItem>()
        sp = requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE)


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
         data_song = mutableListOf<Music>()
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
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                intent.putExtra("album_id", 0L)
                intent.putExtra("pos", 0)
                intent.putExtra("list", "")
                intent.putExtra("type", 0)
                startActivity(intent)

            }

        RxView.clicks(search)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { it1 -> intent.setClass(it1, SearchActivity().javaClass) }
                startActivity(intent)
            }
        RxView.clicks(more1)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumActivity().javaClass) }
                intent.putExtra("album_type", 0)
                startActivity(intent)
            }
        RxView.clicks(more2)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, AlbumActivity().javaClass) }
                intent.putExtra("album_type", 1)
                startActivity(intent)
            }
        RxView.clicks(more3)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, ArtistActivity().javaClass) }
                startActivity(intent)
            }

        RxView.clicks(list_dow)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE
                MainActivity.craet(true)
            }

        RxView.clicks(play_list_back)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE
                MainActivity.craet(true)
            }
    }


    override fun onResume() {
        super.onResume()

        if (bannerdata.size==0) {
            loadData()
        }
        try {
            if (MusicPlayActivity.bool) {
                music.visibility = View.VISIBLE
            }else{
                music.visibility = View.GONE
            }
        } catch (e: Exception) {
        }


        observert = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n", "CheckResult")
            override fun onNext(data: Int) {
                poplue.visibility = View.VISIBLE
                MainActivity.craet(false)
                edit_song.text =
                    getText(R.string.album).toString() + ":" + data_song[data].album_name
                var srtist_name = ""
                for (it in data_song[data].all_artist) {
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
                        MainActivity.craet(true)
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
                       val sps  = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
                        if (sps.getBoolean("login", false)) {
                            poplue.visibility = View.GONE
                            in_indel.visibility = View.VISIBLE
                            context?.let { it1 -> Glide.with(it1).load("").into(del) }
                            in_title.text = getText(R.string.song_but)
                            val list: MutableList<Playlist> = mPlaylistDao.queryAll()
                            val idmap = mutableListOf<Music>()
                            idmap.add(data_song[data])
                            initSongLists(list, idmap)

                        } else {
                            context?.let { it1 ->
                                MaterialDialog.Builder(it1)
                                    .title("登录")
                                    .content("未登陆账号，是否登录")
                                    .positiveText("确认")
                                    .negativeText("取消")
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



                RxView.clicks(relat4)
                    .throttleFirst(3, TimeUnit.SECONDS)
                    .subscribe {
                        context?.let { it1 ->
                            MaterialDialog.Builder(it1)
                                .title("下载音乐")
                                .content("是否下载音乐")
                                .positiveText("确认")
                                .negativeText("取消")
                                .onPositive { _: MaterialDialog?, _: DialogAction? ->

                                    val idmap = mutableListOf<Music>()

                                    for (ite in SongDetActivity.adapter.listdet) {
                                        if (ite.type == 1) {
                                            idmap.add(ite.song)
                                        }
                                    }
                                    if (idmap.isNotEmpty()) {
                                        for(its in idmap){

                                            val downs = mDownDao.querys(its.song_id)
                                            if(downs.size>0){
                                                for(itd in downs){
                                                    if(itd.type==0){
                                                        val request = OkGo.get<File>(its.uri)
                                                        OkDownload.request(its.uri, request) //
                                                            .priority(0)
                                                            .fileName("music" + its.song_id + ".mp3") //
                                                            .save() //
                                                            .register(LogDownloadListener(
                                                                its,
                                                                context,
                                                                0,
                                                                downs,
                                                                0
                                                            )) //
                                                            .start()
                                                    }else{
                                                        Toast.makeText(
                                                            context,
                                                            getText(R.string.download_carry),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }else{
                                                val request = OkGo.get<File>(its.uri)
                                                OkDownload.request(its.uri, request) //
                                                    .priority(0)
                                                    .fileName("music" + its.song_id + ".mp3") //
                                                    .save() //
                                                    .register(LogDownloadListener(
                                                        its,
                                                        context,
                                                        0,
                                                        downs,
                                                        0
                                                    )) //
                                                    .start()
                                            }


                                        }

                                    } else {
                                        Toast.makeText(
                                            context,
                                            getText(R.string.song_collect_error),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                                .show()
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
        adaptert = context?.let { PlaySongAdapter(song, it) }!!
        in_list.adapter = adaptert
        adaptert.setOnItemClickListener(object : PlaySongAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                MaterialDialog.Builder(context!!)
                    .title("添加音乐")
                    .content("是否将音乐加入此歌单")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                        val playlist: Playlist = mPlaylistDao.query(song[position].play_list_id)[0]
                        val playsong = mDownDao.query(song[position].play_list_id)
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
                                        context!!,
                                        songs,
                                        num,
                                        song[position].play_list_id,2,position
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
                                    context!!,
                                    songs,
                                    num,
                                    song[position].play_list_id,2,position
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
                intent.putExtra("id", artists[position].artsit_id)
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
                intent.putExtra("album_id", 0L)
                intent.putExtra("pos", position)
                intent.putExtra("list", json)
                intent.putExtra("type", 2)
                startActivity(intent)

            }
        })

    }

}


