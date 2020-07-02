package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
interface UserEditContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun registerdata(context: Context, name: String, gender: Int, city: String, images: String,mess:String)
        fun osst(context: Context)

    }

    interface IModel : IModelContract{
        fun registerdata(context: Context, name: String, gender: Int, city: String, images: String,mess:String)
        fun osst(context: Context)
    }
}
