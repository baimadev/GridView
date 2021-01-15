package com.baima.jetpack

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.BannerViewHolder>() {

    private var urls: List<String>? = null
    private var count = 5

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.home_banner_item2,
            parent,
            false
        )
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {

        val realIndex = getRealPosition(position)

        Glide.with(holder.imageView1.context)
            .load(urls?.get(realIndex))
            .error(R.drawable.ic_launcher_background)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imageView1)

    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }


    fun loadData(urls: List<String>) {
        this.urls = urls
        count = urls.size
        notifyDataSetChanged()
    }

    private fun getRealPosition(position: Int): Int {
        if (urls == null) return 0
        return position % count
    }

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView1: ImageView = view.findViewById(R.id.banner1)

    }

}