package com.example.music.xiaobai.music.contract

import com.example.music.xiaobai.bean.Music
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
interface PlayControlContract {

    interface IView : IViewContract {
    }

    interface IPresenter : IPresenterContract {
        fun getPlayList(): MutableList<Music>
    }

    interface IModel : IModelContract {
        fun getPlayList(): MutableList<Music>
    }
}
