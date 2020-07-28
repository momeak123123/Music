package com.app.xiaobai.music

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.xiaobai.music.music.view.custom.ColorFlipPagerTitleView
import com.app.xiaobai.music.music.view.fragment.SongFragment
import com.google.gson.JsonArray
import com.jakewharton.rxbinding2.view.RxView
import com.next.easynavigation.adapter.ViewPagerAdapter
import io.reactivex.Observer
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
        lateinit var search: String
        var type: Int = 0
    }

    private val mDataList = mutableListOf<String>()
    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_index)

        context = this
        top_title.text = getText(R.string.search)
        val bundle = intent.extras
        search = bundle?.get("txt") as String
        type = (bundle.get("sear") as Int)

        initView()
        initData()

    }

    fun initData(){

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

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }



    }

}
