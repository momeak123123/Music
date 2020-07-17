package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.ChangePassContract
import com.example.xiaobai.music.music.model.ChangePassModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class ChangePassPresenter : BaseMvpPresenter<ChangePassContract.IView, ChangePassContract.IModel>(), ChangePassContract.IPresenter{

    override fun registerModel() = ChangePassModel::class.java
    override fun data(context: Context, pass: String, passs: String) {
        getModel().data(context,pass,passs)
    }

}
