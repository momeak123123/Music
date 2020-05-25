package com.example.music.config

import android.widget.Toast
import com.example.music.MusicApp
import com.example.music.R
import com.example.music.bean.ResultBean
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request

class Okgo {
    companion object {

        val url: String = "http://192.168.1.249:1017/"
        fun getokgo(inter: String,stringCallback: StringCallback?) {
            OkGo.get<String>(url + inter)
                .execute(stringCallback)
        }

        fun getokgo(inter: String, map: Map<String, String>,stringCallback: StringCallback?) {
            OkGo.get<String>(url + inter)
                .params(map)
                .execute(stringCallback)
        }

        fun postokgo(inter: String,stringCallback: StringCallback?) {
            OkGo.post<String>(url + inter)
                .execute(stringCallback)
        }

        fun postokgo(inter: String, map: Map<String, String>,stringCallback: StringCallback?) {
            OkGo.post<String>(url + inter)
                .params(map)
                .execute(stringCallback)
        }

    }

  /*  override fun onSuccess(response: Response<String>) {
        /**
         * 成功回调
         */

    }

    override fun onStart(request: Request<String?, out Request<*, *>?>?) {
        super.onStart(request)
        /**
         * 开始回调
         */
    }

    override fun onError(response: Response<String>) {
        super.onError(response)
        /**
         * 错误回调
         */
    }

    override fun onFinish() {
        super.onFinish()
        /**
         * 完成回调
         */
    }

    override fun uploadProgress(progress: Progress) {
        super.uploadProgress(progress)
        /**
         * 上传进度回调
         */
    }

    override fun downloadProgress(progress: Progress) {
        super.downloadProgress(progress)
        /**
         * 下载回调
         */
    }*/
}
