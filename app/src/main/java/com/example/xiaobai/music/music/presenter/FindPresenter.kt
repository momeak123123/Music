package com.example.xiaobai.music.music.presenter

import com.example.xiaobai.music.music.contract.FindContract
import com.example.xiaobai.music.music.model.FindModel
import com.example.xiaobai.music.sql.bean.Search
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindPresenter : BaseMvpPresenter<FindContract.IView, FindContract.IModel>(), FindContract.IPresenter{

    override fun registerModel() = FindModel::class.java
    override fun listdata(): MutableList<Search> {
        return getModel().listdata()
    }

    override fun listcean() {
         getModel().listcean()
    }

    override fun search(queryText: String) {
        getModel().search(queryText)
    }

}
