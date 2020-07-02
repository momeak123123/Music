package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.FindContract
import com.example.xiaobai.music.music.model.FindModel
import com.example.xiaobai.music.sql.bean.Search

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

    override fun listcean(): MutableList<Search> {
        return getModel().listcean()
    }

    override fun search(queryText: String) {
        getModel().search(queryText)
    }

}
