package com.baima.jetpack

import android.annotation.SuppressLint
import android.app.Person
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.core.view.MotionEventCompat.getPointerId
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import kotlin.math.abs


class Banner : ViewPager{

    constructor(context: Context):super(context)

    constructor(context: Context, attributeSet: AttributeSet):super(context, attributeSet)

    private val DELAY_TIME = 4000
    private var urls: List<String>? = null
    private var viewPagerViews: ArrayList<ImageView> = ArrayList()

    //private var currentItem = 0
    private var mPagerAdapter : MyPagerAdapter? = null
    private var count = 0
    private val campaignBannerTimerHandler = Handler()
    private val pagerScroller = ViewPagerScroller(context)
    private val slideRunnable: Runnable = object : Runnable {
        override fun run() {
            currentItem++
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null)
            campaignBannerTimerHandler.postDelayed(this, 4000)
        }
    }


    private var mVelocityTracker :VelocityTracker? = null
    private var configuration: ViewConfiguration = ViewConfiguration.get(context)
    var mMaximumVelocity: Int = configuration.scaledMaximumFlingVelocity
    var mMinimumVelocity = configuration.scaledMinimumFlingVelocity

    init {
        mPagerAdapter = MyPagerAdapter()
        adapter = mPagerAdapter
        startScroll()


        pagerScroller.setScrollDuration(1000) //设置时间，时间越长，速度越慢
        pagerScroller.initViewPagerScroll(this)

        setOnTouchListener{ view, ev ->



            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            val action = MotionEventCompat.getActionMasked(ev)
            Log.e("xia","onTOuch")
            var mActivePointerId = 0
            when(action){
                MotionEvent.ACTION_DOWN -> {
                    if (!pagerScroller.isFinished) {
                        pagerScroller.abortAnimation()
                        mActivePointerId = ev!!.getPointerId(0)
                    }
                }
                MotionEvent.ACTION_UP -> {

                    //当手指立刻屏幕时，获得速度，作为fling的初始速度
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    val initialVelocity = mVelocityTracker!!.getXVelocity(mActivePointerId).toInt()
                    Log.e("xia","${initialVelocity} $mMinimumVelocity")
                    if (abs(initialVelocity) >mMinimumVelocity) {
                        //setCurrentItem(currentItem+5,true)
                        doFling(-initialVelocity)

                    }
                    startScroll()
                }

            }


            if (mVelocityTracker != null) {
                mVelocityTracker!!.addMovement(ev)
            }
           super.onTouchEvent(ev)
        }

    }


    private fun doFling(speed: Int) {
        pagerScroller.fling(scrollX, 0, 0, speed, -5000, 10000, 0, 0)
        invalidate()
    }
    override fun computeScroll() {
        if (pagerScroller.computeScrollOffset()) {
            scrollTo(pagerScroller.currX, pagerScroller.currY)
            postInvalidate()
        }
    }

    fun loadData(urls: List<String>): Banner {
        this.urls = urls
        count = urls.size
        currentItem = getFirstPosition()
        mPagerAdapter?.notifyDataSetChanged()
        return this
    }

    fun getFirstPosition(): Int {
        if (urls==null)return 0
        var center = Int.MAX_VALUE / 2
        center -= center % urls!!.size
        Log.e("xia", "setFirst $center")
        return center
    }


    fun startScroll() {
        campaignBannerTimerHandler.removeCallbacks(slideRunnable)
        campaignBannerTimerHandler.postDelayed(slideRunnable, 4000)
    }

    fun stopScroll() {
        campaignBannerTimerHandler.removeCallbacks(slideRunnable)
    }


    inner class MyPagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return Int.MAX_VALUE
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = inflate(context, R.layout.home_banner_item, null)
            val topBanner = view.findViewById<ImageView>(R.id.banner1)
            val textView = view.findViewById<TextView>(R.id.text)
            textView.text = "${getRealPosition(position).toString()}  fake pos：$position"


            //TODO ViewPage  onTouchevent actionup startScroll
            topBanner.setOnTouchListener { _: View?, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    stopScroll()
                }

                false
            }


            Glide.with(context)
                    .load(urls?.get(getRealPosition(position)))
                    .error(R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(topBanner)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        fun getRealPosition(position: Int):Int{
            if (urls==null)return 0
            return  position % urls!!.size
        }




    }


//    inner class ViewHolder {
//        var topBanner: ImageView? = null
//
//        fun createView(): View? {
//            val view: View = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false)
//            tvBanner = view.findViewById<View>(R.id.tv_banner)
//
//            return view
//        }
//
//        fun bind(position: Int, s: String?) {
//            if (position % 3 == 0) {
//                ivBanner.setBackgroundColor(-0x10000)
//            } else if (position % 3 == 1) {
//                ivBanner.setBackgroundColor(-0xff0100)
//            } else {
//                ivBanner.setBackgroundColor(-0xffff01)
//            }
//            tvBanner.setText(s)
//        }
//    }


}

