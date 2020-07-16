package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
interface RegisteredContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun registerdata(context: Context, email:String, pass:String,code:String): Boolean
        fun registercode(context: Context, email: String)
    }

    interface IModel : IModelContract {
        fun registerdata(context: Context,email:String,pass:String,code:String): Boolean
        fun registercode(context: Context, email: String)
    }
}
