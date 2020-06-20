package com.example.music.xiaobai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.music.xiaobai.R
import com.example.music.xiaobai.bean.ApkModel
import com.example.music.xiaobai.config.LogDownloadListener
import com.jakewharton.rxbinding2.view.RxView
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import java.io.File
import java.util.concurrent.TimeUnit

class DownloadListAdapter (val datas: List<ApkModel>, val context: Context) : RecyclerView.Adapter<DownloadListAdapter.InnerHolder>() {

    private var mItemClickListener: ItemClickListener? = null


    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.download_list_item, holder, false)
        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var priority: TextView = itemView.findViewById(R.id.priority)
        var name: TextView = itemView.findViewById(R.id.name)
        var download: Button = itemView.findViewById(R.id.download)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            mItemClickListener?.onItemClick(v,position)
        }

        if (OkDownload.getInstance().getTask(datas[position].uri) != null) {
            holder.download.text = "已在队列"
            holder.download.isEnabled = false
        } else {
            holder.download.text = "下载"
            holder.download.isEnabled = true
        }
        holder.priority.text = String.format("优先级：%s", datas[position].priority)
        holder.name.text = datas[position].name
        RxView.clicks( holder.download)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {

                //这里只是演示，表示请求可以传参，怎么传都行，和okgo使用方法一样
                val request = OkGo.get<File>(datas[position].uri)

                //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
                OkDownload.request(datas[position].uri, request) //
                    .priority(datas[position].priority) //
                    .extra1(datas[position]) //
                    .save() //
                    .register(LogDownloadListener()) //
                    .start()
                notifyDataSetChanged()
            }
    }
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }
}