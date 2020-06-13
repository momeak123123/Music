package com.example.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.music.contract.MyContract
import com.example.music.music.presenter.MyPresenter
import com.example.music.music.view.act.UserEditActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.head.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyFragment : BaseMvpFragment<MyContract.IPresenter>(), MyContract.IView {

    companion object {

        lateinit var observer: Observer<Boolean>

    }

    override fun registerPresenter() = MyPresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    override fun initData() {
        super.initData()
        getPresenter().data(requireContext())
        val sp: SharedPreferences =
            requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)

        Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
        name.text = sp.getString("nickname", "")
        city.text = sp.getString("city", "")
        attention_num.text = sp.getString("follow", "")
        collect_num.text = sp.getString("collect", "")
        like_num.text = sp.getString("like", "")
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()

        RxView.clicks(btn_up)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, UserEditActivity().javaClass) }
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()
        observer = object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(data: Boolean) {
                if(data){
                    val sp: SharedPreferences =
                        requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)

                    Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
                    name.text = sp.getString("nickname", "")
                    city.text = sp.getString("city", "")
                    attention_num.text = sp.getString("follow", "")
                    collect_num.text = sp.getString("collect", "")
                    like_num.text = sp.getString("like", "")
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

}
