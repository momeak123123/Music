package com.example.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.SongDetContract
import com.example.music.music.model.SongDetModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class SongDetPresenter : BaseMvpPresenter<SongDetContract.IView, SongDetContract.IModel>(), SongDetContract.IPresenter{

    override fun registerModel() = SongDetModel::class.java
    override fun listdata(context: Context, id: Long) {
        getModel().listdata(context,id)
    }

}
