package com.example.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface StartPageContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract {
        fun homedata(context: Context)
        fun ads()
    }

    interface IModel : IModelContract {
        fun homedata(context: Context)
        fun ads()
    }
}
