package com.example.music.xiaobai.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
interface LoginContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun logindata(context: Context,email:String,pass:String): Boolean
    }

    interface IModel : IModelContract {
        fun logindata(context: Context,email:String,pass:String): Boolean
    }
}
