package com.example.xiaobai.music.music.contract

import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/07
 * @Description input description
 **/
interface UserSetContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract

    interface IModel : IModelContract
}
