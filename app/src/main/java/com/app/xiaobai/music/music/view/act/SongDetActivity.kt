package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.adapter.SongDetAdapter
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.artistlist
import com.app.xiaobai.music.config.Constants
import com.app.xiaobai.music.config.LogDownloadListener
import com.app.xiaobai.music.music.contract.SongDetContract
import com.app.xiaobai.music.music.presenter.SongDetPresenter
import com.app.xiaobai.music.sql.bean.Collect
import com.app.xiaobai.music.sql.dao.mCollectDao
import com.app.xiaobai.music.sql.dao.mDownDao
import com.app.xiaobai.music.utils.FilesUtils
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
import kotlinx.android.synthetic.main.food.*
import kotlinx.android.synthetic.main.popule.*
import kotlinx.android.synthetic.main.song_index.*
import kotlinx.android.synthetic.main.song_set.*
import kotlinx.android.synthetic.main.song_set.edit_song
import mvp.ljb.kt.act.BaseMvpActivity
import java.io.File
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


    private lateinit var data: MutableList<Collect>
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
        val window = window
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        context = this
        cencel.text = getText(R.string.song_cancel_coll)
        relat3.visibility = View.GONE

    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        names = bundle?.get("name") as String
        imaurl = bundle.get("url") as String
        nums = bundle.get("num") as String
        ids = bundle.get("id") as Long
        playids = bundle.get("playid") as Long
        data = mCollectDao.query(playids)
        val song = mutableListOf<Music>()
        val artist = mutableListOf<artistlist>()
        if (MusicApp.network() != -1) {
            getPresenter().listdata(context, playids)
            swipe_refresh_layout.isRefreshing = true
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

    @SuppressLint("CheckResult", "ResourceAsColor")
    override fun initView() {
        super.initView()


        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if (MusicApp.network() != -1) {
                getPresenter().listdata(context, playids)
            } else {
                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
                Toast.makeText(
                    context,
                    getText(R.string.error_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
        })




        RxView.clicks(song_set_back)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)
            }
        RxView.clicks(item1)
            .throttleFirst(3, TimeUnit.SECONDS)
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
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                foods.visibility = View.VISIBLE
                view.visibility = View.VISIBLE
            }
        RxView.clicks(item3)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                set.visibility = View.GONE
                getPresenter().deldata(context, ids, playids)
            }

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
                    MaterialDialog.Builder(context)
                        .title(getText(R.string.song_cancel_coll))
                        .content(getText(R.string.song_cancel_colls))
                        .positiveText(getText(R.string.carry))
                        .negativeText(getText(R.string.cancel))
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
                        .onPositive { _: MaterialDialog?, _: DialogAction? ->

                            val idmap = mutableListOf<Music>()

                            for (ite in adapter.listdet) {
                                if (ite.type == 1) {
                                    idmap.add(ite.song)
                                }
                            }
                            if (idmap.isNotEmpty()) {
                                getPresenter().delsong(context, idmap, playids)
                                adapter.update(true)
                                bools = true
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
                    MaterialDialog.Builder(context)
                        .title(getText(R.string.download_song))
                        .content(getText(R.string.download_playsong))
                        .positiveText(getText(R.string.carry))
                        .negativeText(getText(R.string.cancel))
                        .positiveColorRes(R.color.colorAccentDarkTheme)
                        .negativeColorRes(R.color.red)
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
                                        if(Constants.Downnum()){
                                            val request = OkGo.get<File>(its.uri)
                                            OkDownload.request(its.uri, request) //
                                                .priority(0)
                                                .folder(context.getExternalFilesDir("")!!.absolutePath+"/download")
                                                .fileName("music" + its.song_id ) //
                                                .save() //
                                                .register(
                                                    LogDownloadListener(
                                                        its,
                                                        context,
                                                        0,
                                                        downs
                                                    )
                                                ) //
                                                .start()
                                        }else{
                                            Toast.makeText(
                                                context,
                                                getText(R.string.download_num),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

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
                                    for (itsd in data) {
                                        if (its.song_id == itsd.song_id) {
                                            mDownDao.delete(itsd.id)
                                            FilesUtils.delFile(itsd.uri)
                                            Toast.makeText(
                                                context,
                                                getText(R.string.song_delsongsucc),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                foods.visibility = View.GONE
                view.visibility = View.GONE
                adapter.type = 0
                adapter.notifyItemRangeChanged(1, songlist.size)
                adapter.update(false)
                bools = true

            }


    }


    override fun onResume() {
        super.onResume()

        if (MusicApp.getAblumid() == playids) {
            adapter.notifyItemChanged(0)
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

                    val datas = mCollectDao.query(playids)
                    if (datas.size == 0) {
                        for (it in song) {
                            val collect = Collect()
                            collect.playid = playids
                            collect.song_id = it.song_id
                            collect.name = it.name
                            collect.album_name = it.album_name
                            collect.album_id = it.album_id
                            collect.uri = it.uri
                            collect.artist = it.all_artist[0].name
                            collect.artist_id = it.all_artist[0].id
                            collect.pic_url = it.pic_url
                            collect.publish_time = it.publish_time
                            collect.song_list_id = it.song_list_id
                            mCollectDao.insert(collect)
                        }
                    }

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
                getPresenter().delsongs(context, songlist[data], data, playids)
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
                    view.visibility = View.GONE
                    set.visibility = View.GONE
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
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        poplue.visibility = View.GONE
                    }


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
                            if (downs.size > 0) {
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
                            } else {
                                if (MusicApp.userlogin()) {
                                    MaterialDialog.Builder(context)
                                        .title(getText(R.string.download_song))
                                        .content(getText(R.string.download_playsong))
                                        .positiveText(getText(R.string.carry))
                                        .negativeText(getText(R.string.cancel))
                                        .positiveColorRes(R.color.colorAccentDarkTheme)
                                        .negativeColorRes(R.color.red)
                                        .onPositive { _: MaterialDialog?, _: DialogAction? ->

                                            if(Constants.Downnum()){
                                                val request = OkGo.get<File>(songlist[data].uri)
                                                OkDownload.request(
                                                    songlist[data].uri,
                                                    request
                                                ) //
                                                    .priority(0)
                                                    .folder(context.getExternalFilesDir("")!!.absolutePath+"/download")
                                                    .fileName("music" + songlist[data].song_id ) //
                                                    .save() //
                                                    .register(
                                                        LogDownloadListener(
                                                            songlist[data],
                                                            context,
                                                            0,
                                                            downs
                                                        )
                                                    ) //
                                                    .start()
                                            }else{
                                                Toast.makeText(
                                                    context,
                                                    getText(R.string.download_num),
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
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }
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
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        adapter = SongDetAdapter(song, context, imaurl, playids, names, nums)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : SongDetAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                if (position > 0) {
                    if (adapter.type == 0) {

                        val json: String = Gson().toJson(song)
                        val intent = Intent()
                        intent.setClass(context, MusicPlayActivity().javaClass)
                        intent.putExtra("album_id", playids)
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
    }

}
