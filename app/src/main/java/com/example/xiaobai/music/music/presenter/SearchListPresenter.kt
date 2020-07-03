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
    override fun qqdata(context: Context, search: String,limi:Int) {
        getModel().qqdata(context,search,limi)
    }

    override fun kugoudata(context: Context, search: String, limi: Int) {
        getModel().kugoudata(context,search,limi)
    }

    override fun baidudata(context: Context, search: String) {
        getModel().baidudata(context,search)
    }

    override fun wangyidata(context: Context, search: String) {
        getModel().wangyidata(context,search)
    }

    override fun kuwodata(context: Context, search: String) {
        getModel().kuwodata(context,search)
    }
    
}
