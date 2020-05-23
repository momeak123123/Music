package com.example.music.music.view.fragment

import android.os.Bundle
import com.example.music.R
import com.example.music.bean.Music
import com.example.music.music.contract.FindContract
import com.example.music.music.presenter.FindPresenter
import com.ywl5320.libmusic.WlMusic
import io.reactivex.Observer
import mvp.ljb.kt.fragment.BaseMvpFragment

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


}
