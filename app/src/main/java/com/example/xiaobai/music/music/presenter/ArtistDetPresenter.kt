package com.example.xiaobai.music.music.presenter

import android.content.Context
import com.example.xiaobai.music.music.contract.ArtistDetContract
import com.example.xiaobai.music.music.model.ArtistDetModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistDetPresenter : BaseMvpPresenter<ArtistDetContract.IView, ArtistDetContract.IModel>(), ArtistDetContract.IPresenter{

    override fun registerModel() = ArtistDetModel::class.java
    override fun listdata(context: Context, id: Long, type: Int) {
        getModel().listdata(context,id,type)
    }

}
