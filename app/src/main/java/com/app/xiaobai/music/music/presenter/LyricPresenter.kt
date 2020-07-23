package com.app.xiaobai.music.music.presenter

import com.app.xiaobai.music.music.contract.LyricContract
import com.app.xiaobai.music.music.model.LyricModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LyricPresenter : BaseMvpPresenter<LyricContract.IView, LyricContract.IModel>(), LyricContract.IPresenter{

    override fun registerModel() = LyricModel::class.java

}
