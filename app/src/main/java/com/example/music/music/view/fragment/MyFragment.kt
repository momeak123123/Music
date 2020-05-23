package com.example.music.music.view.fragment

import android.os.Bundle
import com.example.music.R
import com.example.music.music.contract.MyContract
import com.example.music.music.presenter.MyPresenter
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyFragment : BaseMvpFragment<MyContract.IPresenter>(), MyContract.IView {

    companion object {

    }

    override fun registerPresenter() = MyPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }


}
