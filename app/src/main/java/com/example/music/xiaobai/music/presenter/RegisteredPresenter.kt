package com.example.music.xiaobai.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.RegisteredContract
import com.example.music.xiaobai.music.model.RegisteredModel

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
