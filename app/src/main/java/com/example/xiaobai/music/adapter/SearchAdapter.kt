package com.example.xiaobai.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xiaobai.music.R
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.dao.mSearchDao

class SearchAdapter  (val datas: MutableList<Search>, val context: Context) : RecyclerView.Adapter<SearchAdapter.InnerHolder>() {


    private var mItemClickListener: ItemClickListener? = null
    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }

    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.search_item, holder, false)

        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt: TextView = itemView.findViewById(R.id.txt)
        var del: ImageView = itemView.findViewById(R.id.del)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            mItemClickListener?.onItemClick(v,position)
        }
        holder.txt.text = datas[position].txt

        holder.del.setOnClickListener {
            remove(position)
            mSearchDao.delete(datas[position].id)
        }
        holder.txt.setOnClickListener {

        }
    }


    fun add(item: Search) {
        datas.add(item)
        notifyItemInserted(datas.size)
    }

    fun remove(position: Int) {
        datas.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAll() {
        datas.removeAll(datas)
        notifyDataSetChanged()
    }
}