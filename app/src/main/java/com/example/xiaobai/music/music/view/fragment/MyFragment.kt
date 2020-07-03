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
import com.example.xiaobai.music.adapter.SongListAdapter
import com.example.xiaobai.music.music.contract.MyContract
import com.example.xiaobai.music.music.model.MainModel
import com.example.xiaobai.music.music.presenter.MyPresenter
import com.example.xiaobai.music.music.view.act.*
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mDownDao
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_registered.*
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.iv_cover
import kotlinx.android.synthetic.main.fragment_unlogin.*
import kotlinx.android.synthetic.main.song_add.*
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

        loaddata()

        sp = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
        name.text = sp.getString("nickname", "")
        city.text = sp.getString("countries", "")
        attention_num.text = sp.getString("follow", "")
        collect_num.text = sp.getString("collect", "")
        sign.text = sp.getString("message", "")
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(add)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                in_add.visibility = View.VISIBLE
                MainActivity.craetdert(true)
                MainActivity.craet(true)

            }

        RxView.clicks(in_deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { o: Any? ->
                if (et_name.isNotEmpty) {
                    context?.let { MainModel.addsonglist(it, et_name.editValue) }
                    et_name.clear()
                    in_add.visibility = View.GONE
                    MainActivity.craetdert(false)
                    MainActivity.craet(false)

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
                if(nums>0){
                    val intent = Intent()
                    context?.let { it1 -> intent.setClass(it1, DownloadActivity().javaClass) }
                    startActivity(intent)
                }else{
                    Toast.makeText(
                        context,
                        getText(R.string.song_download_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        RxView.clicks(iv_cover)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, UserEditActivity().javaClass) }
                startActivity(intent)
            }

        RxView.clicks(set)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, SetActivity().javaClass) }
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

        loaddata()

        nums = mDownDao.queryt(1).count()
        println(nums)
        like_num.text = nums.toString()

        if(sp.getBoolean("login",false)){
            include.visibility = View.GONE
            Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
            name.text = sp.getString("nickname", "")
            city.text = sp.getString("countries", "")
            attention_num.text = sp.getString("follow", "")
            collect_num.text = sp.getString("collect", "")
            sign.text = sp.getString("message", "")

        }else{
            include.visibility = View.VISIBLE
        }

        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if(data){
                    val sp: SharedPreferences =
                        requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)

                    Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
                    name.text = sp.getString("nickname", "")
                    city.text = sp.getString("city", "")
                    attention_num.text = sp.getString("follow", "")
                    collect_num.text = sp.getString("collect", "")
                    sign.text = sp.getString("message", "")
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observert = object : Observer<JsonObject> {
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
