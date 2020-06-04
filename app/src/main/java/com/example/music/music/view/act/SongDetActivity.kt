package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.adapter.SongDetAdapter
import com.example.music.bean.Music
import com.example.music.config.CornerTransform
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.SongListContract
import com.example.music.music.presenter.SongListPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.song_index.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongDetActivity : BaseMvpActivity<SongListContract.IPresenter>() , SongListContract.IView {

    private lateinit var imaurl: String
    private lateinit var adapter: SongDetAdapter
    private lateinit var context: Context

    override fun registerPresenter() = SongListPresenter::class.java

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
        top_title.text = bundle?.get("name") as String
        imaurl = bundle.get("url") as String

        val transformation = CornerTransform(context, dip2px(context, 30))
        transformation.setExceptCorner(true, true, false, false)

        Glide.with(context)
            .load(imaurl)
            .skipMemoryCache(true)
            .placeholder(R.color.main_black_grey)
            .transform(transformation)
            .into(iv_cover)

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



        /*val sp: SharedPreferences = getSharedPreferences("Music", Context.MODE_PRIVATE)

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
        }*/

    }

    fun dip2px(context: Context, dpValue: Int): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Music>) {
        recyc_item.layoutManager = LinearLayoutManager(context)
        recyc_item.itemAnimator = DefaultItemAnimator()
        adapter =  SongDetAdapter(song, context,imaurl)
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

}
