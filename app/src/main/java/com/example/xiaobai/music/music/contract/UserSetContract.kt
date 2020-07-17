package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/07
 * @Description input description
 **/
interface UserSetContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract {
         fun code(context : Context, code: String)
        fun pass(context: Context, pass: String)
    }

    interface IModel : IModelContract{
        fun code(context :Context ,code: String)
        fun pass(context: Context, pass: String)
    }
}
