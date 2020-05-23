package com.example.music.music.presenter

import com.example.music.bean.HomeList
import com.example.music.bean.HomeSinger
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.HomeContract
import com.example.music.music.model.HomeModel
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

    override fun getdata1(): MutableList<HomeList> {
       return getModel().getdata1()
    }

    override fun getdata2(): MutableList<HomeList> {
        return getModel().getdata2()
    }

    override fun getdata3(): MutableList<HomeSinger> {
        return getModel().getdata3()
    }

    override fun getdata4(): MutableList<HomeList> {
        return getModel().getdata4()
    }


}
