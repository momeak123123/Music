package  com.example.xiaobai.music.music.model

import android.content.Context
import android.content.SharedPreferences
import com.example.xiaobai.music.bean.Code
import com.example.xiaobai.music.bean.ResultBean
import com.example.xiaobai.music.common.Constants
import com.example.xiaobai.music.music.contract.CodeListContract
import com.example.xiaobai.music.music.view.act.CodeListActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/07/17
 * @Description input description
 **/
class CodeListModel : BaseModel(), CodeListContract.IModel {
    override fun codelist(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        OkGo.post<String>(Constants.URL + "api/user/get_invite_list")
            .params("token", sp.getString("token", ""))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    /**
                     * 成功回调
                     */
                    if(response.code()==200){
                        try {
                            val bean =
                                Gson().fromJson(response.body(), ResultBean::class.javaObjectType)
                            if (bean.code == 200) {

                                val list = bean.data.getAsJsonArray("user_list")
                                val codelist = Gson().fromJson<Array<Code>>(
                                    list,
                                    Array<Code>::class.java
                                ).toMutableList()
                                Observable.just(codelist).subscribe(CodeListActivity.observer)
                            }
                        } catch (e: Exception) {

                        }
                    }



                }
            })
    }
}