package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.AlbumDetAdapter
import com.example.music.bean.Song
import com.example.music.config.ItemClickListener
import com.example.music.config.LogDownloadListener
import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.presenter.AlbumDetPresenter
import com.example.music.utils.FileUtils
import com.example.music.utils.FileUtils.getMusicDir
import com.example.music.utils.FileUtils.isSDcardAvailable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import kotlinx.android.synthetic.main.album_index.*
import kotlinx.android.synthetic.main.head.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetActivity : BaseMvpActivity<AlbumDetContract.IPresenter>() , AlbumDetContract.IView {

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
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        val bundle = intent.extras
        top_title.text = bundle?.get("album_name") as String
        txt.text = bundle.get("artist_name") as String
        Glide.with(context).load(bundle.get("album_url") as String).placeholder(R.color.main_black_grey).into(iv_cover)
        Glide.with(context).load(R.drawable.more).placeholder(R.color.main_black_grey).into(top_set)

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(top_set)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if(adapter.type==0){
                    adapter.type=1
                    adapter.notifyDataSetChanged()
                }else{
                    adapter.type=0
                    adapter.notifyDataSetChanged()
                }


            }

       /* if(isSDcardAvailable()){
            val request =
                OkGo.get<File>(apk.getUrl())
            OkDownload.request(apk.getId().toString(), request)
                .save()
                .folder(getMusicDir())
                .register(LogDownloadListener(apk, context))
                .start()
        }*/



        val sp: SharedPreferences = getSharedPreferences("Music", Context.MODE_PRIVATE)

        val data_song = mutableListOf<Song>()

        if (!sp.getString("song", "").equals("")) {
            val song: List<Song> = Gson().fromJson(
                sp.getString("song", ""),
                object : TypeToken<List<Song>>() {}.type
            )
            if (song.isNotEmpty()) {
                if (song.size > 8) {
                    for (i in 0..7) {
                        data_song.add(song[i])
                    }
                } else {
                    for (i in song) {
                        data_song.add(i)
                    }
                }
                initSongList(data_song)
            }
        }

    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Song>) {
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
         adapter =  AlbumDetAdapter(song, context)
        recyc_item.adapter = adapter
        recyc_item.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                       /* val intent = Intent()
                        intent.setClass(context, MusicPlayActivity().javaClass)
                        intent.putExtra("id",position)
                        startActivity(intent)*/
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
