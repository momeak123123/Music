package com.app.xiaobai.music.music.contract

import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
interface AlbumContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract

    interface IModel : IModelContract
}
