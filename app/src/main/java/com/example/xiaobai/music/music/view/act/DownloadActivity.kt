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
import com.example.xiaobai.music.adapter.DownloadAdapter
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.artistlist
import com.example.xiaobai.music.music.contract.DownloadContract
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.presenter.DownloadPresenter
import com.example.xiaobai.music.sql.bean.Down
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mCollectDao
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.example.xiaobai.music.utils.FilesUtils
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
        lateinit var adapter: DownloadAdapter
    }


    private lateinit var data: MutableList<Down>
    private lateinit var sp: SharedPreferences
    private var bools: Boolean = true
    private lateinit var names: String
    private var album_id: Long = 2
    private lateinit var covers: String
    private lateinit var txts: String
    var songlist = mutableListOf<Music>()

    private lateinit var context: Context
    override fun registerPresenter() = DownloadPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.download_list
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        cencel.visibility = View.GONE
        down.text = getText(R.string.love)
    }

    override fun initData() {
        super.initData()
        sp = getSharedPreferences("User", Context.MODE_PRIVATE)
        swipe_refresh_layout.isRefreshing = true
        loadData()
    }


    fun loadData() {

        data = mDownDao.queryAll()
        val song = mutableListOf<Music>()

        if (data.size > 0) {
            for (i in 0 until data.size) {

                val artist = mutableListOf<artistlist>()
                artist.add(0, artistlist(data[i].artist_id, data[i].artist))

                val music = Music(
                    data[i].name,
                    data[i].album_name,
                    data[i].album_id,
                    data[i].song_id,
                    data[i].uri,
                    artist,
                    data[i].pic_url,
                    data[i].song_list_id,
                    data[i].publish_time
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
            }
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
                                if (ite.type == 1) {
                                    idmap.add(ite.song)
                                }
                            }
                            if (idmap.isNotEmpty()) {
                                for (its in idmap) {
                                    for (i in 0 until data.size) {
                                        if (its.song_id == data[i].song_id) {
                                            mDownDao.delete(data[i].id)
                                            FilesUtils.delFile(data[i].uri)
                                            adapter.removeAll(its)
                                            Toast.makeText(
                                                context,
                                                getText(R.string.song_delsongsucc),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged()
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
        AlbumDetActivity.adapters = PlaySongAdapter(song, context)
        in_list.adapter = AlbumDetActivity.adapters
        AlbumDetActivity.adapters.setOnItemClickListener(object :
            PlaySongAdapter.ItemClickListener {
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
                                        if (sea.song_list_id == 0L) {
                                            songs.remove(sea)
                                            Toast.makeText(
                                                context,
                                                getText(R.string.error_searcher),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            if (sea.song_id == det.song_id) {
                                                songs.remove(sea)
                                            }
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

        names = "下载列表"
        covers = song[1].pic_url
        txts = ""
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyc_item.isNestedScrollingEnabled = false
        recyc_item.layoutManager = layoutManager
        recyc_item.itemAnimator = DefaultItemAnimator()
        recyc_item.setHasFixedSize(true)
        adapter = DownloadAdapter(song, context, album_id, txts, covers, names)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : DownloadAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (position > 0) {
                    if (adapter.type == 0) {

                        val json: String = Gson().toJson(song)
                        val intent = Intent()
                        intent.setClass(context, MusicPlayActivity().javaClass)
                        intent.putExtra("album_id", album_id)
                        intent.putExtra("pos", position - 1)
                        intent.putExtra("list", json)
                        intent.putExtra("type", 4)
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

        loadData()

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
                    intent.putExtra("type", 4)
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

                val downs = mDownDao.querys(songlist[data].song_id)
                imageView5.setImageResource(R.drawable.xaidel)
                dolw.text = getText(R.string.delete)
                if (downs.size > 0) {
                    del_txt.text = getText(R.string.song_collectsucc)

                } else {
                    del_txt.text = getText(R.string.song_collect)
                }

                RxView.clicks(relat4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {

                        MaterialDialog.Builder(context)
                            .title(getText(R.string.song_delsong))
                            .content(getText(R.string.song_delsongs))
                            .positiveColorRes(R.color.colorAccentDarkTheme)
                            .negativeColorRes(R.color.red)
                            .positiveText(getText(R.string.carry))
                            .negativeText(getText(R.string.cancel))
                            .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                poplue.visibility = View.GONE
                                mDownDao.delete(downs[0].id)
                                FilesUtils.delFile(downs[0].uri)
                                adapter.remove(data)
                                Toast.makeText(
                                    context,
                                    getText(R.string.song_delsongsucc),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .show()
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
