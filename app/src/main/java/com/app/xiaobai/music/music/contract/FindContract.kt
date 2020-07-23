package com.app.xiaobai.music.music.contract

import com.app.xiaobai.music.sql.bean.Search
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface FindContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract {
        fun listdata(): MutableList<Search>
        fun listcean()
        fun search(queryText: String)
    }

    interface IModel : IModelContract{
        fun listdata(): MutableList<Search>
        fun listcean()
        fun search(queryText: String)
    }

}
