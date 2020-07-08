package com.example.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.UserSetContract
import com.example.xiaobai.music.music.model.UserSetModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/07
 * @Description input description
 **/
class UserSetPresenter : BaseMvpPresenter<UserSetContract.IView, UserSetContract.IModel>(), UserSetContract.IPresenter{

    override fun registerModel() = UserSetModel::class.java

}
