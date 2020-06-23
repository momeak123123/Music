package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
interface AlbumDetContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun songdata(id:Long,type:Int,context: Context)
        fun songdatas(id:Long,type:Int, time: Long ,context: Context)

    }

    interface IModel : IModelContract{
        fun songdata(id:Long,type:Int,context: Context)
        fun songdatas(id:Long,type:Int, time: Long ,context: Context)
    }
}
