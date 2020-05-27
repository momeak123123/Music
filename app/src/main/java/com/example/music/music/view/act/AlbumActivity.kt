package com.example.music.music.view.act

import com.example.music.music.contract.AlbumContract
import com.example.music.music.presenter.AlbumPresenter
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumActivity : BaseMvpActivity<AlbumContract.IPresenter>() , AlbumContract.IView {

    override fun registerPresenter() = AlbumPresenter::class.java

    override fun getLayoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
