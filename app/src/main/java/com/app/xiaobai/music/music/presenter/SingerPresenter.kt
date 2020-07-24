package com.app.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.app.xiaobai.music.music.contract.SingerContract
import com.app.xiaobai.music.music.model.SingerModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
class SingerPresenter : BaseMvpPresenter<SingerContract.IView, SingerContract.IModel>(), SingerContract.IPresenter{

    override fun registerModel() = SingerModel::class.java

}
