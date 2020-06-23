package com.example.music.xiaobai.music.view.act

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
import com.example.music.xiaobai.MusicApp
import com.example.music.xiaobai.R
import com.example.music.xiaobai.adapter.ArtistListAdapter
import com.example.music.xiaobai.adapter.ArtistTagAdapter
import com.example.music.xiaobai.bean.Artists
import com.example.music.xiaobai.bean.Hierarchy
import com.example.music.xiaobai.config.ItemClickListener
import com.example.music.xiaobai.music.contract.ArtistContract
import com.example.music.xiaobai.music.presenter.ArtistPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_download_list.*
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
        top_title.text =getString(R.string.item3s)

        val sp: SharedPreferences =
            context.getSharedPreferences("Music", Context.MODE_PRIVATE)

        if (!sp.getString("h1", "").equals("")) {
            getPresenter().taglist(context,false)
            val hierarchy1: List<Hierarchy> = Gson().fromJson(
                sp.getString("h1", ""),
                object : TypeToken<List<Hierarchy>>() {}.type
            )
            val hierarchy2: List<Hierarchy> = Gson().fromJson(
                sp.getString("h2", ""),
                object : TypeToken<List<Hierarchy>>() {}.type
            )
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
            loaddata()

        }else{
            if(MusicApp.getNetwork()){
                getPresenter().taglist(context,true)
            }else{
                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
                Toast.makeText(
                    context,
                    getText(R.string.nonet),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }

    fun loaddata(){
        if (MusicApp.getNetwork()) {
            getPresenter().listdata(context, varieties, letter)
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
    }


    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { loaddata() })

        RxView.clicks(top_flot)
            .throttleFirst(3, TimeUnit.SECONDS)
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

    fun initTopList(list: List<Hierarchy>) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyc_tab1.layoutManager = linearLayoutManager
        recyc_tab1.itemAnimator = DefaultItemAnimator()
        val adapter = ArtistTagAdapter(list, context, 1)
        recyc_tab1.adapter = adapter
        recyc_tab1.addOnItemTouchListener(
            ItemClickListener(recyc_tab1,
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


    fun initAlbumList(album: List<Hierarchy>) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyc_tab2.layoutManager = linearLayoutManager
        recyc_tab2.itemAnimator = DefaultItemAnimator()
        val adapter = ArtistTagAdapter(album, context, 2)
        recyc_tab2.adapter = adapter
        recyc_tab2.addOnItemTouchListener(
            ItemClickListener(recyc_tab2,
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
            ItemClickListener(recyc_list,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        println(artists)
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artists[position].artsit_id)
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
