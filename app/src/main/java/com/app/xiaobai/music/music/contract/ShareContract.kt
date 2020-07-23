package com.app.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
interface ShareContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract {
        fun usercode(context: Context)
    }

    interface IModel : IModelContract{
        fun usercode(context: Context)
    }
}
