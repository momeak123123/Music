package com.app.xiaobai.music.music.presenter

import android.content.Context
import com.app.xiaobai.music.music.contract.RegisteredContract
import com.app.xiaobai.music.music.model.RegisteredModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredPresenter : BaseMvpPresenter<RegisteredContract.IView, RegisteredContract.IModel>(), RegisteredContract.IPresenter{

    override fun registerModel() = RegisteredModel::class.java
    override fun registerdata(context: Context, email:String, pass:String,code:String): Boolean {
        return getModel().registerdata(context,email,pass,code)
    }

    override fun registercode(context: Context, email: String) {
        getModel().registercode(context,email)
    }


}
