package com.example.music.xiaobai.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.LyricContract
import com.example.music.xiaobai.music.model.LyricModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LyricPresenter : BaseMvpPresenter<LyricContract.IView, LyricContract.IModel>(), LyricContract.IPresenter{

    override fun registerModel() = LyricModel::class.java

}
