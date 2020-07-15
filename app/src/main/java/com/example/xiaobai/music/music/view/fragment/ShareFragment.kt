package com.example.xiaobai.music.music.view.fragment

import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.ShareContract
import com.example.xiaobai.music.music.presenter.SharePresenter
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class ShareFragment : BaseMvpFragment<ShareContract.IPresenter>(), ShareContract.IView {

    override fun registerPresenter() = SharePresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.fragment_code
    }
}
