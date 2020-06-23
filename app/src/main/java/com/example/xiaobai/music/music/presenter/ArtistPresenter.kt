package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.ArtistContract
import com.example.xiaobai.music.music.model.ArtistModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/26
 * @Description input description
 **/
class ArtistPresenter : BaseMvpPresenter<ArtistContract.IView, ArtistContract.IModel>(), ArtistContract.IPresenter{

    override fun registerModel() = ArtistModel::class.java
    override fun taglist(context: Context,bool: Boolean) {
       return getModel().taglist(context,bool)
    }

    override fun listdata(context:Context,varieties: Int, letter: Int) {
        getModel().listdata(context,varieties,letter)
    }

}
