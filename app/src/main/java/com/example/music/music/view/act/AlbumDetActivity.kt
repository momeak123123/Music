package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.AlbumDetAdapter
import com.example.music.bean.Music
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.presenter.AlbumDetPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.album_index.*
import kotlinx.android.synthetic.main.album_index.swipe_refresh_layout
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.head.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetActivity : BaseMvpActivity<AlbumDetContract.IPresenter>() , AlbumDetContract.IView {

    companion object {
        fun finish() {
            finish()
        }

        lateinit var observer: Observer<JsonObject>
        lateinit var observers: Observer<Boolean>
    }

    private lateinit var names: String
    private var album_type: Int =0
    private var album_id: Long =0
    private lateinit var covers: String
    private lateinit var songdata: String
    private lateinit var txts: String

    var songlist = mutableListOf<Music>()

    private var type: Int = 0
    private lateinit var adapter: AlbumDetAdapter
    private lateinit var context: Context
    override fun registerPresenter() = AlbumDetPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.album_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context=this
    }

    override fun initData() {
        super.initData()
        refresh()
    }

    private fun refresh() {
        swipe_refresh_layout.isRefreshing = true
        val bundle = intent.extras
        type =  bundle?.get("type") as Int
        album_id = bundle.get("album_id") as Long
        album_type = bundle.get("album_type") as Int
        loadData()
        names = bundle.get("palylist_name") as String
        txts = bundle.get("info") as String
        covers = bundle.get("cover") as String
    }

   fun loadData(){
       if(type == 1){
           getPresenter().songdatas(album_id,album_type,context)
       }else{
           getPresenter().songdata(album_id,album_type,context)
       }
   }

    init {
        observer = object : Observer<JsonObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonObject) {

                val song : MutableList<Music> = Gson().fromJson(
                    data.asJsonObject.get("song_list").asJsonArray,
                    object : TypeToken<MutableList<Music>>() {}.type
                )

                if (song.isNotEmpty()) {
                    songlist = song
                    songlist.add(0,song[0])
                    initSongList(songlist)
                }
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { loadData() })

       /* if(isSDcardAvailable()){
            val request =
                OkGo.get<File>(apk.getUrl())
            OkDownload.request(apk.getId().toString(), request)
                .save()
                .folder(getMusicDir())
                .register(LogDownloadListener(apk, context))
                .start()
        }*/


    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Music>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyc_item.isNestedScrollingEnabled = false
        recyc_item.layoutManager = layoutManager
        recyc_item.itemAnimator = DefaultItemAnimator()
        recyc_item.setHasFixedSize(true)

        adapter =  AlbumDetAdapter(song, context,txts,covers,names)
        recyc_item.adapter = adapter
        recyc_item.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        if(position>0){
                            MusicPlayActivity.id = position
                            Observable.just(song).subscribe(MusicPlayActivity.observer)
                            val intent = Intent()
                            intent.setClass(context, MusicPlayActivity().javaClass)
                            startActivity(intent)
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )

        if (swipe_refresh_layout != null) {
            swipe_refresh_layout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
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

    override fun onDestroy() {
        super.onDestroy()
    }

}
