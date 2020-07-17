package com.example.xiaobai.music.music.presenter

import android.content.Context
import com.example.xiaobai.music.music.contract.CodeListContract
import com.example.xiaobai.music.music.model.CodeListModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class CodeListPresenter : BaseMvpPresenter<CodeListContract.IView, CodeListContract.IModel>(), CodeListContract.IPresenter{

    override fun registerModel() = CodeListModel::class.java
    override fun codelist(context: Context) {
        getModel().codelist(context)
    }

}
