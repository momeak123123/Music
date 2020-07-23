package com.app.xiaobai.music.music.contract

import android.content.Context
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

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
