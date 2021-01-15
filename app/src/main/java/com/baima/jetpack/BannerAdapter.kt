package com.baima.jetpack

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import jp.co.rakuten.golf.gora2.activity.home.customview.BannerLinearSnapHelper
import java.util.*

@SuppressLint("ClickableViewAccessibility")
class BannerAdapter(val recyclerView: RecyclerView) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private var urls: List<String>? = null
    private var count = 5 //default count
    private val campaignBannerTimerHandler = Handler()
    private var onImageClickListener: ((Int) -> Boolean)? = null
    private var updateFlg: String = ""
    private var snapHelper : BannerLinearSnapHelper? =null
    private val slideRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!recyclerView.layoutManager!!.isSmoothScrolling) {
                val targetPos = getCurrentItem()+1
                snapHelper?.snapToTargetExistingView(targetPos)

            }
            campaignBannerTimerHandler.postDelayed(this, 4000)
        }
    }

    init {
        snapHelper = BannerLinearSnapHelper { isScroll ->
            if (isScroll) {
                stopScroll()
            } else {
                startScroll()
            }
        }.also {
            it.attachToRecyclerView(recyclerView)
        }

        recyclerView.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                startScroll()
            }
            return@setOnTouchListener false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.home_banner_item2,
                parent,
                false
        )
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {

        holder.itemView.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                stopScroll()
            }
            false
        }

        val realIndex = getRealPosition(position)

        val options = RequestOptions.signatureOf(ObjectKey(updateFlg))
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_launcher_background)

        Glide.with(holder.itemView.context)
                .load(urls?.get(realIndex))
                .apply(options)
                .into(holder.imageView)

        holder.imageView.setOnClickListener {
            onImageClickListener?.invoke(realIndex)
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    fun loadData(urls: List<String>, updateFlg: String) {
        this.urls = urls
        this.updateFlg = updateFlg
        count = urls.size
        val firstPos = getFirstPosition()
        //recyclerView.layoutManager?.scrollToPosition(getFirstPosition())
        val manager = recyclerView.layoutManager as LinearLayoutManager
        val offset = snapHelper?.firstItemDistanceToStart(manager) ?:0
        manager.scrollToPositionWithOffset(firstPos,offset)
        startScroll()
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (Int) -> Boolean) {
        this.onImageClickListener = listener
        notifyDataSetChanged()
    }

    private fun getFirstPosition(): Int {
        if (urls == null) return 0
        var center = Int.MAX_VALUE / 2
        center -= center % urls!!.size
        return center
    }

    private fun getRealPosition(position: Int): Int {
        if (urls == null) return 0
        return position % count
    }

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.banner1)
    }

    private fun getCurrentItem():Int{
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    fun startScroll() {
        campaignBannerTimerHandler.removeCallbacks(slideRunnable)
        campaignBannerTimerHandler.postDelayed(slideRunnable, 4000)
    }

    fun stopScroll() {
        campaignBannerTimerHandler.removeCallbacks(slideRunnable)
    }

}