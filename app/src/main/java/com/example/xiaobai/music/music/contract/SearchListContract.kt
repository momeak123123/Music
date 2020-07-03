package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
interface SearchListContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun qqdata(context: Context, search: String,limi:Int)
        fun kugoudata(context: Context, search: String,limi:Int)
        fun baidudata(context: Context, search: String)
        fun wangyidata(context: Context, search: String)
        fun kuwodata(context: Context, search: String)

    }

    interface IModel : IModelContract{
        fun qqdata(context: Context, search: String,limi:Int)
        fun kugoudata(context: Context, search: String,limi:Int)
        fun baidudata(context: Context, search: String)
        fun wangyidata(context: Context, search: String)
        fun kuwodata(context: Context, search: String)

    }

}
