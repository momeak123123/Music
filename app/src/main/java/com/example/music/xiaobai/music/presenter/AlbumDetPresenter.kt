package com.example.music.xiaobai.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.AlbumDetContract
import com.example.music.xiaobai.music.model.AlbumDetModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class AlbumDetPresenter : BaseMvpPresenter<AlbumDetContract.IView, AlbumDetContract.IModel>(), AlbumDetContract.IPresenter{

    override fun registerModel() = AlbumDetModel::class.java
    override fun songdata(id:Long,type:Int,context: Context) {
        getModel().songdata(id,type,context)
    }

    override fun songdatas(id:Long,type:Int, time: Long ,context: Context) {
        getModel().songdatas(id, type,time,context)
    }

}
