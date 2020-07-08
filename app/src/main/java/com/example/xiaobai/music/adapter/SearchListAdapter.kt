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
import com.example.xiaobai.music.bean.Music
import com.example.xiaobai.music.bean.Searchs
import com.example.xiaobai.music.music.view.fragment.HomeFragment
import com.example.xiaobai.music.sql.bean.Search
import com.example.xiaobai.music.sql.dao.mSearchDao
import io.reactivex.Observable

class SearchListAdapter(val datas: MutableList<Searchs>, val context: Context) : RecyclerView.Adapter<SearchListAdapter.InnerHolder>() {


    private var mItemClickListener: ItemClickListener? = null
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
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
            LayoutInflater.from(context).inflate(R.layout.search_index_item, holder, false)

        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var iv_cover: ImageView = itemView.findViewById(R.id.iv_cover)
        var type: ImageView = itemView.findViewById(R.id.type)
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
        Glide.with(context).load(datas[position].music.pic_url).placeholder(R.color.main_black_grey).into(holder.iv_cover)
        when(datas[position].type){
            0->
                holder.type.setImageResource(R.drawable.qqmusic)
            1->
                holder.type.setImageResource(R.drawable.kugou)
            2->
                holder.type.setImageResource(R.drawable.baidu)
            3->
                holder.type.setImageResource(R.drawable.wangyiyun)
            4->
                holder.type.setImageResource(R.drawable.kuwo)
        }
        holder.title.text = datas[position].music.name
        val artist =  datas[position].music.all_artist
        var srtist_name = ""
        for(it in artist){
            if(srtist_name != ""){
                srtist_name += "/"+it.name
            }else{
                srtist_name = it.name
            }

        }
        holder.txt.text = srtist_name
    }


    fun add(item: Searchs) {
        datas.add(item)
        notifyItemChanged(datas.size)
    }
}