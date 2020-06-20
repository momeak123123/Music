package com.example.music.xiaobai.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface MyContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun data(context: Context)
    }

    interface IModel : IModelContract{
        fun data(context: Context)
    }
}
