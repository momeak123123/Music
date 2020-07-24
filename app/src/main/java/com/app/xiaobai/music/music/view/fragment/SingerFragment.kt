package com.app.xiaobai.music.music.view.fragment

import com.app.xiaobai.music.R
import com.app.xiaobai.music.music.contract.SingerContract
import com.app.xiaobai.music.music.presenter.SingerPresenter
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
class SingerFragment : BaseMvpFragment<SingerContract.IPresenter>(), SingerContract.IView {

    override fun registerPresenter() = SingerPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.search_singer_index
    }
}
