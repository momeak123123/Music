package com.example.music.music.view.fragment

import com.example.music.R
import com.example.music.music.contract.LyricContract
import com.example.music.music.presenter.LyricPresenter
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LyricFragment : BaseMvpFragment<LyricContract.IPresenter>(), LyricContract.IView {

    override fun registerPresenter() = LyricPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.frag_player_lrcview
    }
}
