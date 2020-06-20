package com.example.music.xiaobai.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.SongEditContract
import com.example.music.xiaobai.music.model.SongEditModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/06/16
 * @Description input description
 **/
class SongEditPresenter : BaseMvpPresenter<SongEditContract.IView, SongEditContract.IModel>(), SongEditContract.IPresenter{

    override fun registerModel() = SongEditModel::class.java
    override fun registerdata(context: Context, name: String, playid: Long) {
        getModel().registerdata(context,name,playid)
    }

}
