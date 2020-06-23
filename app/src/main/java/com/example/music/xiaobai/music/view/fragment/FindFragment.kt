package com.example.music.xiaobai.music.view.fragment

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
import com.example.music.xiaobai.MainActivity
import com.example.music.xiaobai.MusicApp
import com.example.music.xiaobai.R
import com.example.music.xiaobai.adapter.SongListAdapter
import com.example.music.xiaobai.music.contract.FindContract
import com.example.music.xiaobai.music.model.MainModel.Companion.addsonglist
import com.example.music.xiaobai.music.presenter.FindPresenter
import com.example.music.xiaobai.music.view.act.LoginActivity
import com.example.music.xiaobai.music.view.act.SongDetActivity
import com.example.music.xiaobai.sql.bean.Playlist
import com.example.music.xiaobai.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_find.*
import kotlinx.android.synthetic.main.fragment_unfild.*
import kotlinx.android.synthetic.main.song_add.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindFragment : BaseMvpFragment<FindContract.IPresenter>(), FindContract.IView {

    private var bools: Boolean = false
    private var adapter: SongListAdapter? = null

    companion object {
        lateinit var observer: Observer<JsonObject>
        lateinit var observers: Observer<JsonArray>
    }

    private lateinit var sp: SharedPreferences
    override fun registerPresenter() = FindPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_find
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    override fun initData() {
        super.initData()
        sp = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)

        if (sp.getBoolean("login", false)) {
            unfild.visibility = View.GONE

        } else {
            unfild.visibility = View.VISIBLE
        }
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if (MusicApp.getNetwork()) {
                loaddata()
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

        RxView.clicks(add)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                in_add.visibility = View.VISIBLE
                MainActivity.craet(false)

            }

        RxView.clicks(in_deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { o: Any? ->
                if (et_name.isNotEmpty) {
                    context?.let { addsonglist(it, et_name.editValue) }
                    et_name.clear()
                    in_add.visibility = View.GONE
                    MainActivity.craet(true)
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.song_error_name),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        RxView.clicks(in_cancel)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe{ o: Any? ->
                in_add.visibility = View.GONE
                MainActivity.craet(true)
            }
        RxView.clicks(login)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, LoginActivity().javaClass) }
                startActivity(intent)
            }

    }

    fun loaddata(){
        if (MusicApp.getNetwork()) {
            val list: MutableList<Playlist> = mPlaylistDao.queryAll()
            if (list.size > 0) {
                initSongList(list)
                bools = true
            } else {
                context?.let { getPresenter().listdata(it) }
            }

        } else {
            Toast.makeText(
                context,
                getText(R.string.nonet),
                Toast.LENGTH_LONG
            ).show()
        }

        if (swipe_refresh_layout != null) {
            swipe_refresh_layout.isRefreshing = false
        }
    }


    override fun onResume() {
        super.onResume()

        if (sp.getBoolean("login", false)) {
            unfild.visibility = View.GONE
            loaddata()
        } else {
            unfild.visibility = View.VISIBLE
        }





        observer = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {

                val song: Playlist = Gson().fromJson(
                    data,
                    object : TypeToken<Playlist>() {}.type
                )
                mPlaylistDao.insert(song)
                adapter!!.add(song)


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observers = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(listdata: JsonArray) {

                val data: MutableList<Playlist> = Gson().fromJson(
                    listdata,
                    object : TypeToken<MutableList<Playlist>>() {}.type
                )

                if (data.size > 0) {
                    for (it in data) {
                        mPlaylistDao.insert(it)
                    }

                    initData()
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    /**
     * 带输入框的对话框
     */
    /* @SuppressLint("ResourceAsColor")
     private fun showInputDialog() {
         context?.let {
             MaterialDialog.Builder(it)
                 .title(R.string.song_create)
                 .inputType(
                     InputType.TYPE_CLASS_TEXT
                 )
                 .input(
                     getString(R.string.song_error_name),
                     "",
                     false,
                     MaterialDialog.InputCallback { _, input ->
                         context?.let { it1 -> getPresenter().addSongList(it1,input.toString()) }
                     }
                 )
                 .inputRange(1, 10)
                 .positiveText(R.string.song_deter)
                 .positiveColor(R.color.orange)
                 .negativeText(R.string.song_cancel)
                 .negativeColor(R.color.blue)
                 .cancelable(false)
                 .show()
         }
     }*/

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
