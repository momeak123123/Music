package com.example.music.music.view.act

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
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.adapter.PlaySongAdapter
import com.example.music.adapter.SearchAdapter
import com.example.music.music.contract.SearchContract
import com.example.music.music.model.MusicPlayModel
import com.example.music.music.presenter.SearchPresenter
import com.example.music.sql.bean.Search
import com.example.music.sql.config.Initialization
import com.example.music.sql.dao.mSearchDao
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.search.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
class SearchActivity : BaseMvpActivity<SearchContract.IPresenter>() , SearchContract.IView {

    private lateinit var context: Context
    private lateinit var adapter: SearchAdapter
    private  var sreachtxt: String = ""
    var Datas = mutableListOf<Search>()

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

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        Datas = getPresenter().listdata()

        if(Datas.size==0){
            sreachtitle.visibility= View.GONE
            recyclerView3.visibility= View.GONE
            del.visibility= View.GONE
            deltxt.visibility= View.GONE
        }else{
            sreachtitle.visibility= View.VISIBLE
            recyclerView3.visibility= View.VISIBLE
            del.visibility= View.VISIBLE
            deltxt.visibility= View.VISIBLE
        }

        initSearch()
        initSearchList(Datas)
        RxView.clicks(flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(deltxt)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                 adapter.removeAll()
               mSearchDao.deleteAll()
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
                if(MusicApp.getNetwork()){
                    val sea = Search()
                    sea.txt = queryText
                    sea.state = 0
                    mSearchDao.insert(sea)

                }else{
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

    /**
     * 初始化历史记录
     */
    private fun initSearchList(datas : MutableList<Search> ) {
        recyclerView3.layoutManager = LinearLayoutManager(this)
        recyclerView3.itemAnimator = DefaultItemAnimator()

        adapter = SearchAdapter(datas,this)
        recyclerView3.adapter = adapter
        adapter.setOnItemClickListener(object : SearchAdapter.ItemClickListener {
            override fun onItemClick(view:View,position: Int) {


            }
        })
    }
}
