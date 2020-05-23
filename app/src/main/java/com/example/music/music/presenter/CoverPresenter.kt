package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.CoverContract
import com.example.music.music.model.CoverModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class CoverPresenter : BaseMvpPresenter<CoverContract.IView, CoverContract.IModel>(), CoverContract.IPresenter{

    override fun registerModel() = CoverModel::class.java

}
