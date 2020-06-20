package com.example.music.xiaobai.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.music.xiaobai.music.contract.FindContract
import com.example.music.xiaobai.music.model.FindModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindPresenter : BaseMvpPresenter<FindContract.IView, FindContract.IModel>(), FindContract.IPresenter{

    override fun registerModel() = FindModel::class.java
    override fun addSongList(context: Context, et_name: String) {
        getModel().addSongList(context,et_name)
    }

    override fun listdata(context: Context) {
        getModel().listdata(context)
    }

}
