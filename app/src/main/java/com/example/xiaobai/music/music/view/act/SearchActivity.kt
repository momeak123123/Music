package com.example.xiaobai.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.cy.cyflowlayoutlibrary.FlowLayoutAdapter
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.SearchContract
import com.example.xiaobai.music.music.presenter.SearchPresenter
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.config.Initialization
import com.example.xiaobai.music.sql.dao.mSearchDao
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.search.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
class SearchActivity : BaseMvpActivity<SearchContract.IPresenter>(), SearchContract.IView {

    private lateinit var context: Context

    private var sreachtxt: String = ""
    var Datas = mutableListOf<Search>()
    var lists = mutableListOf<String>()
    var Datat = mutableListOf<Search>()
    var listt = mutableListOf<String>()
    private var flowLayoutAdapter: FlowLayoutAdapter<String>? = null
    private var flowLayoutAdapters: FlowLayoutAdapter<String>? = null
    override fun registerPresenter() = SearchPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.search
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        Initialization.setupDatabaseSearch(this)
        context = this
    }

    override fun initData() {
        super.initData()
        initSearch()
        Datas = getPresenter().listdata()

        Datat = getPresenter().listcean()
        for(it in Datat){
            listt.add(it.txt)
        }

        flowLayoutAdapter = object : FlowLayoutAdapter<String>(listt) {
            override fun bindDataToView(
                holder: ViewHolder,
                position: Int,
                bean: String?
            ) {
                holder.setText(R.id.txt, bean)
            }

            override fun onItemClick(position: Int, bean: String?) {
                if (MusicApp.getNetwork()) {
                    val sea = Search()
                    sea.txt = bean
                    sea.state = 0
                    mSearchDao.insert(sea)
                    val intent = Intent()
                    intent.setClass(context as SearchActivity, SearchListActivity().javaClass)
                    intent.putExtra("txt", bean)
                    startActivity(intent)
                } else {
                    if (swipe_refresh_layout != null) {
                        swipe_refresh_layout.isRefreshing = false
                    }
                    Toast.makeText(
                        context,
                        getText(R.string.nonet),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun getItemLayoutID(position: Int, bean: String?): Int {
                return R.layout.search_item
            }
        }
        flowLayout.setAdapter(flowLayoutAdapter)

    if (Datas.size == 0) {
            sreachtitle.visibility = View.GONE
            flowLayout2.visibility = View.GONE
            del.visibility = View.GONE
        } else {
            sreachtitle.visibility = View.VISIBLE
            flowLayout2.visibility = View.VISIBLE
            del.visibility = View.VISIBLE
        }
        for(it in Datas){
            lists.add(it.txt)
        }
        flowLayoutAdapters = object : FlowLayoutAdapter<String>(lists) {
            override fun bindDataToView(
                holder: ViewHolder,
                position: Int,
                bean: String?
            ) {
                holder.setText(R.id.txt, bean)
            }

            override fun onItemClick(position: Int, bean: String?) {
                intent.setClass(context as SearchActivity, SearchListActivity().javaClass)
                intent.putExtra("txt", bean)
                startActivity(intent)
            }

            override fun getItemLayoutID(position: Int, bean: String?): Int {
                return R.layout.search_item
            }
        }
        flowLayout2.setAdapter(flowLayoutAdapters)
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()



        RxView.clicks(flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(del)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                Datas.clear()
                mSearchDao.deleteAll()
                sreachtitle.visibility = View.GONE
                flowLayout2.visibility = View.GONE
                del.visibility = View.GONE
            }
    }

    /**
     * 初始化搜索
     */
    private fun initSearch() {
        search_view.clearFocus()
        val id = search_view.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val textView = search_view.findViewById(id) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 16F// 设置输入字体大小
        textView.height = 40// 设置输入框的高度
        textView.gravity = Gravity.CENTER_VERTICAL


        val spanText = SpannableString("搜索音乐")

        // 设置字体大小
        spanText.setSpan(
            AbsoluteSizeSpan(16, true), 0, spanText.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        spanText.setSpan(
            ForegroundColorSpan(Color.GRAY), 0,
            spanText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        search_view.queryHint = spanText

        search_view.isIconifiedByDefault = false
        search_view.isFocusable = false

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(queryText: String): Boolean {
                sreachtxt = queryText
                return true
            }

            override fun onQueryTextSubmit(queryText: String): Boolean {
                //点击搜索
                if (MusicApp.getNetwork()) {
                    val sea = Search()
                    sea.txt = queryText
                    sea.state = 0
                    mSearchDao.insert(sea)
                    intent.setClass(context as SearchActivity, SearchListActivity().javaClass)
                    intent.putExtra("txt", queryText)
                    startActivity(intent)
                } else {
                    if (swipe_refresh_layout != null) {
                        swipe_refresh_layout.isRefreshing = false
                    }
                    Toast.makeText(
                        context,
                        getText(R.string.nonet),
                        Toast.LENGTH_LONG
                    ).show()
                }


                return true
            }
        })
    }

}
