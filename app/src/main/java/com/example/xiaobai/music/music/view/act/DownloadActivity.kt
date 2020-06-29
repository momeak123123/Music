package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.AlbumDetAdapter
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.artistlist
import com.example.xiaobai.music.music.contract.DownloadContract
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.presenter.DownloadPresenter
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.download_list.*
import kotlinx.android.synthetic.main.food.*
import kotlinx.android.synthetic.main.play_list.*
import kotlinx.android.synthetic.main.popule.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/20
 * @Description input description
 **/

@SuppressLint("Registered")
class DownloadActivity : BaseMvpActivity<DownloadContract.IPresenter>(), DownloadContract.IView {

    companion object {
        lateinit var observer: Observer<JsonArray>
        lateinit var observers: Observer<Boolean>
        lateinit var observerd: Observer<Int>
        lateinit var observert: Observer<Int>
        lateinit var adapters: PlaySongAdapter
    }


    private lateinit var sp: SharedPreferences
    private var bools: Boolean = true
    private var album_time: Long = 0
    private lateinit var names: String
    private var album_type: Int = 0
    private var album_id: Long = 0
    private lateinit var covers: String
    private lateinit var songdata: String
    private lateinit var txts: String
    var songlist = mutableListOf<Music>()
    private var activity_type: Int = 0
    private lateinit var adapter: AlbumDetAdapter
    private lateinit var context: Context
    override fun registerPresenter() = DownloadPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.download_list
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        down.visibility = View.GONE
        relat4.visibility = View.GONE
    }

    override fun initData() {
        super.initData()
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
        swipe_refresh_layout.isRefreshing = true
        loadData()
    }


    fun loadData() {

        val data = mDownDao.queryt(1)
        val song = mutableListOf<Music>()
        val artist = mutableListOf<artistlist>()
        for (it in data) {
            if(it.type==1){
                artist.add(artistlist(0, it.artist))
                val music = Music(
                    it.name,
                    it.album_name,
                    it.album_id,
                    it.song_id,
                    it.uri,
                    artist,
                    it.pic_url,
                    it.song_list_id,
                    it.publish_time
                )
                song.add(music)
            }
        }
        if (song.isNotEmpty()) {
            songlist.clear()
            songlist = song
            val one = mutableListOf<artistlist>()
            val det = Music("", "", 0, 0, "", one, "", 0, "")
            songlist.add(0, det)
            initSongList(songlist)
        }


    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { loadData() })



        RxView.clicks(foods)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {}

        RxView.clicks(all)
            .throttleFirst(0, TimeUnit.SECONDS)
            .subscribe {
                if (bools) {
                    all.text = getText(R.string.song_unall)
                    adapter.update(true)
                    bools = false
                } else {
                    all.text = getText(R.string.song_all)
                    adapter.update(false)
                    bools = true
                }

            }
        RxView.clicks(cencel)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (sp.getBoolean("login", false)) {
                    val idmap = mutableListOf<Music>()
                    for (ite in adapter.listdet) {
                        if (ite.type == 1) {
                            idmap.add(ite.song)
                        }
                    }
                    if (idmap.isNotEmpty()) {
                        in_indel.visibility = View.VISIBLE
                        Glide.with(context).load("").into(del)
                        in_title.text = getText(R.string.song_but)
                        val list: MutableList<Playlist> = mPlaylistDao.queryAll()
                        initSongLists(list, idmap)
                    } else {
                        Toast.makeText(
                            context,
                            getText(R.string.song_collect_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    MaterialDialog.Builder(context)
                        .title("登录")
                        .content("未登陆账号，是否登录")
                        .positiveText("确认")
                        .negativeText("取消")
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                            val intent = Intent()
                            context.let { intent.setClass(it, LoginActivity().javaClass) }
                            startActivity(intent)
                        }
                        .show()
                }


            }


        RxView.clicks(deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                foods.visibility = View.GONE
                view.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)

            }

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
        AlbumDetActivity.adapters = PlaySongAdapter(song, context)
        in_list.adapter = AlbumDetActivity.adapters
        AlbumDetActivity.adapters.setOnItemClickListener(object :
            PlaySongAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                MaterialDialog.Builder(context)
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
                                        context,
                                        songs,
                                        num,
                                        song[position].play_list_id, 1, position
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
                                    song[position].play_list_id, 1, position
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
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Music>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyc_item.isNestedScrollingEnabled = false
        recyc_item.layoutManager = layoutManager
        recyc_item.itemAnimator = DefaultItemAnimator()
        recyc_item.setHasFixedSize(true)
        adapter = AlbumDetAdapter(song, context, album_id, txts, covers, names)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : AlbumDetAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (position > 0) {
                    if (adapter.type == 0) {
                        val json: String = Gson().toJson(song)
                        val intent = Intent()
                        intent.setClass(context, MusicPlayActivity().javaClass)
                        intent.putExtra("album_id", album_id)
                        intent.putExtra("pos", position - 1)
                        intent.putExtra("list", json)
                        intent.putExtra("type", 1)
                        startActivity(intent)
                    } else {
                        if (adapter.listdet[position].type == 0) {
                            adapter.listdet[position].type = 1
                            adapter.notifyItemChanged(position)
                        } else {
                            adapter.listdet[position].type = 0
                            adapter.notifyItemChanged(position)
                        }
                    }

                }

            }
        })

        if (swipe_refresh_layout != null) {
            swipe_refresh_layout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            adapter.notifyItemChanged(0)
        } catch (e: Exception) {
        }

        observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(data: Boolean) {
                if (data) {
                    finish()
                } else {
                    val json: String = Gson().toJson(songlist)
                    val intent = Intent()
                    intent.setClass(context, MusicPlayActivity().javaClass)
                    intent.putExtra("album_id", album_id)
                    intent.putExtra("pos", 0)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 1)
                    startActivity(intent)
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }

        observerd = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("CheckResult")
            override fun onNext(data: Int) {
                if (data == 1) {
                    foods.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                } else {
                    foods.visibility = View.GONE
                    view.visibility = View.GONE
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }

        observert = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n", "CheckResult")
            override fun onNext(data: Int) {
                poplue.visibility = View.VISIBLE
                edit_song.text =
                    getText(R.string.album).toString() + ":" + songlist[data].album_name
                var srtist_name = ""
                for (it in songlist[data].all_artist) {
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
                        if (sp.getBoolean("login", false)) {
                            poplue.visibility = View.GONE
                            in_indel.visibility = View.VISIBLE
                            Glide.with(context).load("").into(del)
                            in_title.text = getText(R.string.song_but)
                            val list: MutableList<Playlist> = mPlaylistDao.queryAll()
                            val idmap = mutableListOf<Music>()
                            idmap.add(songlist[data])
                            initSongLists(list, idmap)
                        } else {
                            MaterialDialog.Builder(context)
                                .title("登录")
                                .content("未登陆账号，是否登录")
                                .positiveText("确认")
                                .negativeText("取消")
                                .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                    val intent = Intent()
                                    context.let { intent.setClass(it, LoginActivity().javaClass) }
                                    startActivity(intent)
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

    override fun onDestroy() {
        super.onDestroy()
    }
}
