package com.example.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.xiaobai.music.MainActivity
import com.example.xiaobai.music.MusicApp
import com.example.xiaobai.music.R
import com.example.xiaobai.music.adapter.SongListAdapter
import com.example.xiaobai.music.music.contract.FindContract
import com.example.xiaobai.music.music.model.MainModel.Companion.addsonglist
import com.example.xiaobai.music.music.presenter.FindPresenter
import com.example.xiaobai.music.music.view.act.LoginActivity
import com.example.xiaobai.music.music.view.act.SongDetActivity
import com.example.xiaobai.music.sql.bean.Playlist
import com.example.xiaobai.music.sql.dao.mPlaylistDao
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
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


    override fun registerPresenter() = FindPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_find
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }


}
