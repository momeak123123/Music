package com.example.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.music.R
import com.example.music.bean.HomeList

class HomeSongAdapter (val datas: List<HomeList>, val context: Context) : RecyclerView.Adapter<HomeSongAdapter.InnerHolder>() {

    private var itemClickListener: IKotlinItemClickListener? = null
    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.home_item3, holder, false)
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
        var iv_cover: ImageView = itemView.findViewById(R.id.iv_cover)
        var title: TextView = itemView.findViewById(R.id.title)
        var txt: TextView = itemView.findViewById(R.id.txt)
        var more: ImageView = itemView.findViewById(R.id.more)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        Glide.with(context).load(datas[position].imageUrl).placeholder(R.drawable.gplugin_load).into(holder.iv_cover)
        holder.title.text = datas[position].title
        holder.txt.text = datas[position].txt

        holder.more.setOnClickListener {

        }
    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
    }


    fun add(item: HomeList) {
        datas.toMutableList().add(item)
        notifyItemInserted(datas.size)
    }

    fun remove(position: Int) {
        datas.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }
}