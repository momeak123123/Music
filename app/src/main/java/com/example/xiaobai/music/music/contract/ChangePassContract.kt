package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
interface ChangePassContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun data(context: Context, pass: String, passs: String)
    }

    interface IModel : IModelContract{
        fun data(context: Context, pass: String, passs: String)
    }
}
