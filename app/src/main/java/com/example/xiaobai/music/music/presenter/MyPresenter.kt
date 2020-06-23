package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.MyContract
import com.example.xiaobai.music.music.model.MyModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyPresenter : BaseMvpPresenter<MyContract.IView, MyContract.IModel>(), MyContract.IPresenter{

    override fun registerModel() = MyModel::class.java
    override fun data(context: Context) {
        getModel().data(context)
    }

}
