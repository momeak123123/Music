package com.example.music.music.view.act

import com.example.music.music.contract.AlbumDetContract
import com.example.music.music.presenter.AlbumDetPresenter
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetActivity : BaseMvpActivity<AlbumDetContract.IPresenter>() , AlbumDetContract.IView {

    override fun registerPresenter() = AlbumDetPresenter::class.java

    override fun getLayoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
