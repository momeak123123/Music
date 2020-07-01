package com.example.xiaobai.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.SetContract
import com.example.xiaobai.music.music.model.SetModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/01
 * @Description input description
 **/
class SetPresenter : BaseMvpPresenter<SetContract.IView, SetContract.IModel>(), SetContract.IPresenter{

    override fun registerModel() = SetModel::class.java

}
