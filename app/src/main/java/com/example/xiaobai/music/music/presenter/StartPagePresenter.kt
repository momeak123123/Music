package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.StartPageContract
import com.example.xiaobai.music.music.model.StartPageModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class StartPagePresenter : BaseMvpPresenter<StartPageContract.IView, StartPageContract.IModel>(), StartPageContract.IPresenter{

    override fun registerModel() = StartPageModel::class.java
    override fun homedata(context: Context) {
        getModel().homedata(context)
    }

    override fun ads() {
        getModel().ads()
    }

}
