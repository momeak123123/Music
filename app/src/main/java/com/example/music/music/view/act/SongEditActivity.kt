package com.example.music.music.view.act

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.music.contract.SongEditContract
import com.example.music.music.presenter.SongEditPresenter
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.artist_index.*
import kotlinx.android.synthetic.main.head.*
import kotlinx.android.synthetic.main.user_edit.*
import mvp.ljb.kt.act.BaseMvpActivity
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/16
 * @Description input description
 **/
class SongEditActivity : BaseMvpActivity<SongEditContract.IPresenter>(), SongEditContract.IView {

    private var playid: Long = 0
    private lateinit var context: Context

    override fun registerPresenter() = SongEditPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.song_edit
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
        top_title.text = getText(R.string.song_edit)
    }


    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()
        RxView.clicks(top_flot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                finish()
            }

        RxView.clicks(btn_edit)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if (MusicApp.getNetwork()) {
                    if (name.text.toString() != "") {
                        getPresenter().registerdata(
                            context,
                            name.text.toString(),
                            playid
                        )
                    } else {
                        Toast.makeText(context, R.string.song_error_name, Toast.LENGTH_SHORT).show()
                    }
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
    }

    override fun initData() {
        super.initData()
        val bundle = intent.extras
        val urls = bundle?.get("url") as String
        val names = bundle.get("name") as String
        playid = bundle.get("playid") as Long
        Glide.with(context).load(urls).placeholder(R.color.main_black_grey).into(ima)
        name.text = Editable.Factory.getInstance().newEditable(names)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
