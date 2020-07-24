package com.app.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.app.xiaobai.music.music.contract.SongContract
import com.app.xiaobai.music.music.model.SongModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/24
 * @Description input description
 **/
class SongPresenter : BaseMvpPresenter<SongContract.IView, SongContract.IModel>(), SongContract.IPresenter{

    override fun registerModel() = SongModel::class.java

}
