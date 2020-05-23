package com.example.music.music.presenter

import com.example.music.bean.Music
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.PlayControlContract
import com.example.music.music.model.MusicListModel
import com.example.music.music.model.PlayControlModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class PlayControlPresenter : BaseMvpPresenter<PlayControlContract.IView, PlayControlContract.IModel>(), PlayControlContract.IPresenter{

    override fun registerModel() = PlayControlModel::class.java
    override fun getPlayList(): MutableList<Music> {
       return getModel().getPlayList()
    }

}
