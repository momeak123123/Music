package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.*
import com.example.music.bean.*
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.AlbumContract
import com.example.music.music.presenter.AlbumPresenter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.home_item1.*
import kotlinx.android.synthetic.main.music_album.*
import kotlinx.android.synthetic.main.music_list.*
import kotlinx.android.synthetic.main.start_page.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumActivity : BaseMvpActivity<AlbumContract.IPresenter>() , AlbumContract.IView {

    private lateinit var context: Context

    override fun registerPresenter() = AlbumPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.music_album
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context=this
    }

    override fun initData() {
        super.initData()
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        val sp: SharedPreferences =
            context.getSharedPreferences("Music", Context.MODE_PRIVATE)
        val bundle = intent.extras
        val int = bundle?.get("album_type") as Int
        if(int==0){
            top_title.text="排行榜"
            if (!sp.getString("list", "").equals("")) {
                val list: List<TopList> = Gson().fromJson(
                    sp.getString("list", ""),
                    object : TypeToken<List<TopList>>() {}.type
                )
                if (list.isNotEmpty()) {
                    initTopList(list)
                }
            }
        }else{
            top_title.text="推荐专辑"
            if (!sp.getString("album", "").equals("")) {
                val album: List<Album> = Gson().fromJson(
                    sp.getString("album", ""),
                    object : TypeToken<List<Album>>() {}.type
                )
                if (album.isNotEmpty()) {
                    initAlbumList(album)
                }
            }
        }

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }


    }

    fun initTopList(list: List<TopList>){
        recyc_tab.layoutManager = GridLayoutManager(context, 2)
        recyc_tab.itemAnimator = DefaultItemAnimator()
        val adapter = AlbumTopAdapter(list, context)
        recyc_tab.adapter = adapter
        recyc_tab.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    fun initAlbumList(album: List<Album>){
        recyc_tab.layoutManager = GridLayoutManager(context, 3)
        recyc_tab.itemAnimator = DefaultItemAnimator()
        val adapter = AlbumListAdapter(album, context)
        recyc_tab.adapter = adapter
        recyc_tab.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )

    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
