package com.example.music.music.contract

import android.content.Context
import com.example.music.bean.Banner

import com.example.music.bean.HomeList
import com.example.music.bean.HomeSinger
import com.google.gson.JsonObject
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface HomeContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract {
        fun imagesdata(): MutableList<BannerItem>


    }

    interface IModel : IModelContract {
        fun imagesdata(): MutableList<BannerItem>


    }
}
