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
import com.bumptech.glide.Glide
import com.example.xiaobai.music.IndexActivity
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SongListAdapter
import com.example.xiaobai.music.bean.SongLists
import com.example.xiaobai.music.music.contract.MyContract
import com.example.xiaobai.music.music.presenter.MyPresenter
import com.example.xiaobai.music.music.view.act.DownloadActivity
import com.example.xiaobai.music.music.view.act.LoginActivity
import com.example.xiaobai.music.music.view.act.SongDetActivity
import com.example.xiaobai.music.music.view.act.UserSetActivity
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.iv_cover
import kotlinx.android.synthetic.main.fragment_unlogin.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyFragment : BaseMvpFragment<MyContract.IPresenter>(), MyContract.IView {

    private var nums: Int = 0

    private var bools: Boolean = false
    private var adapter: SongListAdapter? = null

    companion object {
        lateinit var observer: Observer<Boolean>
        lateinit var observert: Observer<JsonObject>
        lateinit var observers: Observer<JsonArray>
    }

    private lateinit var sp: SharedPreferences

    override fun registerPresenter() = MyPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    override fun initData() {
        super.initData()

        sp = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        if (sp.getBoolean("login", false)) {
            context?.let { getPresenter().data(it) }
        }

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(add)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                Observable.just(true).subscribe(IndexActivity.observer)
            }

        RxView.clicks(item1)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }
        RxView.clicks(item3)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }
        RxView.clicks(item5)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (nums > 0) {
                    val intent = Intent()
                    context?.let { it1 -> intent.setClass(it1, DownloadActivity().javaClass) }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.song_download_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }


        RxView.clicks(set)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, UserSetActivity().javaClass) }
                startActivity(intent)
            }


        RxView.clicks(btn_uplogin)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, LoginActivity().javaClass) }
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()

        if (sp.getBoolean("login", false)) {
            context?.let { getPresenter().data(it) }
            nums = mDownDao.queryAll().count()
            like_num.text = nums.toString()
        }

        loaddata()

        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if (data) {

                    Glide.with(requireContext()).load(sp.getString("url", ""))
                        .placeholder(R.color.main_black_grey).into(iv_cover)
                    name.text = sp.getString("nickname", "")
                    city.text = sp.getString("countries", "")
                    attention_num.text = sp.getString("follow", "")
                    collect_num.text = sp.getString("collect", "")
                    sign.text = sp.getString("message", "")
                    MusicApp.setMinute(sp.getString("follow", "").toString().toInt())
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observert = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {

                val song: SongLists = Gson().fromJson(
                    data,
                    object : TypeToken<SongLists>() {}.type
                )
                val play = Playlist()
                play.name = song.name
                play.pic_url = song.pic_url
                play.play_list_id = song.play_list_id
                play.song_num = song.song_num
                play.user_id = sp.getString("userid", "").toString()
                play.create_time = song.create_time
                mPlaylistDao.insert(play)
                adapter!!.add(play)


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observers = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(listdata: JsonArray) {

                val data: MutableList<SongLists> = Gson().fromJson(
                    listdata,
                    object : TypeToken<MutableList<SongLists>>() {}.type
                )
                if (data.size > 0) {
                    for (ited in data) {
                        val play = Playlist()
                        play.name = ited.name
                        play.pic_url = ited.pic_url
                        play.play_list_id = ited.play_list_id
                        play.song_num = ited.song_num
                        play.user_id = sp.getString("userid", "").toString()
                        play.create_time = ited.create_time
                        mPlaylistDao.insert(play)
                    }
                }

                loaddata()
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

    }


    fun loaddata() {

        if (sp.getBoolean("login", false)) {

            include.visibility = View.GONE

            Glide.with(requireContext()).load(sp.getString("url", ""))
                .placeholder(R.color.main_black_grey).into(iv_cover)
            name.text = sp.getString("nickname", "")
            city.text = sp.getString("countries", "")
            attention_num.text = sp.getString("follow", "")
            collect_num.text = sp.getString("collect", "")
            sign.text = sp.getString("message", "")
            MusicApp.setMinute(sp.getString("follow", "").toString().toInt())

            var num = 0
            val list: MutableList<Playlist> =
                mPlaylistDao.querys(sp.getString("userid", "").toString())
            for (it in list) {
                num += it.song_num.toInt()
            }

            collect_num.text = num.toString()

            if (list.size > 0) {
                initSongList(list)
                bools = true
            } else {
                context?.let { getPresenter().listdata(it) }
            }


        } else {
            include.visibility = View.VISIBLE
        }

    }


    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Playlist>) {
        song_list.layoutManager = LinearLayoutManager(context)
        song_list.itemAnimator = DefaultItemAnimator()
        adapter = context?.let { SongListAdapter(song, it) }
        song_list.adapter = adapter
        adapter?.setOnItemClickListener(object : SongListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                context?.let { intent.setClass(it, SongDetActivity().javaClass) }
                intent.putExtra("id", song[position].id)
                intent.putExtra("playid", song[position].play_list_id)
                intent.putExtra("name", song[position].name)
                intent.putExtra("url", song[position].pic_url)
                intent.putExtra("num", song[position].song_num)
                startActivity(intent)

            }
        })

    }

}
