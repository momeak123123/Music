package com.app.xiaobai.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.xiaobai.music.R
import com.app.xiaobai.music.bean.Hierarchy
import com.xuexiang.xui.utils.ResUtils.getResources
import com.xuexiang.xui.widget.textview.supertextview.SuperButton

class ArtistTagAdapter (val datas: List<Hierarchy>, val context: Context,val type:Int) : RecyclerView.Adapter<ArtistTagAdapter.InnerHolder>() {

    private var mItemClickListener: ItemClickListener? = null


    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): InnerHolder {
        //加载View
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.artist_item, holder, false)

        return InnerHolder(itemView)

    }

    /**
     * 得到总条数
     */
    override fun getItemCount(): Int = datas.size

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tag: SuperButton = itemView.findViewById(R.id.tag)
    }

    /**
     * 绑定数据，View和数据绑定
     */
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            mItemClickListener?.onItemClick(v,position)
        }
        holder.tag.text = datas[position].cat_name
        holder.tag.setOnClickListener {
            if(type==1){
                for (i in datas.indices) {
                    datas[i].cat_hierarchy = 1
                }
                datas[position].cat_hierarchy = 0
                notifyDataSetChanged()
            }else{
                for (i in datas.indices) {
                    datas[i].cat_hierarchy = 2
                }
                datas[position].cat_hierarchy = 0
                notifyDataSetChanged()
            }

        }

        if(datas[position].cat_hierarchy==0){
            holder.tag.setTextColor(getResources().getColor(R.color.lightBlue,null))
            holder.tag.setShapeStrokeColor(getResources().getColor(R.color.lightBlue,null))
                .setUseShape()
        }else{
            holder.tag.setTextColor(getResources().getColor(R.color.white,null))
            holder.tag.setShapeStrokeColor(getResources().getColor(R.color.gray,null))
                .setUseShape()
        }


    }


    interface ItemClickListener {
        fun onItemClick(view:View,position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }


}


