package com.example.xiaobai.music.music.presenter

import android.content.Context
import com.example.xiaobai.music.music.contract.RegisteredContract
import com.example.xiaobai.music.music.model.RegisteredModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredPresenter : BaseMvpPresenter<RegisteredContract.IView, RegisteredContract.IModel>(), RegisteredContract.IPresenter{

    override fun registerModel() = RegisteredModel::class.java
    override fun registerdata(context: Context, email:String, pass:String): Boolean {
        return getModel().registerdata(context,email,pass)
    }

    override fun registercode(context: Context, email: String) {
        getModel().registercode(context,email)
    }


}
