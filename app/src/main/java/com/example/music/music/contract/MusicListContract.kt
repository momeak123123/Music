package com.example.music.music.contract

import com.example.music.bean.Music
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
interface MusicListContract {

    interface IView : IViewContract {

    }

    interface IPresenter : IPresenterContract {
        fun listdata(): MutableList<Music>
        fun onclick(datas: MutableList<Music>, position: Int)


    }

    interface IModel : IModelContract {
        fun listdata(): MutableList<Music>
    }
}
