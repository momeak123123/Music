package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
interface CodeListContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun codelist(context: Context)
    }

    interface IModel : IModelContract{
        fun codelist(context: Context)
    }
}
