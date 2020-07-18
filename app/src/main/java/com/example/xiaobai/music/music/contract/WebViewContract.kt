package com.example.xiaobai.music.music.contract

import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/18
 * @Description input description
 **/
interface WebViewContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract

    interface IModel : IModelContract
}
