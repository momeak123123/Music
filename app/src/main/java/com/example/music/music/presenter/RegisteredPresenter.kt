package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.RegisteredContract
import com.example.music.music.model.RegisteredModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredPresenter : BaseMvpPresenter<RegisteredContract.IView, RegisteredContract.IModel>(), RegisteredContract.IPresenter{

    override fun registerModel() = RegisteredModel::class.java
    override fun registerdata(): Boolean {
        return getModel().registerdata()
    }


}
