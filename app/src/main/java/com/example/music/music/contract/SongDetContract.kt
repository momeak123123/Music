package com.example.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
interface SongDetContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun listdata(context: Context, id: Long)
    }

    interface IModel : IModelContract{
        fun listdata(context: Context,id: Long)
    }
}
