package com.example.xiaobai.music.music.presenter

import com.example.xiaobai.music.music.contract.CodeContract
import com.example.xiaobai.music.music.model.CodeModel
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class CodePresenter : BaseMvpPresenter<CodeContract.IView, CodeContract.IModel>(), CodeContract.IPresenter{

    override fun registerModel() = CodeModel::class.java

}
