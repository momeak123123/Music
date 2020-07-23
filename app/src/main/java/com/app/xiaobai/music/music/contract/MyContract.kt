package com.app.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface MyContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun data(context: Context)
        fun addSongList(context:Context,et_name: String)
        fun listdata(context: Context)
    }

    interface IModel : IModelContract{
        fun data(context: Context)
        fun addSongList(context:Context,et_name: String)
        fun listdata(context: Context)
    }
}
