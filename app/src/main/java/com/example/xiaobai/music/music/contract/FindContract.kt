package com.example.xiaobai.music.music.contract

import android.content.Context
import com.example.xiaobai.music.sql.bean.Search
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface FindContract {

    interface IView : IViewContract

    interface IPresenter : IPresenterContract {
        fun listdata(): MutableList<Search>
        fun listcean(): MutableList<Search>
        fun search(queryText: String)
    }

    interface IModel : IModelContract{
        fun listdata(): MutableList<Search>
        fun listcean(): MutableList<Search>
        fun search(queryText: String)
    }

}
