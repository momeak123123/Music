package com.example.music.xiaobai.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.ArtistDetContract
import com.example.music.xiaobai.music.model.ArtistDetModel

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
