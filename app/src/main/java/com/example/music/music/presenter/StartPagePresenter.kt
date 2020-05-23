package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.StartPageContract
import com.example.music.music.model.StartPageModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPagePresenter : BaseMvpPresenter<StartPageContract.IView, StartPageContract.IModel>(), StartPageContract.IPresenter{

    override fun registerModel() = StartPageModel::class.java

}
