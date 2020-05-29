package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.SearchAdapter
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.SearchContract
import com.example.music.music.presenter.SearchPresenter
import com.google.gson.Gson
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.music_album.*
import kotlinx.android.synthetic.main.search.*
import mvp.ljb.kt.act.BaseMvpActivity
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
class SearchActivity : BaseMvpActivity<SearchContract.IPresenter>() , SearchContract.IView {

    private lateinit var adapter: SearchAdapter
    private  var sreachtxt: String = ""
    var Datas = mutableListOf<String>()

    override fun registerPresenter() = SearchPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.search
    }


    override fun initData() {
        super.initData()
        Datas.clear()
        Datas.addAll(getPresenter().listdata())
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
       /* if(Datas.size==0){
            sreachtitle.visibility= View.GONE
            recyclerView3.visibility= View.GONE
            del.visibility= View.GONE
            deltxt.visibility= View.GONE
        }else{
            sreachtitle.visibility= View.VISIBLE
            recyclerView3.visibility= View.VISIBLE
            del.visibility= View.VISIBLE
            deltxt.visibility= View.VISIBLE
        }*/
        initSearch()
        initSearchList()
        RxView.clicks(flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }
    }

    /**
     * 初始化搜索
     */
    private fun initSearch() {

        search_view.clearFocus()
        search_view.isIconifiedByDefault = false
        search_view.isFocusable = false
        search_view.queryHint = "搜索歌曲名称"

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(queryText: String): Boolean {
                sreachtxt = queryText
                return true
            }

            override fun onQueryTextSubmit(queryText: String): Boolean {
                //点击搜索
                adapter.add(queryText)
                return true
            }
        })
    }

    /**
     * 初始化历史记录
     */
    private fun initSearchList() {
        recyclerView3.layoutManager = LinearLayoutManager(this)
        recyclerView3.itemAnimator = DefaultItemAnimator()

        adapter = SearchAdapter(Datas,this)
        recyclerView3.adapter = adapter
        recyclerView3.addOnItemTouchListener(
            ItemClickListener(this,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val del: ImageView = view!!.findViewById(R.id.del)
                        val txt: TextView = view.findViewById(R.id.txt)
                        del.setOnClickListener {

                        }
                        txt.setOnClickListener {

                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }
}
