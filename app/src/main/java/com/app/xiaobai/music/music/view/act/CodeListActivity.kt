package com.app.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.xiaobai.music.R
import com.app.xiaobai.music.adapter.CodeListAdapter
import com.app.xiaobai.music.bean.Code
import com.app.xiaobai.music.music.contract.CodeListContract
import com.app.xiaobai.music.music.presenter.CodeListPresenter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.code_index.*
import kotlinx.android.synthetic.main.head.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class CodeListActivity : BaseMvpActivity<CodeListContract.IPresenter>(), CodeListContract.IView {

    private lateinit var context: Context

    companion object {
        lateinit var observer: Observer<JsonArray>
    }

    override fun registerPresenter() = CodeListPresenter::class.java
    override fun getLayoutId(): Int {
        return R.layout.code_index
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        top_title.text = getText(R.string.code_list)
        context = this
    }

    override fun initData() {
        super.initData()
        getPresenter().codelist(context)
        swipe_refresh_layout.isRefreshing = true
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        swipe_refresh_layout.setColorSchemeColors(-0xff6634, -0xbbbc, -0x996700, -0x559934, -0x7800)
        //下拉刷新
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getPresenter().codelist(context)
        })

        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }
    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<JsonArray> {
            override fun onSubscribe(d: Disposable) {}

            @SuppressLint("SetTextI18n")
            override fun onNext(codelist: JsonArray) {
                if (codelist.size() > 0) {
                    val list = Gson().fromJson<Array<Code>>(
                        codelist,
                        Array<Code>::class.java
                    ).toMutableList()

                    initSearchList(list)
                } else {
                    Toast.makeText(
                        context,
                        getText(R.string.code_lists),
                        Toast.LENGTH_LONG
                    ).show()
                }

                if (swipe_refresh_layout != null) {
                    swipe_refresh_layout.isRefreshing = false
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    /**
     * 初始化
     */
    private fun initSearchList(datas: MutableList<Code>) {
        recyc_item.layoutManager = LinearLayoutManager(this)
        recyc_item.itemAnimator = DefaultItemAnimator()
        val adapter = CodeListAdapter(datas, this)
        recyc_item.adapter = adapter
        adapter.setOnItemClickListener(object : CodeListAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {

            }
        })
    }

}
