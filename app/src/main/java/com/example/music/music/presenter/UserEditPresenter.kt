package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.UserEditContract
import com.example.music.music.model.UserEditModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditPresenter : BaseMvpPresenter<UserEditContract.IView, UserEditContract.IModel>(), UserEditContract.IPresenter{

    override fun registerModel() = UserEditModel::class.java

}
