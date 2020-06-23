package com.example.xiaobai.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.TopList

class HomeListAdapter(val datas: List<TopList>, val context: Context) : RecyclerView.Adapter<HomeListAdapter.InnerHolder>() {

    private var mItemClickListener: ItemClickListener? = null


    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.home_item1, holder, false)
        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_cover: ImageView = itemView.findViewById(R.id.iv_cover)
        var title: TextView = itemView.findViewById(R.id.title)
        var txt: TextView = itemView.findViewById(R.id.txt)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            mItemClickListener?.onItemClick(v,position)
        }
        Glide.with(context).load(datas[position].pic_url).placeholder(R.color.main_black_grey).into(holder.iv_cover)
        holder.title.text = datas[position].name
        holder.txt.text = datas[position].info
    }
    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }
}