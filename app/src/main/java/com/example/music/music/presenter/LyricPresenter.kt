package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.LyricContract
import com.example.music.music.model.LyricModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LyricPresenter : BaseMvpPresenter<LyricContract.IView, LyricContract.IModel>(), LyricContract.IPresenter{

    override fun registerModel() = LyricModel::class.java

}
