package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.SearchListContract
import com.example.xiaobai.music.music.model.SearchListModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/24
 * @Description input description
 **/
class SearchListPresenter : BaseMvpPresenter<SearchListContract.IView, SearchListContract.IModel>(), SearchListContract.IPresenter{

    override fun registerModel() = SearchListModel::class.java
    override fun qqdata(context: Context, search: String) {
        getModel().qqdata(context,search)
    }

}
