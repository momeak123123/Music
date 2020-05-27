package com.example.music.music.presenter

import android.content.Context
import com.example.music.bean.HomeList
import com.example.music.bean.HomeSinger
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.HomeContract
import com.example.music.music.model.HomeModel
import com.google.gson.JsonObject
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class HomePresenter : BaseMvpPresenter<HomeContract.IView, HomeContract.IModel>(), HomeContract.IPresenter{

    override fun registerModel() = HomeModel::class.java
    override fun imagesdata(): MutableList<BannerItem> {
        return getModel().imagesdata()
    }

    override fun listdata(context: Context) {
        return getModel().listdata(context)
    }


}
