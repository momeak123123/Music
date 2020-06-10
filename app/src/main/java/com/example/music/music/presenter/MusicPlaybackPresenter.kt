package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.MusicPlaybackContract
import com.example.music.music.model.MusicPlaybackModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/15
 * @Description input description
 **/
class MusicPlaybackPresenter : BaseMvpPresenter<MusicPlaybackContract.IView, MusicPlaybackContract.IModel>(), MusicPlaybackContract.IPresenter{

    override fun registerModel() = MusicPlaybackModel::class.java

}
