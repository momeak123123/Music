package com.example.xiaobai.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaobai.music.IndexActivity
import com.example.xiaobai.music.R
import com.example.xiaobai.music.bean.*
import com.example.xiaobai.music.music.view.act.*
import com.example.xiaobai.music.music.view.fragment.HomeFragment
import com.google.gson.Gson
import com.jakewharton.rxbinding2.view.RxView
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class HomeDetAdapter(
    val context: Context,
    val ads: List<Banner>,
    val list: List<TopList>,
    val album: List<Album>,
    val artist: List<Artists>,
    val song: List<Music>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        const val TYPE_SEARCH = 0
        const val TYPE_TITILE1 = 1
        const val TYPE_TIME1 = 2
        const val TYPE_TITILE2 = 3
        const val TYPE_TIME2 = 4
        const val TYPE_TITILE3 = 5
        const val TYPE_TIME3 = 6
        const val TYPE_TITILE4 = 7
        const val TYPE_TIME4 = 8
    }

    var type = 0
    private lateinit var bannerdata: MutableList<BannerItem>
    private var mItemClickListener: ItemClickListener? = null


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                TYPE_SEARCH
            }
            1 -> {
                TYPE_TITILE1
            }
            2 -> {
                TYPE_TIME1
            }
            3 -> {
                TYPE_TITILE2
            }
            4 -> {
                TYPE_TIME2
            }
            5 -> {
                TYPE_TITILE3
            }
            6 -> {
                TYPE_TIME3
            }
            7 -> {
                TYPE_TITILE4
            }
            8 -> {
                TYPE_TIME4
            }
            else -> {
                TYPE_SEARCH
            }
        }
    }

    override fun getItemCount(): Int {
        return 9
    }


    /**
     * 相当于getView()
     */
    override fun onCreateViewHolder(holder: ViewGroup, position: Int): RecyclerView.ViewHolder {


        //加载View
        return when (position) {
            TYPE_SEARCH -> SearchHolder(
                LayoutInflater.from(context).inflate(R.layout.home_search, holder, false)
            )
            TYPE_TITILE1 -> Title1Holder(
                LayoutInflater.from(context).inflate(R.layout.home_title, holder, false)
            )
            TYPE_TIME1 -> Time1Holder(
                LayoutInflater.from(context).inflate(R.layout.home_item1, holder, false)
            )
            TYPE_TITILE2 -> Title2Holder(
                LayoutInflater.from(context).inflate(R.layout.home_title, holder, false)
            )
            TYPE_TIME2 -> Time2Holder(
                LayoutInflater.from(context).inflate(R.layout.home_item1, holder, false)
            )
            TYPE_TITILE3 -> Title3Holder(
                LayoutInflater.from(context).inflate(R.layout.home_title, holder, false)
            )
            TYPE_TIME3 -> Time3Holder(
                LayoutInflater.from(context).inflate(R.layout.home_item2, holder, false)
            )
            TYPE_TITILE4 -> Title4Holder(
                LayoutInflater.from(context).inflate(R.layout.home_title, holder, false)
            )
            TYPE_TIME4 -> Time4Holder(
                LayoutInflater.from(context).inflate(R.layout.home_item3, holder, false)
            )
            else -> SearchHolder(
                LayoutInflater.from(context).inflate(R.layout.home_search, holder, false)
            )
        }

    }


    /**
     * 绑定数据，View和数据绑定
     */

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_SEARCH -> (holder as SearchHolder).bindData()
            TYPE_TITILE1 -> (holder as Title1Holder).bindData()
            TYPE_TIME1 -> (holder as Time1Holder).bindData(position)
            TYPE_TITILE2 -> (holder as Title2Holder).bindData()
            TYPE_TIME2 -> (holder as Time2Holder).bindData(position)
            TYPE_TITILE3 -> (holder as Title3Holder).bindData()
            TYPE_TIME3 -> (holder as Time3Holder).bindData(position)
            TYPE_TITILE4 -> (holder as Title4Holder).bindData()
            TYPE_TIME4 -> (holder as Time4Holder).bindData(position)

        }

    }

    inner class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var banner: SimpleImageBanner = itemView.findViewById(R.id.banner)

        @SuppressLint("ResourceAsColor", "CheckResult")
        fun bindData() {

            if (ads.isNotEmpty()) {

                bannerdata = mutableListOf<BannerItem>()

                for (it in ads) {
                    val item1 = BannerItem()
                    item1.imgUrl = it.img
                    item1.title = ""
                    bannerdata.add(item1)
                }

                banner.setSource(bannerdata)
                    .setOnItemClickListener(BaseBanner.OnItemClickListener<BannerItem?> { _, _, position ->
                        println(
                            position
                        )
                    })
                    .setIsOnePageLoop(false).startScroll()

            }



        }
    }


    inner class Title1Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView
        var more: TextView

        init {
            title = itemView.findViewById(R.id.title)
            more = itemView.findViewById(R.id.more)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            title.text = context.getText(R.string.item1)

            RxView.clicks(more)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe {
                    val intent = Intent()
                    context.let { intent.setClass(it, AlbumActivity().javaClass) }
                    intent.putExtra("album_type", 0)
                    context.startActivity(intent)
                }

        }
    }

    inner class Time1Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idea: RelativeLayout
        var iv_cover: ImageView
        var title: TextView
        var txt: TextView
        var idea1: RelativeLayout
        var iv_cover1: ImageView
        var title1: TextView
        var txt1: TextView
        var idea2: RelativeLayout
        var iv_cover2: ImageView
        var title2: TextView
        var txt2: TextView
        var idea3: RelativeLayout
        var iv_cover3: ImageView
        var title3: TextView
        var txt3: TextView
        var idea4: RelativeLayout
        var iv_cover4: ImageView
        var title4: TextView
        var txt4: TextView
        var idea5: RelativeLayout
        var iv_cover5: ImageView
        var title5: TextView
        var txt5: TextView


        init {
            idea = itemView.findViewById(R.id.idea)
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
            idea1 = itemView.findViewById(R.id.idea1)
            iv_cover1 = itemView.findViewById(R.id.iv_cover1)
            title1 = itemView.findViewById(R.id.title1)
            txt1 = itemView.findViewById(R.id.txt1)
            idea2 = itemView.findViewById(R.id.idea2)
            iv_cover2 = itemView.findViewById(R.id.iv_cover2)
            title2 = itemView.findViewById(R.id.title2)
            txt2 = itemView.findViewById(R.id.txt2)
            idea3 = itemView.findViewById(R.id.idea3)
            iv_cover3 = itemView.findViewById(R.id.iv_cover3)
            title3= itemView.findViewById(R.id.title3)
            txt3 = itemView.findViewById(R.id.txt3)
            idea4 = itemView.findViewById(R.id.idea4)
            iv_cover4 = itemView.findViewById(R.id.iv_cover4)
            title4 = itemView.findViewById(R.id.title4)
            txt4 = itemView.findViewById(R.id.txt4)
            idea5 = itemView.findViewById(R.id.idea5)
            iv_cover5 = itemView.findViewById(R.id.iv_cover5)
            title5 = itemView.findViewById(R.id.title5)
            txt5 = itemView.findViewById(R.id.txt5)
        }

        @SuppressLint("CheckResult")
        fun bindData(position: Int) {

            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v, position)
            }


            if (list.isNotEmpty()) {

                Glide.with(context).load(list[0].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover)
                title.text = list[0].name
                txt.text = list[0].info

                Glide.with(context).load(list[1].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover1)
                title1.text = list[1].name
                txt1.text = list[1].info

                Glide.with(context).load(list[2].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover2)
                title2.text = list[2].name
                txt2.text = list[2].info

                Glide.with(context).load(list[3].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover3)
                title3.text = list[3].name
                txt3.text = list[3].info

                Glide.with(context).load(list[4].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover4)
                title4.text = list[4].name
                txt4.text = list[4].info

                Glide.with(context).load(list[5].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover5)
                title5.text = list[5].name
                txt5.text = list[5].info

                RxView.clicks(idea)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", list[0].from_id)
                        intent.putExtra("album_type", list[0].from)
                        intent.putExtra("album_time", list[0].update_time)
                        intent.putExtra("palylist_name", list[0].name)
                        intent.putExtra("info", list[0].info)
                        intent.putExtra("cover", list[0].pic_url)
                        intent.putExtra("type", 1)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea1)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", list[1].from_id)
                        intent.putExtra("album_type", list[1].from)
                        intent.putExtra("album_time", list[1].update_time)
                        intent.putExtra("palylist_name", list[1].name)
                        intent.putExtra("info", list[1].info)
                        intent.putExtra("cover", list[1].pic_url)
                        intent.putExtra("type", 1)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea2)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", list[2].from_id)
                        intent.putExtra("album_type", list[2].from)
                        intent.putExtra("album_time", list[2].update_time)
                        intent.putExtra("palylist_name", list[2].name)
                        intent.putExtra("info", list[2].info)
                        intent.putExtra("cover", list[2].pic_url)
                        intent.putExtra("type", 1)
                        context.startActivity(intent)
                    }


                RxView.clicks(idea3)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", list[3].from_id)
                        intent.putExtra("album_type", list[3].from)
                        intent.putExtra("album_time", list[3].update_time)
                        intent.putExtra("palylist_name", list[3].name)
                        intent.putExtra("info", list[3].info)
                        intent.putExtra("cover", list[3].pic_url)
                        intent.putExtra("type", 1)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", list[4].from_id)
                        intent.putExtra("album_type", list[4].from)
                        intent.putExtra("album_time", list[4].update_time)
                        intent.putExtra("palylist_name", list[4].name)
                        intent.putExtra("info", list[4].info)
                        intent.putExtra("cover", list[4].pic_url)
                        intent.putExtra("type", 1)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea5)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", list[5].from_id)
                        intent.putExtra("album_type", list[5].from)
                        intent.putExtra("album_time", list[5].update_time)
                        intent.putExtra("palylist_name", list[5].name)
                        intent.putExtra("info", list[5].info)
                        intent.putExtra("cover", list[5].pic_url)
                        intent.putExtra("type", 1)
                        context.startActivity(intent)
                    }

            }


        }
    }

    inner class Title2Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var more: TextView
        var title: TextView

        init {
            title = itemView.findViewById(R.id.title)
            more = itemView.findViewById(R.id.more)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            title.text = context.getText(R.string.item2)
            RxView.clicks(more)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe {
                    val intent = Intent()
                    context.let { intent.setClass(it, AlbumActivity().javaClass) }
                    intent.putExtra("album_type", 1)
                    context.startActivity(intent)
                }

        }
    }

    inner class Time2Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idea: RelativeLayout
        var iv_cover: ImageView
        var title: TextView
        var txt: TextView
        var idea1: RelativeLayout
        var iv_cover1: ImageView
        var title1: TextView
        var txt1: TextView
        var idea2: RelativeLayout
        var iv_cover2: ImageView
        var title2: TextView
        var txt2: TextView
        var idea3: RelativeLayout
        var iv_cover3: ImageView
        var title3: TextView
        var txt3: TextView
        var idea4: RelativeLayout
        var iv_cover4: ImageView
        var title4: TextView
        var txt4: TextView
        var idea5: RelativeLayout
        var iv_cover5: ImageView
        var title5: TextView
        var txt5: TextView


        init {
            idea = itemView.findViewById(R.id.idea)
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
            idea1 = itemView.findViewById(R.id.idea1)
            iv_cover1 = itemView.findViewById(R.id.iv_cover1)
            title1 = itemView.findViewById(R.id.title1)
            txt1 = itemView.findViewById(R.id.txt1)
            idea2 = itemView.findViewById(R.id.idea2)
            iv_cover2 = itemView.findViewById(R.id.iv_cover2)
            title2 = itemView.findViewById(R.id.title2)
            txt2 = itemView.findViewById(R.id.txt2)
            idea3 = itemView.findViewById(R.id.idea3)
            iv_cover3 = itemView.findViewById(R.id.iv_cover3)
            title3= itemView.findViewById(R.id.title3)
            txt3 = itemView.findViewById(R.id.txt3)
            idea4 = itemView.findViewById(R.id.idea4)
            iv_cover4 = itemView.findViewById(R.id.iv_cover4)
            title4 = itemView.findViewById(R.id.title4)
            txt4 = itemView.findViewById(R.id.txt4)
            idea5 = itemView.findViewById(R.id.idea5)
            iv_cover5 = itemView.findViewById(R.id.iv_cover5)
            title5 = itemView.findViewById(R.id.title5)
            txt5 = itemView.findViewById(R.id.txt5)
        }

        @SuppressLint("CheckResult")
        fun bindData(position: Int) {
            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v, position)
            }



            if (album.isNotEmpty()) {

                Glide.with(context).load(album[0].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover)
                title.text = album[0].name
                txt.text = album[0].info

                Glide.with(context).load(album[1].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover1)
                title1.text = album[1].name
                txt1.text = album[1].info

                Glide.with(context).load(album[2].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover2)
                title2.text = album[2].name
                txt2.text = album[2].info

                Glide.with(context).load(album[3].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover3)
                title3.text = album[3].name
                txt3.text = album[3].info

                Glide.with(context).load(album[4].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover4)
                title4.text = album[4].name
                txt4.text = album[4].info

                Glide.with(context).load(album[5].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover5)
                title5.text = album[5].name
                txt5.text = album[5].info

                RxView.clicks(idea)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", album[0].album_id)
                        intent.putExtra("album_type", album[0].type)
                        intent.putExtra("album_time", 0L)
                        intent.putExtra("palylist_name", album[0].name)
                        intent.putExtra("info", album[0].info)
                        intent.putExtra("cover", album[0].pic_url)
                        intent.putExtra("type", 2)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea1)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", album[1].album_id)
                        intent.putExtra("album_type", album[1].type)
                        intent.putExtra("album_time", 0L)
                        intent.putExtra("palylist_name", album[1].name)
                        intent.putExtra("info", album[1].info)
                        intent.putExtra("cover", album[1].pic_url)
                        intent.putExtra("type", 2)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea2)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", album[2].album_id)
                        intent.putExtra("album_type", album[2].type)
                        intent.putExtra("album_time", 0L)
                        intent.putExtra("palylist_name", album[2].name)
                        intent.putExtra("info", album[2].info)
                        intent.putExtra("cover", album[2].pic_url)
                        intent.putExtra("type", 2)
                        context.startActivity(intent)
                    }


                RxView.clicks(idea3)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", album[3].album_id)
                        intent.putExtra("album_type", album[3].type)
                        intent.putExtra("album_time", 0L)
                        intent.putExtra("palylist_name", album[3].name)
                        intent.putExtra("info", album[3].info)
                        intent.putExtra("cover", album[3].pic_url)
                        intent.putExtra("type", 2)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", album[4].album_id)
                        intent.putExtra("album_type", album[4].type)
                        intent.putExtra("album_time", 0L)
                        intent.putExtra("palylist_name", album[4].name)
                        intent.putExtra("info", album[4].info)
                        intent.putExtra("cover", album[4].pic_url)
                        intent.putExtra("type", 2)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea5)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, AlbumDetActivity().javaClass) }
                        intent.putExtra("album_id", album[5].album_id)
                        intent.putExtra("album_type", album[5].type)
                        intent.putExtra("album_time", 0L)
                        intent.putExtra("palylist_name", album[5].name)
                        intent.putExtra("info", album[5].info)
                        intent.putExtra("cover", album[5].pic_url)
                        intent.putExtra("type", 2)
                        context.startActivity(intent)
                    }
            }


        }
    }

    inner class Title3Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var more: TextView
        var title: TextView

        init {
            title = itemView.findViewById(R.id.title)
            more = itemView.findViewById(R.id.more)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            title.text = context.getText(R.string.item3)
            RxView.clicks(more)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe {
                    val intent = Intent()
                    context.let { intent.setClass(it, ArtistActivity().javaClass) }
                    context.startActivity(intent)
                }

        }
    }

    inner class Time3Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idea: RelativeLayout
        var iv_cover: ImageView
        var title: TextView

        var idea1: RelativeLayout
        var iv_cover1: ImageView
        var title1: TextView

        var idea2: RelativeLayout
        var iv_cover2: ImageView
        var title2: TextView

        var idea3: RelativeLayout
        var iv_cover3: ImageView
        var title3: TextView

        var idea4: RelativeLayout
        var iv_cover4: ImageView
        var title4: TextView

        var idea5: RelativeLayout
        var iv_cover5: ImageView
        var title5: TextView

        var idea6: RelativeLayout
        var iv_cover6: ImageView
        var title6: TextView

        var idea7: RelativeLayout
        var iv_cover7: ImageView
        var title7: TextView


        init {
            idea = itemView.findViewById(R.id.idea)
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)

            idea1 = itemView.findViewById(R.id.idea1)
            iv_cover1 = itemView.findViewById(R.id.iv_cover1)
            title1 = itemView.findViewById(R.id.title1)

            idea2 = itemView.findViewById(R.id.idea2)
            iv_cover2 = itemView.findViewById(R.id.iv_cover2)
            title2 = itemView.findViewById(R.id.title2)

            idea3 = itemView.findViewById(R.id.idea3)
            iv_cover3 = itemView.findViewById(R.id.iv_cover3)
            title3= itemView.findViewById(R.id.title3)

            idea4 = itemView.findViewById(R.id.idea4)
            iv_cover4 = itemView.findViewById(R.id.iv_cover4)
            title4 = itemView.findViewById(R.id.title4)

            idea5 = itemView.findViewById(R.id.idea5)
            iv_cover5 = itemView.findViewById(R.id.iv_cover5)
            title5 = itemView.findViewById(R.id.title5)

            idea6 = itemView.findViewById(R.id.idea6)
            iv_cover6 = itemView.findViewById(R.id.iv_cover6)
            title6 = itemView.findViewById(R.id.title6)

            idea7 = itemView.findViewById(R.id.idea7)
            iv_cover7 = itemView.findViewById(R.id.iv_cover7)
            title7 = itemView.findViewById(R.id.title7)

        }

        @SuppressLint("CheckResult")
        fun bindData(position: Int) {

            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v, position)
            }

            if (artist.isNotEmpty()) {

                Glide.with(context).load(artist[0].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover)
                title.text = artist[0].name

                Glide.with(context).load(artist[1].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover1)
                title1.text = artist[1].name

                Glide.with(context).load(artist[2].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover2)
                title2.text = artist[2].name

                Glide.with(context).load(artist[3].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover3)
                title3.text = artist[3].name

                Glide.with(context).load(artist[4].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover4)
                title4.text = artist[4].name

                Glide.with(context).load(artist[5].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover5)
                title5.text = artist[5].name

                Glide.with(context).load(artist[6].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover6)
                title6.text = artist[6].name

                Glide.with(context).load(artist[7].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover7)
                title7.text = artist[7].name

                RxView.clicks(idea)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[0].artist_id)
                        intent.putExtra("type", artist[0].type)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea1)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[1].artist_id)
                        intent.putExtra("type", artist[1].type)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea2)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[2].artist_id)
                        intent.putExtra("type", artist[2].type)
                        context.startActivity(intent)
                    }


                RxView.clicks(idea3)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[3].artist_id)
                        intent.putExtra("type", artist[3].type)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea4)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[4].artist_id)
                        intent.putExtra("type", artist[4].type)
                        context.startActivity(intent)
                    }

                RxView.clicks(idea5)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[5].artist_id)
                        intent.putExtra("type", artist[5].type)
                        context.startActivity(intent)
                    }
                RxView.clicks(idea6)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[6].artist_id)
                        intent.putExtra("type", artist[6].type)
                        context.startActivity(intent)
                    }
                RxView.clicks(idea7)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        val intent = Intent()
                        context.let { intent.setClass(it, ArtistDetActivity().javaClass) }
                        intent.putExtra("id", artist[7].artist_id)
                        intent.putExtra("type", artist[7].type)
                        context.startActivity(intent)
                    }
            }


        }
    }

    inner class Title4Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var more: TextView
        var title: TextView
        var right: ImageView

        init {
            title = itemView.findViewById(R.id.title)
            more = itemView.findViewById(R.id.more)
            right = itemView.findViewById(R.id.right)
        }

        @SuppressLint("CheckResult")
        fun bindData() {
            more.visibility = View.GONE
            right.visibility = View.GONE
            title.text = context.getText(R.string.item4)
        }
    }

    inner class Time4Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idea: RelativeLayout
        var iv_cover: ImageView
        var title: TextView
        var txt: TextView
        var more : ImageView
        var idea1: RelativeLayout
        var iv_cover1: ImageView
        var title1: TextView
        var txt1: TextView
        var more1 : ImageView
        var idea2: RelativeLayout
        var iv_cover2: ImageView
        var title2: TextView
        var txt2: TextView
        var more2 : ImageView
        var idea3: RelativeLayout
        var iv_cover3: ImageView
        var title3: TextView
        var txt3: TextView
        var more3 : ImageView
        var idea4: RelativeLayout
        var iv_cover4: ImageView
        var title4: TextView
        var txt4: TextView
        var more4 : ImageView
        var idea5: RelativeLayout
        var iv_cover5: ImageView
        var title5: TextView
        var txt5: TextView
        var more5 : ImageView
        var idea6: RelativeLayout
        var iv_cover6: ImageView
        var title6: TextView
        var txt6: TextView
        var more6 : ImageView
        var idea7: RelativeLayout
        var iv_cover7: ImageView
        var title7: TextView
        var txt7: TextView
        var more7 : ImageView


        init {
            idea = itemView.findViewById(R.id.idea)
            iv_cover = itemView.findViewById(R.id.iv_cover)
            title = itemView.findViewById(R.id.title)
            txt = itemView.findViewById(R.id.txt)
            more = itemView.findViewById(R.id.more)

            idea1 = itemView.findViewById(R.id.idea1)
            iv_cover1 = itemView.findViewById(R.id.iv_cover1)
            title1 = itemView.findViewById(R.id.title1)
            txt1 = itemView.findViewById(R.id.txt1)
            more1 = itemView.findViewById(R.id.more1)

            idea2 = itemView.findViewById(R.id.idea2)
            iv_cover2 = itemView.findViewById(R.id.iv_cover2)
            title2 = itemView.findViewById(R.id.title2)
            txt2 = itemView.findViewById(R.id.txt2)
            more2 = itemView.findViewById(R.id.more2)

            idea3 = itemView.findViewById(R.id.idea3)
            iv_cover3 = itemView.findViewById(R.id.iv_cover3)
            title3= itemView.findViewById(R.id.title3)
            txt3 = itemView.findViewById(R.id.txt3)
            more3 = itemView.findViewById(R.id.more3)

            idea4 = itemView.findViewById(R.id.idea4)
            iv_cover4 = itemView.findViewById(R.id.iv_cover4)
            title4 = itemView.findViewById(R.id.title4)
            txt4 = itemView.findViewById(R.id.txt4)
            more4 = itemView.findViewById(R.id.more4)

            idea5 = itemView.findViewById(R.id.idea5)
            iv_cover5 = itemView.findViewById(R.id.iv_cover5)
            title5 = itemView.findViewById(R.id.title5)
            txt5 = itemView.findViewById(R.id.txt5)
            more5 = itemView.findViewById(R.id.more5)

            idea6 = itemView.findViewById(R.id.idea6)
            iv_cover6 = itemView.findViewById(R.id.iv_cover6)
            title6 = itemView.findViewById(R.id.title6)
            txt6 = itemView.findViewById(R.id.txt6)
            more6 = itemView.findViewById(R.id.more6)

            idea7 = itemView.findViewById(R.id.idea7)
            iv_cover7 = itemView.findViewById(R.id.iv_cover7)
            title7 = itemView.findViewById(R.id.title7)
            txt7 = itemView.findViewById(R.id.txt7)
            more7 = itemView.findViewById(R.id.more7)
        }

        @SuppressLint("CheckResult")
        fun bindData(position: Int) {

            itemView.setOnClickListener { v ->
                mItemClickListener?.onItemClick(v, position)
            }


            if (song.isNotEmpty()) {

                Glide.with(context).load(song[0].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover)
                title.text = song[0].name
                var srtist_name = ""
                for (it in song[0].all_artist) {
                    if (srtist_name != "") {
                        srtist_name += "/" + it.name
                    } else {
                        srtist_name = it.name
                    }

                }
                txt.text = srtist_name

                more.setOnClickListener {
                    Observable.just(song[0]).subscribe(IndexActivity.observert)
                }

                Glide.with(context).load(song[1].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover1)
                title1.text = song[1].name
                var srtist_name1 = ""
                for (it in song[1].all_artist) {
                    if (srtist_name1 != "") {
                        srtist_name1 += "/" + it.name
                    } else {
                        srtist_name1 = it.name
                    }

                }
                txt1.text = srtist_name1

                more1.setOnClickListener {
                    Observable.just(song[2]).subscribe(IndexActivity.observert)
                }

                Glide.with(context).load(song[2].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover2)
                title2.text = song[2].name
                var srtist_name2 = ""
                for (it in song[2].all_artist) {
                    if (srtist_name2 != "") {
                        srtist_name2 += "/" + it.name
                    } else {
                        srtist_name2 = it.name
                    }

                }
                txt2.text = srtist_name2

                more2.setOnClickListener {
                    Observable.just(song[2]).subscribe(IndexActivity.observert)
                }

                Glide.with(context).load(song[3].pic_url)
                    .placeholder(R.drawable.undetback).into(iv_cover3)
                title3.text = song[3].name
                var srtist_name3 = ""
                for (it in song[3].all_artist) {
                    if (srtist_name3 != "") {
                        srtist_name3 += "/" + it.name
                    } else {
                        srtist_name3 = it.name
                    }

                }
                txt3.text = srtist_name3

                more3.setOnClickListener {
                    Observable.just(song[3]).subscribe(IndexActivity.observert)
                }
            }

            Glide.with(context).load(song[4].pic_url)
                .placeholder(R.drawable.undetback).into(iv_cover4)
            title4.text = song[4].name
            var srtist_name4 = ""
            for (it in song[4].all_artist) {
                if (srtist_name4 != "") {
                    srtist_name4 += "/" + it.name
                } else {
                    srtist_name4 = it.name
                }

            }
            txt4.text = srtist_name4

            more4.setOnClickListener {
                Observable.just(song[4]).subscribe(IndexActivity.observert)
            }


            Glide.with(context).load(song[5].pic_url)
                .placeholder(R.drawable.undetback).into(iv_cover5)
            title5.text = song[5].name
            var srtist_name5 = ""
            for (it in song[5].all_artist) {
                if (srtist_name5 != "") {
                    srtist_name5 += "/" + it.name
                } else {
                    srtist_name5 = it.name
                }

            }
            txt5.text = srtist_name5

            more5.setOnClickListener {
                Observable.just(song[5]).subscribe(IndexActivity.observert)
            }

            Glide.with(context).load(song[6].pic_url)
                .placeholder(R.drawable.undetback).into(iv_cover6)
            title6.text = song[6].name
            var srtist_name6 = ""
            for (it in song[6].all_artist) {
                if (srtist_name6 != "") {
                    srtist_name6 += "/" + it.name
                } else {
                    srtist_name6 = it.name
                }

            }
            txt6.text = srtist_name6

            more6.setOnClickListener {
                Observable.just(song[6]).subscribe(IndexActivity.observert)
            }

            Glide.with(context).load(song[7].pic_url)
                .placeholder(R.drawable.undetback).into(iv_cover7)
            title7.text = song[7].name
            var srtist_name7 = ""
            for (it in song[7].all_artist) {
                if (srtist_name7 != "") {
                    srtist_name7 += "/" + it.name
                } else {
                    srtist_name7 = it.name
                }

            }
            txt7.text = srtist_name7

            more7.setOnClickListener {
                Observable.just(song[7]).subscribe(IndexActivity.observert)
            }

            RxView.clicks(idea)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 0)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)

                }

            RxView.clicks(idea1)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 1)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }

            RxView.clicks(idea2)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 2)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }


            RxView.clicks(idea3)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 3)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }

            RxView.clicks(idea4)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 4)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }

            RxView.clicks(idea5)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 5)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }
            RxView.clicks(idea6)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 6)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }
            RxView.clicks(idea7)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    val json: String = Gson().toJson(song)
                    val intent = Intent()
                    context.let { intent.setClass(it, MusicPlayActivity().javaClass) }
                    intent.putExtra("album_id", 0L)
                    intent.putExtra("pos", 7)
                    intent.putExtra("list", json)
                    intent.putExtra("type", 2)
                    context.startActivity(intent)
                }
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.mItemClickListener = itemClickListener
    }


}


