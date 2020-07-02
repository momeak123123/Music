package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SearchListAdapter
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.presenter.SearchListPresenter
import com.example.xiaobai.music.sql.config.Initialization
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.search_index.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListActivity : BaseMvpActivity<SearchListContract.IPresenter>() , SearchListContract.IView {

    companion object {
        lateinit var observer: Observer<MutableList<Music>>
    }
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
        val search = bundle?.get("txt") as String
        println(search)
        getPresenter().qqdata(context,search)

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

    override fun onResume() {
        super.onResume()
        observer = object : Observer<MutableList<Music>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: MutableList<Music>) {
                initSearchList(data)
            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

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
