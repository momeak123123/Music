package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/16
 * @Description input description
 **/
interface SongEditContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun registerdata(context: Context, name: String, playid: Long)
    }

    interface IModel : IModelContract{
        fun registerdata(context: Context, name: String, playid: Long)
    }

}
