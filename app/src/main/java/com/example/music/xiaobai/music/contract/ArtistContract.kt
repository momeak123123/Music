package com.example.music.xiaobai.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
interface ArtistContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun taglist(context: Context,bool: Boolean)
        fun listdata(context:Context,varieties: Int, letter: Int)

    }

    interface IModel : IModelContract {
        fun taglist(context:Context,bool: Boolean)
        fun listdata(context:Context,varieties: Int, letter: Int)
    }
}
