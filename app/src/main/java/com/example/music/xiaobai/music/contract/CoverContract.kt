package com.example.music.xiaobai.music.contract

import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
interface CoverContract {

    interface IView : IViewContract {
    }

    interface IPresenter : IPresenterContract

    interface IModel : IModelContract
}
