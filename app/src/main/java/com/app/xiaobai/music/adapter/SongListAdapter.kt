package com.app.xiaobai.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.xiaobai.music.R
import com.app.xiaobai.music.sql.bean.Playlist

class SongListAdapter  (val datas: MutableList<Playlist>, val context: Context) : RecyclerView.Adapter<SongListAdapter.InnerHolder>() {

    private var mItemClickListener: ItemClickListener? = null
    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.song_list_item, holder, false)
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
        var right: ImageView = itemView.findViewById(R.id.right)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            mItemClickListener?.onItemClick(v,position)
        }
        Glide.with(context).load(datas[position].pic_url).placeholder(R.drawable.undetback).into(holder.iv_cover)
        holder.title.text = datas[position].name
        holder.txt.text =  datas[position].song_num+"首音乐"


    }

    fun add(item: Playlist) {
        datas.add(item)
        notifyItemChanged(datas.size)
    }

    fun remove(position: Int) {
        datas.removeAt(position)
        notifyItemRemoved(position)
        if (position != datas.size) {
            notifyItemRangeChanged(position, datas.size - position)
        }
    }

    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

}