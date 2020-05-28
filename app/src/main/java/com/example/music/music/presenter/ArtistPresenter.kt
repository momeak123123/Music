package com.example.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.music.contract.ArtistContract
import com.example.music.music.model.ArtistModel
import com.google.gson.JsonObject

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistPresenter : BaseMvpPresenter<ArtistContract.IView, ArtistContract.IModel>(), ArtistContract.IPresenter{

    override fun registerModel() = ArtistModel::class.java
    override fun taglist(context: Context) {
       return getModel().taglist(context)
    }

    override fun listdata(context:Context,varieties: Int, letter: Int) {
        getModel().listdata(context,varieties,letter)
    }

}
