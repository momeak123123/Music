package com.example.xiaobai.music.music.view.fragment

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
import com.example.xiaobai.music.MainActivity
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.HomeDetAdapter
import com.example.xiaobai.music.adapter.PlaySongAdapter
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.config.LogDownloadListener
import com.example.xiaobai.music.music.contract.HomeContract
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.presenter.HomePresenter
import com.example.xiaobai.music.music.view.act.LoginActivity
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mCollectDao
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.example.xiaobai.music.utils.FilesUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
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

    private lateinit var lists: List<TopList>
    private var adapter: HomeDetAdapter? = null
    private lateinit var sp: SharedPreferences

    companion object {
        lateinit var adaptert: PlaySongAdapter
        lateinit var observer: Observer<JsonObject>
        lateinit var observert: Observer<Music>
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


        observert = object : Observer<Music> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n", "CheckResult")
            override fun onNext(data: Music) {
                poplue.visibility = View.VISIBLE
                MainActivity.craet(true)
                edit_song.text =
                    getText(R.string.album).toString() + ":" + data.album_name
                var srtist_name = ""
                for (it in data.all_artist) {
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
                        MainActivity.craetdert(false)
                        MainActivity.craet(false)
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
                        val sps =
                            requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
                        if (sps.getBoolean("login", false)) {
                            poplue.visibility = View.GONE
                            in_indel.visibility = View.VISIBLE
                            context?.let { it1 -> Glide.with(it1).load("").into(del) }
                            in_title.text = getText(R.string.song_but)
                            val list: MutableList<Playlist> = mPlaylistDao.querys(sp.getString("userid","").toString())
                            val idmap = mutableListOf<Music>()
                            idmap.add(data)
                            initSongLists(list, idmap)

                        } else {
                            context?.let { it1 ->
                                MaterialDialog.Builder(it1)
                                    .title("登录")
                                    .content("未登陆账号，是否登录")
                                    .positiveText(getText(R.string.carry))
                                    .negativeText(getText(R.string.cancel))
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



                val downs = mDownDao.querys(data.song_id)
                if (downs.size > 0) {
                    imageView5.setImageResource(R.drawable.xaidel)
                    dolw.text = getText(R.string.delete)
                }else{
                    imageView5.setImageResource(R.drawable.ic_file_download)
                    dolw.text = getText(R.string.song_download)
                }

                val collects = mCollectDao.querys(data.song_id)
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
                                    context?.let { it1 ->
                                        MaterialDialog.Builder(it1)
                                            .title(getText(R.string.download_song))
                                            .content(getText(R.string.download_playsong))
                                            .positiveText(getText(R.string.carry))
                                            .negativeText(getText(R.string.cancel))
                                            .positiveColorRes(R.color.colorAccentDarkTheme)
                                            .negativeColorRes(R.color.red)
                                            .onPositive { _: MaterialDialog?, _: DialogAction? ->

                                                val request = OkGo.get<File>(data.uri)
                                                OkDownload.request(
                                                    data.uri,
                                                    request
                                                ) //
                                                    .priority(0)
                                                    .folder(it1.getExternalFilesDir("")!!.absolutePath+"/download")
                                                    .fileName("music" + data.song_id ) //
                                                    .save() //
                                                    .register(
                                                        LogDownloadListener(
                                                            data,
                                                            context,
                                                            0,
                                                            downs,0
                                                        )
                                                    ) //
                                                    .start()


                                            }
                                            .show()
                                    }
                                } else {
                                    context?.let { it1 ->
                                        MaterialDialog.Builder(it1)
                                            .title(getText(R.string.go))
                                            .content(getText(R.string.ungoset))
                                            .positiveText(getText(R.string.carry))
                                            .negativeText(getText(R.string.cancel))
                                            .positiveColorRes(R.color.colorAccentDarkTheme)
                                            .negativeColorRes(R.color.red)
                                            .onPositive { _: MaterialDialog?, _: DialogAction? ->
                                                val intent = Intent()
                                                context.let {
                                                    if (it != null) {
                                                        intent.setClass(
                                                            it,
                                                            LoginActivity().javaClass
                                                        )
                                                    }
                                                }
                                                startActivity(intent)
                                            }
                                            .show()
                                    }
                                }

                            } else {
                                context?.let { it1 ->
                                    MaterialDialog.Builder(it1)
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
                    .positiveText(getText(R.string.carry))
                    .negativeText(getText(R.string.cancel))
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
                                        context!!,
                                        songs,
                                        num,
                                        song[position].play_list_id, 2, position
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
                                    song[position].play_list_id, 2, position
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


