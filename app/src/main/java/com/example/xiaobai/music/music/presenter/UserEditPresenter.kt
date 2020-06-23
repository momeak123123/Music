package com.example.xiaobai.music.music.presenter

import android.content.Context
import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.example.xiaobai.music.music.contract.UserEditContract
import com.example.xiaobai.music.music.model.UserEditModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/30
 * @Description input description
 **/
class UserEditPresenter : BaseMvpPresenter<UserEditContract.IView, UserEditContract.IModel>(), UserEditContract.IPresenter{

    override fun registerModel() = UserEditModel::class.java

    override fun registerdata(context: Context, name: String, gender: Int, city: String, images: String) {
      getModel().registerdata(context,name,gender,city,images)
    }


}
