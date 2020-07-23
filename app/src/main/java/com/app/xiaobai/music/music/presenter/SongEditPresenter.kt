package com.app.xiaobai.music.music.presenter

import android.content.Context
import com.app.xiaobai.music.music.contract.SongEditContract
import com.app.xiaobai.music.music.model.SongEditModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

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
