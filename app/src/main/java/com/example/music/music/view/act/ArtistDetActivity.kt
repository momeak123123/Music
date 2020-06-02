package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.ArtistDetAdapter
import com.example.music.adapter.ArtistListAdapter
import com.example.music.adapter.ArtistTagAdapter
import com.example.music.bean.AlbumDet
import com.example.music.bean.Artists
import com.example.music.bean.Hierarchy
import com.example.music.bean.Music
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.ArtistDetContract
import com.example.music.music.presenter.ArtistDetPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_artist.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistDetActivity : BaseMvpActivity<ArtistDetContract.IPresenter>() , ArtistDetContract.IView {

    companion object {
        lateinit var observer: Observer<JsonObject>
    }

    private lateinit var context: Context
    override fun registerPresenter() = ArtistDetPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.artist_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        context = this
    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        val id = bundle?.get("id") as Long
        val type = bundle.get("type") as Int
        getPresenter().listdata(context,id,type)

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

                val artist: Map<String,String> = Gson().fromJson(
                    data.getAsJsonObject("artist"),
                    object : TypeToken<Map<String,String>>() {}.type
                )
                Glide.with(context).load(artist["artist_picurl"]).placeholder(R.color.main_black_grey).into(back)
                artist_name.text=artist["artist_name"]
                artist_txt.text=artist["brief_desc"]

                val albums: List<AlbumDet> = Gson().fromJson(
                    data.getAsJsonArray("albums"),
                    object : TypeToken<List<AlbumDet>>() {}.type
                )
                if (albums.isNotEmpty()) {
                    initSingerList(albums)
                }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }


    }


    private fun initSingerList(artists: List<AlbumDet>) {
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        val adapter = ArtistDetAdapter(artists, context)
        recyc_item.adapter = adapter
        recyc_item.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
