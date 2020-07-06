package com.example.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cy.cyflowlayoutlibrary.FlowLayoutAdapter
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SearchAdapter
import com.example.xiaobai.music.bean.Sear
import com.example.xiaobai.music.music.contract.FindContract
import com.example.xiaobai.music.music.presenter.FindPresenter
import com.example.xiaobai.music.music.view.act.*
import com.example.xiaobai.music.parsing.kugouseBean
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.config.Initialization
import com.example.xiaobai.music.sql.dao.mSearchDao
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.fragment_find.*
import kotlinx.android.synthetic.main.fragment_find.del
import kotlinx.android.synthetic.main.fragment_find.flowLayout
import kotlinx.android.synthetic.main.fragment_find.flowLayout2
import kotlinx.android.synthetic.main.fragment_find.search_view
import kotlinx.android.synthetic.main.fragment_find.sreachtitle
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindFragment : BaseMvpFragment<FindContract.IPresenter>(), FindContract.IView {

    companion object {
        lateinit var observer: Observer<MutableList<kugouseBean>>
        lateinit var observert: Observer<List<Sear>>
        lateinit var observers: Observer<Boolean>
    }

    private var sreachtxt: String = ""
    private var bools = true
    var lists = mutableListOf<String>()
    var listt = mutableListOf<String>()
    private var flowLayoutAdapter: FlowLayoutAdapter<String>? = null
    private var flowLayoutAdapters: FlowLayoutAdapter<String>? = null
    override fun registerPresenter() = FindPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_find
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        Initialization.setupDatabaseSearch(context)
    }

    override fun initData() {
        super.initData()
        initSearch()

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(del)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                mSearchDao.deleteAll()
                sreachtitle.visibility = View.GONE
                flowLayout2.visibility = View.GONE
                del.visibility = View.GONE
            }
    }

    override fun onResume() {
        super.onResume()

        if (MusicApp.network() != -1) {
            getPresenter().listcean()

            val datas = getPresenter().listdata()

            if (datas.size == 0) {
                sreachtitle.visibility = View.GONE
                flowLayout2.visibility = View.GONE
                del.visibility = View.GONE
            } else {
                sreachtitle.visibility = View.VISIBLE
                flowLayout2.visibility = View.VISIBLE
                del.visibility = View.VISIBLE
                lists.clear()
                for (it in datas) {
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

                        intent(bean)
                    }

                    override fun getItemLayoutID(position: Int, bean: String?): Int {
                        return R.layout.search_item
                    }
                }
                flowLayout2.setAdapter(flowLayoutAdapters)
            }

        } else {
            Toast.makeText(
                context,
                getText(R.string.error_connection),
                Toast.LENGTH_SHORT
            ).show()
        }

        observers = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(data: Boolean) {
                bools = data

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }

        observert = object : Observer<List<Sear>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(data: List<Sear>) {
                if (data.isNotEmpty()) {
                    listt.clear()
                    for (it in data) {
                        listt.add(it.name)
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
                            if (MusicApp.network() != -1) {
                                val sea = Search()
                                sea.txt = bean
                                sea.state = 0
                                mSearchDao.insert(sea)
                                intent(bean)
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
                }


            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {
            }

        }



        observer = object : Observer<MutableList<kugouseBean>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: MutableList<kugouseBean>) {

                if (data.size > 0) {
                    searlis.visibility = View.VISIBLE
                    initSongList(data)
                } else {
                    searlis.visibility = View.GONE
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

    fun intent(bean: String?) {
        if (bools) {
            val intent = Intent()
            context?.let { intent.setClass(it, SearchListActivity().javaClass) }
            intent.putExtra("txt", bean)
            startActivity(intent)
        } else {
            Toast.makeText(
                context,
                R.string.error_connection,
                Toast.LENGTH_SHORT
            ).show()
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
        textView.setTextColor(Color.BLACK)
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
                if (queryText != "") {
                    searlis.visibility = View.VISIBLE
                    getPresenter().search(queryText)
                } else {
                    searlis.visibility = View.GONE
                }

                return true
            }

            override fun onQueryTextSubmit(queryText: String): Boolean {
                //点击搜索
                if (MusicApp.network() != -1) {
                    val sea = Search()
                    sea.txt = queryText
                    sea.state = 0
                    mSearchDao.insert(sea)
                    lists.add(queryText)
                    intent(queryText)
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


    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<kugouseBean>) {
        searchlist.layoutManager = LinearLayoutManager(context)
        searchlist.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { SearchAdapter(song, it) }
        searchlist.adapter = adapter
        adapter?.setOnItemClickListener(object : SearchAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (MusicApp.network() != -1) {
                    val sea = Search()
                    sea.txt = song[position].keyword
                    sea.state = 0
                    mSearchDao.insert(sea)
                    lists.add(song[position].keyword)
                    intent(song[position].keyword)
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
        })

    }

}
