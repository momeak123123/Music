package com.example.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.music.R
import com.example.music.adapter.SongListAdapter
import com.example.music.bean.Music
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.FindContract
import com.example.music.music.presenter.FindPresenter
import com.example.music.music.view.act.SongDetActivity
import com.example.music.sql.bean.Playlist
import com.example.music.sql.config.Initialization
import com.example.music.sql.dao.mPlaylistDao
import com.example.music.sql.dao.mSearchDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog.SingleButtonCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_find.*
import kotlinx.android.synthetic.main.fragment_find.back
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.song_add.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindFragment : BaseMvpFragment<FindContract.IPresenter>(), FindContract.IView {

    companion object {

    }

    override fun registerPresenter() = FindPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_find
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        Initialization.setupDatabasePlaylist(context)
    }
    override fun initData() {
        super.initData()

        val list: MutableList<Playlist> =  mPlaylistDao.queryAll()
        if(list.size>0){
            back.visibility = View.GONE
            initSongList(list)
        }else{
            back.visibility = View.VISIBLE
        }


    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(add)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                //in_add.visibility = View.VISIBLE
                showInputDialog()
            }

        /*RxView.clicks(in_deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if(et_name.isNotEmpty){
                    context?.let { it1 -> getPresenter().addSongList(it1,names) }
                    in_add.visibility = View.GONE
                }else{
                    Toast.makeText(context, R.string.song_error_name, Toast.LENGTH_SHORT).show()
                }


            }

        RxView.clicks(in_cancel)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_add.visibility = View.GONE
            }*/

    }


    override fun onResume() {
        super.onResume()
    }

    /**
     * 带输入框的对话框
     */
    @SuppressLint("ResourceAsColor")
    private fun showInputDialog() {
        context?.let {
            MaterialDialog.Builder(it)
                .title(R.string.song_create)
                .inputType(
                    InputType.TYPE_CLASS_TEXT
                )
                .input(
                    getString(R.string.song_error_name),
                    "",
                    false,
                    MaterialDialog.InputCallback { _, input ->
                        context?.let { it1 -> getPresenter().addSongList(it1,input.toString()) }
                    }
                )
                .inputRange(1, 10)
                .positiveText(R.string.song_deter)
                .positiveColor(R.color.orange)
                .negativeText(R.string.song_cancel)
                .negativeColor(R.color.blue)
                .cancelable(false)
                .show()
        }
    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Playlist>) {
        song_list.layoutManager = LinearLayoutManager(context)
        song_list.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { SongListAdapter(song, it) }
        song_list.adapter = adapter
        song_list.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val intent = Intent()
                        context?.let { intent.setClass(it, SongDetActivity().javaClass) }
                        intent.putExtra("id",song[position].playid)
                        intent.putExtra("name",song[position].name)
                        intent.putExtra("url",song[position].pic_url)
                        startActivity(intent)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }
}
