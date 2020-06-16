package com.example.music.music.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.SongEditContract
import com.example.music.music.model.SongEditModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/16
 * @Description input description
 **/
class SongEditPresenter : BaseMvpPresenter<SongEditContract.IView, SongEditContract.IModel>(), SongEditContract.IPresenter{

    override fun registerModel() = SongEditModel::class.java

}
