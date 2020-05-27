package com.example.music.music.view.act

import com.example.music.music.contract.ArtistContract
import com.example.music.music.presenter.ArtistPresenter
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistActivity : BaseMvpActivity<ArtistContract.IPresenter>() , ArtistContract.IView {

    override fun registerPresenter() = ArtistPresenter::class.java

    override fun getLayoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
