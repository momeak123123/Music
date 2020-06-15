package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.AlbumDetAdapter
import com.example.music.adapter.SongDetAdapter
import com.example.music.bean.Music
import com.example.music.bean.artistlist
import com.example.music.config.CornerTransform
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.SongDetContract
import com.example.music.music.presenter.SongDetPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.song_index.*
import mvp.ljb.kt.act.BaseMvpActivity


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongDetActivity : BaseMvpActivity<SongDetContract.IPresenter>() , SongDetContract.IView {

    companion object {
        lateinit var observer: Observer<JsonArray>
        lateinit var observers: Observer<Boolean>
    }

    private lateinit var nums: String
    private lateinit var names: String
    private lateinit var imaurl: String
    private lateinit var adapter: SongDetAdapter
    private lateinit var context: Context
    var songlist = mutableListOf<Music>()

    override fun registerPresenter() = SongDetPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.song_index
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
        context=this
    }

    override fun initData() {
        super.initData()


    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        val bundle = intent.extras
        names = bundle?.get("name") as String
        imaurl = bundle.get("url") as String
        nums = bundle.get("num") as String
        getPresenter().listdata(context,bundle.get("id") as Long)



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



    override fun onResume() {
        super.onResume()

        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: JsonArray) {

                val song : MutableList<Music> = Gson().fromJson(
                    data,
                    object : TypeToken<MutableList<Music>>() {}.type
                )

                if (song.isNotEmpty()) {
                    songlist = song
                    val one = mutableListOf<artistlist>()
                    val det =  Music("","",0,0,"", one,"","")
                    songlist.add(0,det)
                    initSongList(songlist)
                }else{
                    val one = mutableListOf<artistlist>()
                    val det =  Music("","",0,0,"", one,"","")
                    songlist.add(0,det)
                    initSongList(songlist)
                }
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

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
    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Music>) {
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        adapter =  SongDetAdapter(song, context,imaurl,names,nums)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : SongDetAdapter.ItemClickListener {
            override fun onItemClick(view:View,position: Int) {
                if(position>0){
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    intent.setClass(context, MusicPlayActivity().javaClass)
                    intent.putExtra("pos",position)
                    intent.putExtra("list",json)
                    startActivity(intent)
                }

            }
        })
    }

}
