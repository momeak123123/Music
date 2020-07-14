package com.example.xiaobai.music.music.contract

import com.example.xiaobai.music.bean.Music
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract

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
