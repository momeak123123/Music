package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.adapter.AlbumDetAdapter
import com.example.music.adapter.PlaySongAdapter
import com.example.music.bean.Music
import com.example.music.bean.artistlist
import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.model.MusicPlayModel
import com.example.music.music.presenter.AlbumDetPresenter
import com.example.music.sql.bean.Playlist
import com.example.music.sql.dao.mDownDao
import com.example.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.album_index.*
import kotlinx.android.synthetic.main.album_index.foods
import kotlinx.android.synthetic.main.album_index.in_indel
import kotlinx.android.synthetic.main.album_index.poplue
import kotlinx.android.synthetic.main.album_index.recyc_item
import kotlinx.android.synthetic.main.album_index.swipe_refresh_layout
import kotlinx.android.synthetic.main.food.*
import kotlinx.android.synthetic.main.play_list.*
import kotlinx.android.synthetic.main.play_list.del
import kotlinx.android.synthetic.main.popule.*
import kotlinx.android.synthetic.main.song_index.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetActivity : BaseMvpActivity<AlbumDetContract.IPresenter>(), AlbumDetContract.IView {

    companion object {
        lateinit var observer: Observer<JsonObject>
        lateinit var observers: Observer<Boolean>
        lateinit var observerd: Observer<Int>
        lateinit var observert: Observer<Int>
    }


    private lateinit var adapters: PlaySongAdapter
    private var bools: Boolean = true
    private var album_time: Long = 0
    private lateinit var names: String
    private var album_type: Int = 0
    private var album_id: Long = 0
    private lateinit var covers: String
    private lateinit var songdata: String
    private lateinit var txts: String
    var songlist = mutableListOf<Music>()
    private var type: Int = 0
    private lateinit var adapter: AlbumDetAdapter
    private lateinit var context: Context
    override fun registerPresenter() = AlbumDetPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.album_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        observer = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {

                val song: MutableList<Music> = Gson().fromJson(
                    data.asJsonObject.get("song_list").asJsonArray,
                    object : TypeToken<MutableList<Music>>() {}.type
                )

                if (song.isNotEmpty()) {
                    songlist.clear()
                    songlist = song
                    val one = mutableListOf<artistlist>()
                    val det = Music("", "", 0, 0, "", one, "", 0, "")
                    songlist.add(0, det)
                    initSongList(songlist)
                } else {
                    songlist.clear()
                    val one = mutableListOf<artistlist>()
                    val det = Music("", "", 0, 0, "", one, "", 0, "")
                    songlist.add(0, det)
                    initSongList(songlist)
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }
    }

    override fun initData() {
        super.initData()
        swipe_refresh_layout.isRefreshing = true
        val bundle = intent.extras
        type = bundle?.get("type") as Int
        album_id = bundle.get("album_id") as Long
        album_type = bundle.get("album_type") as Int
        album_time = bundle.get("album_time") as Long
        names = bundle.get("palylist_name") as String
        txts = bundle.get("info") as String
        covers = bundle.get("cover") as String
        loadData()


    }


    fun loadData() {
        if (MusicApp.getNetwork()) {
            if (type == 1) {
                getPresenter().songdatas(album_id, album_type, album_time, context)
            } else {
                getPresenter().songdata(album_id, album_type, context)
            }
        } else {
            if (swipe_refresh_layout != null) {
                swipe_refresh_layout.isRefreshing = false
            }
            Toast.makeText(
                context,
                getText(R.string.nonet),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { loadData() })

        RxView.clicks(foods)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {}

        RxView.clicks(all)
            .throttleFirst(1, TimeUnit.SECONDS)
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

            }

        RxView.clicks(down)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {

            }


        RxView.clicks(deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                foods.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)

            }

        RxView.clicks(list_dow)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_indel.visibility = View.GONE
            }

        RxView.clicks(play_list_back)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_title.visibility = View.GONE
            }
        /* if(isSDcardAvailable()){
             val request =
                 OkGo.get<File>(apk.getUrl())
             OkDownload.request(apk.getId().toString(), request)
                 .save()
                 .folder(getMusicDir())
                 .register(LogDownloadListener(apk, context))
                 .start()
         }*/


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
        adapters = PlaySongAdapter(song, context)
        in_list.adapter = adapters
        adapters.setOnItemClickListener(object : PlaySongAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                MaterialDialog.Builder(context)
                    .title("添加音乐")
                    .content("是否将音乐加入此歌单")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
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
                                    val num = (playsong.size + songs.size).toString()
                                    MusicPlayModel.addSong(
                                        context,
                                        songs,
                                        num,
                                        song[position].play_list_id
                                    )
                                    adapters.update(position, num)
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
                                val num = (playsong.size + songs.size).toString()
                                MusicPlayModel.addSong(
                                    context,
                                    songs,
                                    num,
                                    song[position].play_list_id
                                )
                                adapters.update(position, num)
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
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    intent.setClass(context, MusicPlayActivity().javaClass)
                    intent.putExtra("album_id", album_id)
                    intent.putExtra("pos", position - 1)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 1)
                    startActivity(intent)
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
                } else {
                    foods.visibility = View.GONE
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
                        srtist_name += "/" + it.artist_name
                    } else {
                        srtist_name = it.artist_name
                    }

                }
                artist_txt.text = getText(R.string.item3s).toString() + ":" + srtist_name
                RxView.clicks(popule_back)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        poplue.visibility = View.GONE
                    }

                RxView.clicks(relat3)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        poplue.visibility = View.GONE
                        in_indel.visibility = View.VISIBLE
                        Glide.with(context).load("").into(del)
                        in_title.text = getText(R.string.song_but)
                        val list: MutableList<Playlist> = mPlaylistDao.queryAll()
                        val idmap = mutableListOf<Music>()
                        idmap.add(songlist[data])
                        initSongLists(list, idmap)
                    }

                RxView.clicks(relat4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {

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

