package com.example.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.bean.Music

/**
 * Created by Sin on 2019/1/20
 */
class MusicListAdapter(val datas: List<Music>, val context: Context) : RecyclerView.Adapter<MusicListAdapter.InnerHolder>() {

    private var itemClickListener: IKotlinItemClickListener? = null
    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.music_item, holder, false)
        itemView.setOnClickListener {
            itemClickListener?.onItemClickListener(position)
        }
        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var musicsrc: ImageView = itemView.findViewById(R.id.musicsrc)
        var musicname: TextView = itemView.findViewById(R.id.musicname)
        var singer: TextView = itemView.findViewById(R.id.singer)
        var source: ImageView = itemView.findViewById(R.id.source)
        var more: ImageView = itemView.findViewById(R.id.more)
        var delete: Button = itemView.findViewById(R.id.delete)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        Glide.with(context).load(datas[position].coverUri).placeholder(R.drawable.gplugin_load).into(holder.musicsrc)
        holder.musicname.text = datas[position].title
        holder.singer.text = datas[position].artist
        Glide.with(context).load(R.drawable.wangyiyun).into(holder.source)
        holder.more.setOnClickListener {

        }

        holder.delete.setOnClickListener {
            
        }
    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
    }


    fun add(item: Music) {
        datas.toMutableList().add(item)
        notifyItemInserted(datas.size)
    }

    fun remove(position: Int) {
        datas.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }
}