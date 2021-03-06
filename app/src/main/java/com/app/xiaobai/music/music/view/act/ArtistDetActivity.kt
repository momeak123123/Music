package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.MusicApp
import com.app.xiaobai.music.R
import com.app.xiaobai.music.SearchIndexActivity
import com.app.xiaobai.music.adapter.ArtistDetAdapter
import com.app.xiaobai.music.bean.Album
import com.app.xiaobai.music.music.contract.ArtistDetContract
import com.app.xiaobai.music.music.model.SearchModel
import com.app.xiaobai.music.music.presenter.ArtistDetPresenter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.artist_index.swipe_refresh_layout
import kotlinx.android.synthetic.main.search_song_index.*
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistDetActivity : BaseMvpActivity<ArtistDetContract.IPresenter>() , ArtistDetContract.IView {

    companion object {
        lateinit var observer: Observer<JsonObject>
        lateinit var observers: Observer<Boolean>
    }
    private lateinit var urls: String
    private lateinit var names: String
    private lateinit var txts: String
    private lateinit var context: Context
    override fun registerPresenter() = ArtistDetPresenter::class.java
    var songlist = mutableListOf<Album>()
    override fun getLayoutId(): Int {
        return R.layout.artist_index
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
    }

    override fun initData() {
        super.initData()
        try {
            val bundle = intent.extras
            val id = bundle?.get("id") as Long
            val type = bundle.get("type") as Int
            if(MusicApp.network()!=-1){
                swipe_refresh_layout.isRefreshing = true
                getPresenter().listdata(context,id,type)
            }else{
                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
                finish()

            }
        }catch (e:Exception){}

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { initData() })
    }


    override fun onResume() {
        super.onResume()

        observer = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {
                val artist: Map<String,String> = Gson().fromJson(
                    data.getAsJsonObject("artist_info"),
                    object : TypeToken<Map<String,String>>() {}.type
                )
                urls=artist["pic_url"].toString()
                names=artist["name"].toString()
                txts=artist["brief_desc"].toString()
                val albums: MutableList<Album> = Gson().fromJson(
                    data.getAsJsonArray("album_info"),
                    object : TypeToken<MutableList<Album>>() {}.type
                )
                if (albums.isNotEmpty()) {
                    songlist.clear()
                    songlist = albums
                    val asrt =  Album(0,"","",0,"","","",0,"")
                    songlist.add(0,asrt)
                    initSingerList(songlist)
                }else{
                    songlist.clear()
                    val asrt =  Album(0,"","",0,"","","",0,"")
                    songlist.add(0,asrt)
                    initSingerList(songlist)
                }

                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        }


        observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if(data){
                    finish()
                }
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }
        }
    }


    private fun initSingerList(artists: List<Album>) {
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        recyc_item.setHasFixedSize(true)
        val adapter = ArtistDetAdapter(artists, context,names,txts,urls)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : ArtistDetAdapter.ItemClickListener {
            override fun onItemClick(view:View,position: Int) {
                if(position>0){
                    val intent = Intent()
                    intent.setClass(context, AlbumDetActivity().javaClass)
                    intent.putExtra("album_id",artists[position].album_id)
                    intent.putExtra("album_type",artists[position].type)
                    intent.putExtra("album_time",0L)
                    intent.putExtra("palylist_name",artists[position].name)
                    intent.putExtra("info",artists[position].info)
                    intent.putExtra("cover",artists[position].pic_url)
                    intent.putExtra("type",2)
                    startActivity(intent)
                }

            }
        })


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
