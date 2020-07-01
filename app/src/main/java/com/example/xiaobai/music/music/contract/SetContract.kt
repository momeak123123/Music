package com.example.xiaobai.music.music.contract

import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/01
 * @Description input description
 **/
interface SetContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract

    interface IModel : IModelContract
}
