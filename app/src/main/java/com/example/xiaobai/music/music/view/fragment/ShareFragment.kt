package com.example.xiaobai.music.music.view.fragment

import android.os.Bundle
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.ShareContract
import com.example.xiaobai.music.music.presenter.SharePresenter
import com.example.xiaobai.music.utils.QRCodeCreator
import kotlinx.android.synthetic.main.fragment_code.*
import mvp.ljb.kt.fragment.BaseMvpFragment

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class ShareFragment : BaseMvpFragment<ShareContract.IPresenter>(), ShareContract.IView {

    override fun registerPresenter() = SharePresenter::class.java

    override fun getLayoutId(): Int {
       return R.layout.fragment_code
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    override fun initData() {
        super.initData()
    }

    override fun initView() {
        super.initView()
        codeima.setImageBitmap(QRCodeCreator.createQRCode("http://img-ppx.oss-cn-hongkong.aliyuncs.com/version/20200714/app-debug.apk",400,400,null))
    }
}
