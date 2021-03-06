package com.app.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
interface ArtistDetContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun listdata(context: Context, id: Long, type: Int)
    }

    interface IModel : IModelContract{
        fun listdata(context: Context, id: Long, type: Int)
    }

}
