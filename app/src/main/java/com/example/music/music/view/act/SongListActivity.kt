package com.example.music.music.view.act

import com.example.music.R
import com.example.music.music.contract.SongListContract
import com.example.music.music.presenter.SongListPresenter
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongListActivity : BaseMvpActivity<SongListContract.IPresenter>() , SongListContract.IView {

    override fun registerPresenter() = SongListPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.song_index
    }

}
