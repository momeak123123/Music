package com.example.music.music.view.act

import com.example.music.music.contract.ArtistDetContract
import com.example.music.music.presenter.ArtistDetPresenter
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistDetActivity : BaseMvpActivity<ArtistDetContract.IPresenter>() , ArtistDetContract.IView {

    override fun registerPresenter() = ArtistDetPresenter::class.java

    override fun getLayoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
