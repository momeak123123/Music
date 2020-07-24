package com.app.xiaobai.music.music.contract

import android.content.Context
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
interface SongContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {

    }

    interface IModel : IModelContract{


    }
}
