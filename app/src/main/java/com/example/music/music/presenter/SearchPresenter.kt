package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.SearchContract
import com.example.music.music.model.SearchModel
import com.example.music.sql.bean.Search

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/19
 * @Description input description
 **/
class SearchPresenter : BaseMvpPresenter<SearchContract.IView, SearchContract.IModel>(), SearchContract.IPresenter{

    override fun registerModel() = SearchModel::class.java
    override fun listdata(): MutableList<Search> {
       return getModel().listdata()
    }

}
