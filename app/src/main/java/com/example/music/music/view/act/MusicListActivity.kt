package com.example.music.music.view.act

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.MusicListAdapter
import com.example.music.bean.Music
import com.example.music.config.SwipeItemLayout
import com.example.music.music.contract.MusicListContract
import com.example.music.music.presenter.MusicListPresenter
import kotlinx.android.synthetic.main.music_list.*
import mvp.ljb.kt.act.BaseMvpActivity


/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
class MusicListActivity : BaseMvpActivity<MusicListContract.IPresenter>() , MusicListContract.IView {
    private lateinit var context: Context
    var Datas = mutableListOf<Music>()
    override fun registerPresenter() = MusicListPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.music_list
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context=this
    }

    override fun initData() {
        super.initData()
        Datas.clear()
       Datas.addAll(getPresenter().listdata())
    }

    override fun initView() {
        super.initView()
        recycleview()
    }

    fun recycleview(){

        musiclist.layoutManager = LinearLayoutManager(this)
        musiclist.itemAnimator = DefaultItemAnimator()

        val adapter = MusicListAdapter(Datas,this)
        musiclist.addOnItemTouchListener(SwipeItemLayout.OnSwipeItemTouchListener(context))
        musiclist.adapter = adapter
        adapter.setOnKotlinItemClickListener(object : MusicListAdapter.IKotlinItemClickListener {
            override fun onItemClickListener(position: Int) {
                getPresenter().onclick(Datas,position)
            }
        })
        musiclist.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
