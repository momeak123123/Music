package com.app.xiaobai.music

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.bean.Music
import com.app.xiaobai.music.bean.Searchs
import com.app.xiaobai.music.bean.artistlist
import com.app.xiaobai.music.music.model.SearchModel
import com.app.xiaobai.music.music.view.custom.ColorFlipPagerTitleView
import com.app.xiaobai.music.music.view.fragment.SAlbumFragment
import com.app.xiaobai.music.music.view.fragment.SingerFragment
import com.app.xiaobai.music.music.view.fragment.SongFragment
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.view.RxView
import com.next.easynavigation.adapter.ViewPagerAdapter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_index.*
import kotlinx.android.synthetic.main.head.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.util.concurrent.TimeUnit

class SearchIndexActivity : AppCompatActivity() {
    companion object {
        lateinit var observer: Observer<JsonArray>
        lateinit var observers: Observer<Boolean>
    }

    lateinit var search: String
    private var type: Int = 0
    private var add: Int = 1
    private val musicall = mutableListOf<Searchs>()
    private val mDataList = mutableListOf<String>()
    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_index)

        val bundle = intent.extras
        search = bundle?.get("txt") as String
        type = (bundle.get("sear") as Int)

        context = this
        top_title.text = getText(R.string.search)

        initView()
        initData()

    }

    fun initData(){
        when (type) {
            0 ->  SearchModel.qqdata(context, search, add)
            1 -> SearchModel.kugoudata(context, search, add)
        }
        swipe_refresh_layout.isRefreshing = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initView(){
        mDataList.add(resources.getText(R.string.songs).toString())
        val list: MutableList<Fragment> = ArrayList()
        list.add(SongFragment())
        mViewPager.adapter = ViewPagerAdapter(supportFragmentManager, list)

        val commonNavigator = CommonNavigator(context)
        magicIndicator.setBackgroundColor(resources.getColor(R.color.colorPrimary,null))
        commonNavigator.scrollPivotX = 0.65f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return mDataList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView = ColorFlipPagerTitleView(context)
                simplePagerTitleView.text = mDataList[index]
                simplePagerTitleView.normalColor = Color.parseColor("#9e9e9e")
                simplePagerTitleView.selectedColor = Color.parseColor("#ffffff")
                simplePagerTitleView.setOnClickListener { mViewPager.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = UIUtil.dip2px(
                    context,
                    6.0
                ).toFloat()
                indicator.lineWidth = UIUtil.dip2px(
                    context,
                    10.0
                ).toFloat()
                indicator.roundRadius = UIUtil.dip2px(
                    context,
                    3.0
                ).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(Color.parseColor("#ffffff"))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            when (type) {
                0 ->  SearchModel.qqdata(context, search, 1)
                1 ->  SearchModel.kugoudata(context, search, 1)
            }
        })

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }


        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(list: JsonArray) {
                add++
                when (type) {
                    0 -> {
                        for (i in 0 until list.size()) {

                            val music = list.get(i)
                            var jsonObj: JsonObject? = null
                            if (music.isJsonObject) {
                                jsonObj = music.asJsonObject
                            }
                            val midest = jsonObj!!.get("mid").asString
                            var mid = ""
                            if (midest != "") {
                                mid =
                                    "http://symusic.top/music.php?source=tencent&types=url&mid=$midest&br=hq"
                            }

                            val title = jsonObj.get("title").asString
                            val id = jsonObj.get("id").asLong
                            val album = jsonObj.get("album").asJsonObject
                            val album_id = album.get("id").asLong
                            val album_name = album.get("name").asString
                            val album_pmid =
                                "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + album.get(
                                    "pmid"
                                ).asString + ".jpg"
                            val one = mutableListOf<artistlist>()
                            val singer = jsonObj.get("singer").asJsonArray
                            for (e in 0 until singer.size()) {
                                val artist = singer.get(e)
                                var jsonOs: JsonObject? = null
                                if (artist.isJsonObject) {
                                    jsonOs = artist.asJsonObject
                                }
                                one.add(
                                    artistlist(
                                        jsonOs!!.get("id").asLong,
                                        jsonOs.get("name").asString
                                    )
                                )
                            }

                            musicall.add(
                                Searchs(
                                    1,
                                    Music(
                                        title,
                                        album_name,
                                        album_id,
                                        id,
                                        "",
                                        one,
                                        album_pmid,
                                        0,
                                        mid
                                    )
                                )

                            )
                        }
                        Observable.just(musicall).subscribe(SongFragment.observer)
                    }
                    1 -> {
                        for (i in 0 until list.size()) {

                            val music = list.get(i)
                            var jsonObj: JsonObject? = null
                            if (music.isJsonObject) {
                                jsonObj = music.asJsonObject
                            }
                            val midhq = jsonObj!!.get("HQFileHash").asString
                            val midsq = jsonObj.get("SQFileHash").asString
                            var mid = ""
                            if (midhq != "") {
                                mid =
                                    "http://symusic.top/music.php?source=kugou&types=url&mid=$midhq&br=hq"
                            } else {
                                if (midsq != "") {
                                    mid =
                                        "http://symusic.top/music.php?source=kugou&types=url&mid=$midsq&br=sq"
                                }
                            }
                            val title = jsonObj.get("SongName").asString
                            var ids = jsonObj.get("ID").asString
                            if (ids == "") {
                                ids = "0"
                            }
                            val id = ids.toLong()
                            var album = jsonObj.get("AlbumID").asString
                            if (album == "") {
                                album = "0"
                            }
                            val album_id = album.toLong()
                            val album_name = jsonObj.get("AlbumName").asString
                            val album_pmid =
                                "http://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                            val one = mutableListOf<artistlist>()
                            val singer = Gson().fromJson<Array<Long>>(
                                jsonObj.get("SingerId").asJsonArray,
                                Array<Long>::class.java
                            ).toMutableList()
                            val ca = jsonObj.get("SingerName").asString
                            if (ca != "") {
                                val ea = ca.substring(4)
                                val da = ea.substring(0, ea.lastIndexOf('<'))
                                one.add(artistlist(singer[0], da))
                            } else {
                                one.add(artistlist(singer[0], ""))
                            }

                            musicall.add(
                                Searchs(
                                    2, Music(
                                        title,
                                        album_name,
                                        album_id,
                                        id,
                                        "",
                                        one,
                                        album_pmid,
                                        0,
                                        mid
                                    )
                                )

                            )
                        }
                        Observable.just(musicall).subscribe(SongFragment.observer)

                    }

                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }
            }

        }

        observers =
            object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(data: Boolean) {
                    if (data) {
                        if (swipe_refresh_layout != null) {
                            swipe_refresh_layout.isRefreshing = false
                        }
                    }else{
                        when (type) {
                            0 ->  SearchModel.qqdata(context, search, add)
                            1 -> SearchModel.kugoudata(context, search, add)
                        }
                    }

                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {
                }

            }
    }

}
