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
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.AlbumDetAdapter
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.artistlist
import com.example.xiaobai.music.config.LogDownloadListener
import com.example.xiaobai.music.music.contract.AlbumDetContract
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.presenter.AlbumDetPresenter
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mCollectDao
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.example.xiaobai.music.utils.FilesUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.album_index.*
import kotlinx.android.synthetic.main.food.*
import kotlinx.android.synthetic.main.play_list.*
import kotlinx.android.synthetic.main.popule.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetActivity : BaseMvpActivity<AlbumDetContract.IPresenter>(), AlbumDetContract.IView {

    companion object {
        lateinit var observer: Observer<JsonArray>
        lateinit var observers: Observer<Boolean>
        lateinit var observerd: Observer<Int>
        lateinit var observert: Observer<Int>
        lateinit var adapters: PlaySongAdapter
        lateinit var adapter: AlbumDetAdapter
    }


    private lateinit var sp: SharedPreferences
    private var bools: Boolean = true
    private var album_time: Long = 0
    private lateinit var names: String
    private var album_type: Int = 0
    private var album_id: Long = 0
    private lateinit var covers: String
    private lateinit var txts: String
    var songlist = mutableListOf<Music>()
    private var activity_type: Int = 0
    private lateinit var context: Context
    override fun registerPresenter() = AlbumDetPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.album_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
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
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }


    }

    override fun initData() {
        super.initData()
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
        swipe_refresh_layout.isRefreshing = true
        try {
            val bundle = intent.extras
            activity_type = bundle?.get("type") as Int
            album_id = bundle.get("album_id") as Long
            album_type = bundle.get("album_type") as Int
            album_time = bundle.get("album_time") as Long
            names = bundle.get("palylist_name") as String
            txts = bundle.get("info") as String
            covers = bundle.get("cover") as String
            loadData()
        } catch (e: Exception) {
        }


    }


    fun loadData() {
        if (MusicApp.network() != -1) {
            if (activity_type == 1) {
                getPresenter().songdatas(album_id, album_type, album_time, context)
            } else {
                getPresenter().songdata(album_id, album_type, context)
            }
        } else {
            if (swipe_refresh_layout != null) {
                swipe_refresh_layout.isRefreshing = false
            }
            finish()
        }

    }

    @SuppressLint("CheckResult", "ResourceAsColor")
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
                if (MusicApp.network() == -1) {
                    Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (MusicApp.userlogin()) {
                        val idmap = mutableListOf<Music>()
                        for (ite in adapter.listdet) {
                            if (ite.type == 1) {
                                idmap.add(ite.song)
                            }
                        }
                        if (idmap.isNotEmpty()) {
                            in_indel.visibility = View.VISIBLE
                            del.visibility = View.GONE
                            in_title.text = getText(R.string.song_but)
                            val list: MutableList<Playlist> =
                                mPlaylistDao.querys(sp.getString("userid", "").toString())
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
                            .title(getText(R.string.go))
                            .content(getText(R.string.ungoset))
                            .positiveText(getText(R.string.carry))
                            .negativeText(getText(R.string.cancel))
                            .positiveColorRes(R.color.colorAccentDarkTheme)
                            .negativeColorRes(R.color.red)
                            .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                val intent = Intent()
                                context.let { intent.setClass(it, LoginActivity().javaClass) }
                                startActivity(intent)
                            }
                            .show()
                    }
                }

            }

        RxView.clicks(down)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.network() == -1) {
                    Toast.makeText(
                        MusicApp.getAppContext(),
                        getText(R.string.error_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (MusicApp.userlogin()) {
                        MaterialDialog.Builder(context)
                            .title(getText(R.string.download_song))
                            .content(getText(R.string.download_playsong))
                            .positiveColorRes(R.color.colorAccentDarkTheme)
                            .negativeColorRes(R.color.red)
                            .positiveText(getText(R.string.carry))
                            .negativeText(getText(R.string.cancel))
                            .onPositive { _: MaterialDialog?, _: DialogAction? ->

                                val idmap = mutableListOf<Music>()

                                for (ite in adapter.listdet) {
                                    if (ite.type == 1) {
                                        idmap.add(ite.song)
                                    }
                                }
                                if (idmap.isNotEmpty()) {
                                    for (its in idmap) {

                                        val downs = mDownDao.querys(its.song_id)
                                        if (downs.size > 0) {
                                            Toast.makeText(
                                                context,
                                                getText(R.string.download_carry),
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {
                                            val request = OkGo.get<File>(its.uri)
                                            OkDownload.request(its.uri, request) //
                                                .priority(0)
                                                .folder(context.getExternalFilesDir("")!!.absolutePath+"/download")
                                                .fileName("music" + its.song_id)
                                                .save() //
                                                .register(
                                                    LogDownloadListener(
                                                        its,
                                                        context,
                                                        0,
                                                        downs,0
                                                    )
                                                ) //
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

                    } else {
                        MaterialDialog.Builder(context)
                            .title(getText(R.string.go))
                            .content(getText(R.string.ungoset))
                            .positiveText(getText(R.string.carry))
                            .negativeText(getText(R.string.cancel))
                            .positiveColorRes(R.color.colorAccentDarkTheme)
                            .negativeColorRes(R.color.red)
                            .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                val intent = Intent()
                                context.let { intent.setClass(it, LoginActivity().javaClass) }
                                startActivity(intent)
                            }
                            .show()
                    }
                }

            }

        RxView.clicks(delect)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.userlogin()) {
                    MaterialDialog.Builder(context)
                        .title(getText(R.string.song_delsong))
                        .content(getText(R.string.song_delsongs))
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .positiveText(getText(R.string.carry))
                        .negativeText(getText(R.string.cancel))
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->

                            val idmap = mutableListOf<Music>()
                            for (ite in adapter.listdet) {
                                if (ite.type != 0) {
                                    idmap.add(ite.song)
                                }
                            }
                            if (idmap.isNotEmpty()) {
                                for (its in idmap) {
                                    val downs = mDownDao.querys(its.song_id)
                                    if (downs.size > 0) {

                                        mDownDao.delete(downs[0].id)
                                        FilesUtils.delFile(downs[0].uri)
                                        Toast.makeText(
                                            context,
                                            getText(R.string.song_delsongsucc),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }else{
                                        Toast.makeText(
                                            context,
                                            getText(R.string.song_download_error),
                                            Toast.LENGTH_SHORT
                                        ).show()
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

        RxView.clicks(deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                foods.visibility = View.GONE
                view.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)
                adapter.update(false)
                bools = true

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
        adapters = PlaySongAdapter(song, context)
        in_list.adapter = adapters
        adapters.setOnItemClickListener(object : PlaySongAdapter.ItemClickListener {
            @SuppressLint("ResourceAsColor")
            override fun onItemClick(view: View, position: Int) {
                MaterialDialog.Builder(context)
                    .title(getText(R.string.song_addsong))
                    .content(getText(R.string.song_addsonglist))
                    .positiveText(getText(R.string.carry))
                    .negativeText(getText(R.string.cancel))
                    .positiveColorRes(R.color.colorAccentDarkTheme)
                    .negativeColorRes(R.color.red)
                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                        val playlist: Playlist = mPlaylistDao.query(song[position].play_list_id)[0]
                        val playsong = mCollectDao.query(song[position].play_list_id)
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

    override fun onStop() {
        super.onStop()
        try {
            adapter.notifyItemChanged(0)
        } catch (e: Exception) {
        }
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

        if (MusicApp.getAblumid() == album_id) {
            adapter.notifyItemChanged(0)
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

            @SuppressLint("SetTextI18n", "CheckResult", "ResourceAsColor")
            override fun onNext(data: Int) {
                poplue.visibility = View.VISIBLE

                val downs = mDownDao.querys(songlist[data].song_id)
                if (downs.size > 0) {
                    imageView5.setImageResource(R.drawable.xaidel)
                    dolw.text = getText(R.string.delete)
                }else{
                    imageView5.setImageResource(R.drawable.ic_file_download)
                    dolw.text = getText(R.string.song_download)
                }

                val collects = mCollectDao.querys(songlist[data].song_id)
                if (collects.size > 0) {
                    del_txt.text = getText(R.string.song_collectsucc)
                }else{
                    del_txt.text = getText(R.string.song_collect)
                }

                RxView.clicks(relat4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        if (MusicApp.network() == -1) {
                            Toast.makeText(
                                MusicApp.getAppContext(),
                                getText(R.string.error_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (downs.size == 0) {
                                if (MusicApp.userlogin()) {
                                    MaterialDialog.Builder(context)
                                        .title(getText(R.string.download_song))
                                        .content(getText(R.string.download_playsong))
                                        .positiveText(getText(R.string.carry))
                                        .negativeText(getText(R.string.cancel))
                                        .positiveColorRes(R.color.colorAccentDarkTheme)
                                        .negativeColorRes(R.color.red)
                                        .onPositive { _: MaterialDialog?, _: DialogAction? ->

                                            val request = OkGo.get<File>(songlist[data].uri)
                                            OkDownload.request(
                                                songlist[data].uri,
                                                request
                                            ) //
                                                .priority(0)
                                                .folder(context.getExternalFilesDir("")!!.absolutePath+"/download")
                                                .fileName("music" + songlist[data].song_id) //
                                                .save() //
                                                .register(
                                                    LogDownloadListener(
                                                        songlist[data],
                                                        context,
                                                        0,
                                                        downs,0
                                                    )
                                                ) //
                                                .start()


                                        }
                                        .show()
                                } else {
                                    MaterialDialog.Builder(context)
                                        .title(getText(R.string.go))
                                        .content(getText(R.string.ungoset))
                                        .positiveText(getText(R.string.carry))
                                        .negativeText(getText(R.string.cancel))
                                        .positiveColorRes(R.color.colorAccentDarkTheme)
                                        .negativeColorRes(R.color.red)
                                        .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                            val intent = Intent()
                                            context.let {
                                                intent.setClass(
                                                    it,
                                                    LoginActivity().javaClass
                                                )
                                            }
                                            startActivity(intent)
                                        }
                                        .show()
                                }

                            } else {
                                MaterialDialog.Builder(context)
                                    .title(getText(R.string.song_delsong))
                                    .content(getText(R.string.song_delsongs))
                                    .positiveColorRes(R.color.colorAccentDarkTheme)
                                    .negativeColorRes(R.color.red)
                                    .positiveText(getText(R.string.carry))
                                    .negativeText(getText(R.string.cancel))
                                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                            mDownDao.delete(downs[0].id)
                                            FilesUtils.delFile(downs[0].uri)
                                            Toast.makeText(
                                                context,
                                                getText(R.string.song_delsongsucc),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    }
                                    .show()
                            }


                        }
                    }

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
                        if (MusicApp.network() == -1) {
                            Toast.makeText(
                                MusicApp.getAppContext(),
                                getText(R.string.error_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (MusicApp.userlogin()) {
                                poplue.visibility = View.GONE
                                in_indel.visibility = View.VISIBLE
                                del.visibility = View.GONE
                                in_title.text = getText(R.string.song_but)
                                val list: MutableList<Playlist> =
                                    mPlaylistDao.querys(sp.getString("userid", "").toString())
                                val idmap = mutableListOf<Music>()
                                idmap.add(songlist[data])
                                initSongLists(list, idmap)
                            } else {
                                MaterialDialog.Builder(context)
                                    .title(getText(R.string.go))
                                    .content(getText(R.string.ungoset))
                                    .positiveText(getText(R.string.carry))
                                    .negativeText(getText(R.string.cancel))
                                    .positiveColorRes(R.color.colorAccentDarkTheme)
                                    .negativeColorRes(R.color.red)
                                    .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                        val intent = Intent()
                                        context.let {
                                            intent.setClass(
                                                it,
                                                LoginActivity().javaClass
                                            )
                                        }
                                        startActivity(intent)
                                    }
                                    .show()
                            }
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

