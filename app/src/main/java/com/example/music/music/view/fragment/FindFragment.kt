package com.example.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.R
import com.example.music.adapter.*
import com.example.music.bean.*
import com.example.music.config.ItemClickListener
import com.example.music.music.contract.FindContract
import com.example.music.music.presenter.FindPresenter
import com.example.music.music.view.act.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.fragment_find.*
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

    override fun initData() {
        super.initData()

        val sp: SharedPreferences =
            requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE)
        val data_song = mutableListOf<Music>()

        if (!sp.getString("song", "").equals("")) {
            val song: List<Music> = Gson().fromJson(
                sp.getString("song", ""),
                object : TypeToken<List<Music>>() {}.type
            )
            if (song.isNotEmpty()) {
                if (song.size > 8) {
                    for (i in 0..7) {
                        data_song.add(song[i])
                    }
                } else {
                    for (i in song) {
                        data_song.add(i)
                    }
                }
                initSongList(data_song)
            }
        }

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(add)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_add.visibility = View.VISIBLE
            }

        RxView.clicks(in_deter)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                if(et_name.isNotEmpty){
                    context?.let { it1 -> getPresenter().addSongList(it1,et_name.text.toString()) }
                    in_add.visibility = View.GONE
                }else{
                    Toast.makeText(context, R.string.song_error_name, Toast.LENGTH_SHORT).show()
                }


            }

        RxView.clicks(in_cancel)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                in_add.visibility = View.GONE
            }

    }


    override fun onResume() {
        super.onResume()
    }

    /**
     * 初始化歌曲
     */
    private fun initSongList(song: MutableList<Music>) {
        song_list.layoutManager = LinearLayoutManager(context)
        song_list.itemAnimator = DefaultItemAnimator()
        val adapter = context?.let { SongListAdapter(song, it) }
        song_list.adapter = adapter
        song_list.addOnItemTouchListener(
            ItemClickListener(context,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        /*val intent = Intent()
                        context?.let { intent.setClass(it, SongDetActivity().javaClass) }
                        intent.putExtra("id",song[position].song_id)
                        intent.putExtra("name",song[position].)
                        intent.putExtra("url",song[position].album["album_picurl"])
                        startActivity(intent)*/
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }
}
