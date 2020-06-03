package com.example.music.music.contract

import com.example.music.sql.bean.Search
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
interface SearchContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun listdata(): MutableList<Search>
    }

    interface IModel : IModelContract{
        fun listdata(): MutableList<Search>
    }
}
