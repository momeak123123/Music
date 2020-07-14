package com.example.xiaobai.music.music.presenter

import com.example.xiaobai.music.music.contract.CoverContract
import com.example.xiaobai.music.music.model.CoverModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class CoverPresenter : BaseMvpPresenter<CoverContract.IView, CoverContract.IModel>(), CoverContract.IPresenter{

    override fun registerModel() = CoverModel::class.java

}
