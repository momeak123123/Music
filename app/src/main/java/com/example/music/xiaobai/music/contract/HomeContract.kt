package com.example.music.xiaobai.music.contract

import android.content.Context
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
        fun homedata(context: Context)

    }

    interface IModel : IModelContract {
        fun imagesdata(): MutableList<BannerItem>
        fun homedata(context: Context)

    }
}
