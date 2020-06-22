package com.example.music.xiaobai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.xiaobai.R
import com.example.music.xiaobai.bean.Artists

class ArtistListAdapter (var datas:MutableList<Artists>, val context: Context) : RecyclerView.Adapter<ArtistListAdapter.InnerHolder>() {


    private var mItemClickListener: ItemClickListener? = null

    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.artist_list_item, holder, false)
        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size


    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_cover: ImageView = itemView.findViewById(R.id.iv_cover)
        var title: TextView = itemView.findViewById(R.id.title)

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
    }

    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

    fun removeAll(){
        datas.removeAll(datas)

    }

    fun addAll(data: MutableList<Artists>){
        datas.addAll(data)
    }

}