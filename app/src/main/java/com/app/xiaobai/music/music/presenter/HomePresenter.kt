package com.app.xiaobai.music.music.presenter

import android.content.Context
import com.app.xiaobai.music.music.contract.HomeContract
import com.app.xiaobai.music.music.model.HomeModel
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import mvp.ljb.kt.presenter.BaseMvpPresenter

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
    override fun homedata(context: Context) {
        getModel().homedata(context)
    }



}
