package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.FindContract
import com.example.xiaobai.music.music.view.fragment.FindFragment
import com.example.xiaobai.music.music.view.fragment.MyFragment
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/18
 * @Description input description
 **/
class FindModel : BaseModel(), FindContract.IModel {

}