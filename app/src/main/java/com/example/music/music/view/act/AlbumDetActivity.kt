package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.AlbumDetAdapter
import com.example.music.bean.Music
import com.example.music.bean.Song
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.presenter.AlbumDetPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.album_index.*
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

        lateinit var observer: Observer<JsonArray>
    }

    private lateinit var imaurl: String
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
        getPresenter().songdata(context)
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        val bundle = intent.extras
        top_title.text = bundle?.get("album_name") as String
        txt.text = bundle.get("artist_name") as String
        imaurl = bundle.get("album_url") as String
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


    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: List<Music>) {

        val linearLayoutManager: LinearLayoutManager =
            object : LinearLayoutManager(context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        recyc_item.setLayoutManager(linearLayoutManager)
        recyc_item.itemAnimator = DefaultItemAnimator()
         adapter =  AlbumDetAdapter(song, context,imaurl)
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
        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(data: JsonArray) {

                    val song: List<Music> = Gson().fromJson(
                        data,
                        object : TypeToken<List<Song>>() {}.type
                    )
                    if (song.isNotEmpty()) {
                        initSongList(song)

                    }
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
