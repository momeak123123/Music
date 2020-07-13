package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SearchListAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.Searchs
import com.example.xiaobai.music.config.Cookie
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.model.MusicPlayModel
import com.example.xiaobai.music.music.presenter.SearchListPresenter
import com.example.xiaobai.music.sql.config.Initialization
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.album_index.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.search_index.*
import kotlinx.android.synthetic.main.search_index.swipe_refresh_layout
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListActivity : BaseMvpActivity<SearchListContract.IPresenter>(),
    SearchListContract.IView {

    companion object {
        lateinit var observer: Observer<MutableList<Searchs>>
        lateinit var observers: Observer<Boolean>
    }

    val datas = mutableListOf<Searchs>()
    private lateinit var search: String
    private lateinit var context: Context
    private lateinit var adapter: SearchListAdapter
    private var type = 0
    override fun registerPresenter() = SearchListPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.search_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        Initialization.setupDatabaseSearch(this)
        context = this
        top_title.text = getText(R.string.search)
    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        search = bundle?.get("txt") as String
        getPresenter().qqdata(context, search, 0)
        swipe_refresh_layout.isRefreshing = true

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            swipe_refresh_layout.isRefreshing = false
        })

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }


    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<MutableList<Searchs>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: MutableList<Searchs>) {

                if (data.size > 0) {
                    for (it in data) {
                        datas.add(it)
                    }
                    initSearchList(datas)
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.error_search),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
               when (type) {
                    0 -> {
                        type++
                        getPresenter().kugoudata(context, search, 0)
                    }
                    1 -> {
                        if (swipe_refresh_layout != null) {
                            swipe_refresh_layout.isRefreshing = false
                        }
                    }


                }


            }

        }

        observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(data: Boolean) {
                if (data) {
                    if (swipe_refresh_layout != null) {
                        swipe_refresh_layout.isRefreshing = false
                    }
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }

    }


    /**
     * 初始化
     */
    private fun initSearchList(datas: MutableList<Searchs>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = SearchListAdapter(datas, this)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : SearchListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val music = mutableListOf<Music>()
                for (it in datas) {
                    music.add(it.music)
                }
                val json: String = Gson().toJson(music)
                val intent = Intent()
                intent.setClass(context, MusicPlayActivity().javaClass)
                intent.putExtra("album_id", 3L)
                intent.putExtra("pos", position)
                intent.putExtra("list", json)
                intent.putExtra("type", 3)
                startActivity(intent)

            }
        })
    }

}
