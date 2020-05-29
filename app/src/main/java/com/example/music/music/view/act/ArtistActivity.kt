package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.ArtistListAdapter
import com.example.music.adapter.ArtistTagAdapter
import com.example.music.adapter.HomeListAdapter
import com.example.music.bean.Artists
import com.example.music.bean.Hierarchy
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.ArtistContract
import com.example.music.music.presenter.ArtistPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_artist.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistActivity : BaseMvpActivity<ArtistContract.IPresenter>(), ArtistContract.IView {
    companion object {
        lateinit var observer: Observer<JsonObject>
        lateinit var observers: Observer<JsonArray>
    }

    private lateinit var adapter: ArtistListAdapter
    var liatdata = mutableListOf<Artists>()
    override fun registerPresenter() = ArtistPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.music_artist
    }

    var varieties: Int = 0
    var letter: Int = 0
    var bool: Boolean = false
    private lateinit var context: Context

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
    }

    override fun initData() {
        super.initData()
        top_title.text = "歌星"
        getPresenter().taglist(context)
        /* val sp: SharedPreferences =
             context.getSharedPreferences("Music", Context.MODE_PRIVATE)

         if (!sp.getString("artist", "").equals("")) {
             val artist: List<Artists> = Gson().fromJson(
                 sp.getString("artist", ""),
                 object : TypeToken<List<Artists>>() {}.type
             )
             if (artist.isNotEmpty()) {
                 initSingerList(artist)
             }
         }*/

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

    }


    override fun onResume() {
        super.onResume()

        observer = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {
                val hierarchy1 = Gson().fromJson<Array<Hierarchy>>(
                    data.getAsJsonArray("hierarchy_1"),
                    Array<Hierarchy>::class.java
                ).toMutableList()
                val hierarchy2 = Gson().fromJson<Array<Hierarchy>>(
                    data.getAsJsonArray("hierarchy_2"),
                    Array<Hierarchy>::class.java
                ).toMutableList()
                if (hierarchy1.isNotEmpty()) {
                    // val hier = Hierarchy(0, 1, "全部")
                    // hierarchy1.add(0, hier)
                    initTopList(hierarchy1)
                }
                if (hierarchy2.isNotEmpty()) {
                    // val hier = Hierarchy(0, 2, "全部")
                    // hierarchy2.add(0, hier)
                    initAlbumList(hierarchy2)
                }
                hierarchy1[0].cat_hierarchy = 0
                hierarchy2[0].cat_hierarchy = 0
                varieties = hierarchy1[0].cat_id
                letter = hierarchy2[0].cat_id
                getPresenter().listdata(context, varieties, letter)
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }

        observers = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonArray) {
                val artist: MutableList<Artists> = Gson().fromJson(
                    data,
                    object : TypeToken<MutableList<Artists>>() {}.type
                )
                if (artist.isNotEmpty()) {
                    if (bool) {
                        initSingerListup(artist)
                    }else{
                        initSingerList(artist)
                    }

                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        }
    }

    fun initTopList(list: MutableList<Hierarchy>) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyc_tab1.layoutManager = linearLayoutManager
        recyc_tab1.itemAnimator = DefaultItemAnimator()
        val adapter = ArtistTagAdapter(list, context, 1)
        recyc_tab1.adapter = adapter
        recyc_tab1.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        bool = true
                        varieties = list[position].cat_id
                        getPresenter().listdata(context, varieties, letter)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )

    }


    fun initAlbumList(album: MutableList<Hierarchy>) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyc_tab2.layoutManager = linearLayoutManager
        recyc_tab2.itemAnimator = DefaultItemAnimator()
        val adapter = ArtistTagAdapter(album, context, 2)
        recyc_tab2.adapter = adapter
        recyc_tab2.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        bool = true
                        letter = album[position].cat_id
                        getPresenter().listdata(context, varieties, letter)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    private fun initSingerList(artists: MutableList<Artists>) {
        recyc_list.layoutManager = LinearLayoutManager(context)
        recyc_list.itemAnimator = DefaultItemAnimator()
        adapter = ArtistListAdapter(artists, context)
        recyc_list.adapter = adapter
        recyc_list.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artists[position].artist_id)
                        intent.putExtra("type", artists[position].type)
                        startActivity(intent)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    fun initSingerListup(artists: MutableList<Artists>) {
        adapter.removeAll()
       adapter.addAll(artists)
        adapter.notifyDataSetChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

