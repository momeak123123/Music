package com.example.xiaobai.music.music.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.MyContract
import com.example.xiaobai.music.music.presenter.MyPresenter
import com.example.xiaobai.music.music.view.act.DownloadActivity
import com.example.xiaobai.music.music.view.act.LoginActivity
import com.example.xiaobai.music.music.view.act.MusicPlayActivity
import com.example.xiaobai.music.music.view.act.UserEditActivity
import com.example.xiaobai.music.sql.dao.mDownDao
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.iv_cover
import kotlinx.android.synthetic.main.fragment_unlogin.*
import mvp.ljb.kt.fragment.BaseMvpFragment
import java.util.concurrent.TimeUnit

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class MyFragment : BaseMvpFragment<MyContract.IPresenter>(), MyContract.IView {

    private var nums: Int = 0
    private lateinit var sp: SharedPreferences

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
        sp = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
        name.text = sp.getString("nickname", "")
        city.text = sp.getString("countries", "")
        attention_num.text = sp.getString("follow", "")
        collect_num.text = sp.getString("collect", "")

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        super.initView()


        RxView.clicks(item1)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }
        RxView.clicks(item2)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {

            }
        RxView.clicks(item3)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                if(nums>0){
                    val intent = Intent()
                    context?.let { it1 -> intent.setClass(it1, DownloadActivity().javaClass) }
                    startActivity(intent)
                }else{
                    Toast.makeText(
                        context,
                        getText(R.string.song_download_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        RxView.clicks(btn_up)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, UserEditActivity().javaClass) }
                startActivity(intent)
            }
        RxView.clicks(btn_register)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                sp.edit().putBoolean("login", false).apply()
                val intent = Intent()
                context?.let { intent.setClass(it, LoginActivity().javaClass) }
                startActivity(intent)
            }

        RxView.clicks(btn_uplogin)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe {
                val intent = Intent()
                context?.let { intent.setClass(it, LoginActivity().javaClass) }
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()

        nums = mDownDao.queryt(1).count()
        println(nums)
        like_num.text = nums.toString()

        if(sp.getBoolean("login",false)){
            include.visibility = View.GONE
            Glide.with(requireContext()).load(sp.getString("url", "")).placeholder(R.color.main_black_grey).into(iv_cover)
            name.text = sp.getString("nickname", "")
            city.text = sp.getString("countries", "")
            attention_num.text = sp.getString("follow", "")
            collect_num.text = sp.getString("collect", "")

        }else{
            include.visibility = View.VISIBLE
        }

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
                }

            }

            override fun onError(e: Throwable) {}
            override fun onComplete() {}

        }
    }

}
