package com.app.xiaobai.music.music.view.fragment

import com.app.xiaobai.music.R
import com.app.xiaobai.music.music.contract.SAlbumContract
import com.app.xiaobai.music.music.presenter.SAlbumPresenter
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
class SAlbumFragment : BaseMvpFragment<SAlbumContract.IPresenter>(), SAlbumContract.IView {

    override fun registerPresenter() = SAlbumPresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.search_album_index
    }
}
