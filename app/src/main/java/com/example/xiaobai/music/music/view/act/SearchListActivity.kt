package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SearchAdapter
import com.example.xiaobai.music.adapter.SearchListAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.presenter.SearchListPresenter
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.config.Initialization
import com.example.xiaobai.music.sql.dao.mSearchDao
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.search.*
import kotlinx.android.synthetic.main.search_index.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListActivity : BaseMvpActivity<SearchListContract.IPresenter>() , SearchListContract.IView {

    private lateinit var context: Context
    private lateinit var adapter: SearchListAdapter
    override fun registerPresenter() = SearchListPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.search_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        Initialization.setupDatabaseSearch(this)
        context = this


    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        val search = bundle?.get("search") as String
        getPresenter().data(context,search)

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



    /**
     * 初始化
     */
    private fun initSearchList(datas : MutableList<Music> ) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = SearchListAdapter(datas,this)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : SearchListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {


            }
        })
    }

}
