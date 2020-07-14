package com.example.xiaobai.music.music.presenter

import android.content.Context
import com.example.xiaobai.music.music.contract.LoginContract
import com.example.xiaobai.music.music.model.LoginModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class LoginPresenter : BaseMvpPresenter<LoginContract.IView, LoginContract.IModel>(), LoginContract.IPresenter{

    override fun registerModel() = LoginModel::class.java
    override fun logindata(context: Context,email:String,pass:String): Boolean {
       return getModel().logindata(context,email,pass)
    }

}
