package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.adapter.PlaySongAdapter
import com.example.music.adapter.SongDetAdapter
import com.example.music.bean.Music
import com.example.music.bean.artistlist
import com.example.music.music.contract.SongDetContract
import com.example.music.music.model.MusicPlayModel
import com.example.music.music.presenter.SongDetPresenter
import com.example.music.sql.bean.Playlist
import com.example.music.sql.dao.mDownDao
import com.example.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.food.*
import kotlinx.android.synthetic.main.play_list.*
import kotlinx.android.synthetic.main.play_list.del
import kotlinx.android.synthetic.main.popule.*
import kotlinx.android.synthetic.main.song_index.*
import kotlinx.android.synthetic.main.song_set.*
import kotlinx.android.synthetic.main.song_set.edit_song
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongDetActivity : BaseMvpActivity<SongDetContract.IPresenter>(), SongDetContract.IView {

    companion object {
        lateinit var observer: Observer<JsonArray>
        lateinit var observers: Observer<Boolean>
        lateinit var observerd: Observer<Int>
        lateinit var observerdel: Observer<Int>
        lateinit var observert: Observer<Int>
        lateinit var adapter: SongDetAdapter
    }

    private lateinit var adapters: PlaySongAdapter
    private var bools: Boolean = true
    private var playids: Long = 0
    private var ids: Long = 0
    private var id: Int = 0
    private lateinit var nums: String
    private lateinit var names: String
    private lateinit var imaurl: String

    private lateinit var context: Context
    var songlist = mutableListOf<Music>()

    override fun registerPresenter() = SongDetPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.song_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        context = this

    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        names = bundle?.get("name") as String
        imaurl = bundle.get("url") as String
        nums = bundle.get("num") as String
        ids = bundle.get("id") as Long
        playids = bundle.get("playid") as Long
        val data = mDownDao.query(playids)
        val song = mutableListOf<Music>()
        val artist = mutableListOf<artistlist>()
        if (MusicApp.getNetwork()) {
            getPresenter().listdata(context, playids)
        } else {
            for (it in data) {
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

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if (MusicApp.getNetwork()) {
                getPresenter().listdata(context, playids)
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
        })


        RxView.clicks(song_set_back)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)
            }
        RxView.clicks(item1)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)
                val intent = Intent()
                intent.setClass(context as SongDetActivity, SongEditActivity().javaClass)
                intent.putExtra("url", imaurl)
                intent.putExtra("name", names)
                intent.putExtra("playid", playids)
                startActivity(intent)
            }
        RxView.clicks(item2)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                foods.visibility = View.VISIBLE
            }
        RxView.clicks(item3)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                getPresenter().deldata(context, ids, playids)
            }

        RxView.clicks(alltxt)
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


    override fun onResume() {
        super.onResume()
        try {
            adapter.notifyItemChanged(0)
        } catch (e: Exception) {
        }

        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonArray) {
                val song: MutableList<Music> = Gson().fromJson(
                    data,
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

                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

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
                    intent.putExtra("album_id", playids)
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


        observerdel = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Int) {
               val list =  mDownDao.querys(songlist[data].song_id)
                if(list.size>0){
                    adapter.remove(data)
                    val playlist: Playlist = mPlaylistDao.query(playids)[0]
                    playlist.song_num = (playlist.song_num.toInt()-1).toString()
                    mPlaylistDao.update(playlist)
                    getPresenter().delsongs(context, list[0].id, list[0].song_list_id)
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
                    set.visibility = View.VISIBLE
                    if (ids == 1L) {
                        item1.visibility = View.GONE
                        item3.visibility = View.GONE
                    }
                } else {
                    foods.visibility = View.GONE
                    set.visibility = View.GONE
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

    /**
     * 初始化歌曲
     */
    private fun initSongLists(song: MutableList<Playlist>, idmap: MutableList<Music>) {
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
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        adapter = SongDetAdapter(song, context, imaurl, playids, names, nums)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : SongDetAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                if (position > 0) {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    intent.setClass(context, MusicPlayActivity().javaClass)
                    intent.putExtra("album_id", playids)
                    intent.putExtra("pos", position - 1)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 1)
                    startActivity(intent)
                }

            }
        })
    }

}
