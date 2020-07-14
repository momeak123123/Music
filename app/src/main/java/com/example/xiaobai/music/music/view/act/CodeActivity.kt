package com.example.xiaobai.music.music.view.act

import android.content.Context
import android.os.Bundle
import com.example.xiaobai.music.R
import com.example.xiaobai.music.music.contract.CodeContract
import com.example.xiaobai.music.music.presenter.CodePresenter
import com.example.xiaobai.music.utils.QRCodeCreator
import kotlinx.android.synthetic.main.fragment_code.*
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/14
 * @Description input description
 **/
class CodeActivity : BaseMvpActivity<CodeContract.IPresenter>() , CodeContract.IView {

    private lateinit var context: Context

    override fun registerPresenter() = CodePresenter::class.java

    override fun getLayoutId(): Int {
        return R.layout.fragment_code
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        context = this
    }


    override fun initView() {
        super.initView()
        codeima.setImageBitmap(QRCodeCreator.createQRCode("http://img-ppx.oss-cn-hongkong.aliyuncs.com/version/20200714/app-debug.apk",400,400,null))
    }

    override fun initData() {
        super.initData()
    }



    override fun onResume() {
        super.onResume()



    }
}
