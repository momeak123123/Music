package com.example.music.music.presenter

import com.example.music.bean.Music
import com.example.music.music.contract.MusicListContract
import com.example.music.music.model.MusicListModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
class MusicListPresenter : BaseMvpPresenter<MusicListContract.IView, MusicListContract.IModel>(), MusicListContract.IPresenter{

    override fun registerModel() = MusicListModel::class.java
    override fun listdata(): MutableList<Music> {
       return getModel().listdata()
    }

    override fun onclick(datas: MutableList<Music>, position: Int) {

        println(position)
    }

}

