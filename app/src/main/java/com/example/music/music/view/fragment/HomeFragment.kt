package com.example.music.music.view.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.MainActivity
import com.example.music.R
import com.example.music.adapter.HomeAlbumAdapter
import com.example.music.adapter.HomeListAdapter
import com.example.music.adapter.HomeSingerAdapter
import com.example.music.adapter.HomeSongAdapter
import com.example.music.bean.HomeList
import com.example.music.bean.HomeSinger
import com.example.music.music.contract.HomeContract
import com.example.music.music.presenter.HomePresenter
import com.example.music.music.view.act.MusicPlaybackActivity
import com.example.music.music.view.act.SearchActivity
import com.example.music.music.view.act.StartPageActivity
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner
import kotlinx.android.synthetic.main.fragment_home.*
import mvp.ljb.kt.fragment.BaseMvpFragment


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomeFragment : BaseMvpFragment<HomeContract.IPresenter>(), HomeContract.IView {

    companion object {

    }

    var data = mutableListOf<BannerItem>()
    var data1 = mutableListOf<HomeList>()
    var data2 = mutableListOf<HomeList>()
    var data3 = mutableListOf<HomeSinger>()
    var data4 = mutableListOf<HomeList>()

    override fun registerPresenter() = HomePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initData() {
        super.initData()
        data1.clear()
        data1.addAll(getPresenter().getdata1())

        data2.clear()
        data2.addAll(getPresenter().getdata2())

        data3.clear()
        data3.addAll(getPresenter().getdata3())

        data4.clear()
        data4.addAll(getPresenter().getdata4())
    }

    override fun initView() {
        super.initView()
        initbanner()
        initSongList()
        initAlbumList()
        initSingerList()
        initSongLists()
        search.setOnClickListener {
            val intent = Intent()
            context?.let { it1 -> intent.setClass(it1, SearchActivity().javaClass) }
            startActivity(intent)
        }
    }


    /**
     * 初始化轮播图
     */
    private fun initbanner() {
        //设置banner样式
        data.clear()
        data.addAll(getPresenter().imagesdata())
        banner.setSource(data)
            .setOnItemClickListener(BaseBanner.OnItemClickListener<BannerItem?> { view, t, position -> })
            .setIsOnePageLoop(false).startScroll()

        banner.setSource(data).startScroll()
    }

    /**
     * 初始化排行榜
     */
    private fun initSongList() {
        recyc_item1.layoutManager = GridLayoutManager(context, 3)
        recyc_item1.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeListAdapter(data1, it) }
        recyc_item1.adapter = adapter
        adapter?.setOnKotlinItemClickListener(object : HomeListAdapter.IKotlinItemClickListener {
            override fun onItemClickListener(position: Int) {
                val intent = Intent()
                context?.let { intent.setClass(it, MusicPlaybackActivity().javaClass) }
                val bundle = Bundle()
                bundle.putParcelable("musiclist", data1[position])
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
    }

    /**
     * 初始化专辑
     */
    private fun initAlbumList() {
        recyc_item2.layoutManager = GridLayoutManager(context, 3)
        recyc_item2.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeAlbumAdapter(data2, it) }
        recyc_item2.adapter = adapter
        adapter?.setOnKotlinItemClickListener(object : HomeAlbumAdapter.IKotlinItemClickListener {
            override fun onItemClickListener(position: Int) {

            }
        })
    }

    /**
     * 初始化歌手
     */
    private fun initSingerList() {
        recyc_item3.layoutManager = GridLayoutManager(context, 4)
        recyc_item3.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeSingerAdapter(data3, it) }
        recyc_item3.adapter = adapter
        adapter?.setOnKotlinItemClickListener(object : HomeSingerAdapter.IKotlinItemClickListener {
            override fun onItemClickListener(position: Int) {

            }
        })
    }

    /**
     * 初始化歌曲
     */
    private fun initSongLists() {
        recyc_item4.layoutManager = LinearLayoutManager(context)
        recyc_item4.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { HomeSongAdapter(data4, it) }
        recyc_item4.adapter = adapter
        adapter?.setOnKotlinItemClickListener(object : HomeSongAdapter.IKotlinItemClickListener {
            override fun onItemClickListener(position: Int) {

            }
        })
    }

}


