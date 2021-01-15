package com.baima.jetpack

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


/**
 * ViewPager 滚动速度设置
 *
 * @author lyy
 */
class ViewPagerScroller : Scroller {
    private var mScrollDuration = 2000 // 滑动速度

    /**
     * 设置速度速度
     *
     * @param duration
     */
    fun setScrollDuration(duration: Int) {
        mScrollDuration = duration
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, interpolator: Interpolator?) : super(context, interpolator) {}
    constructor(
        context: Context?, interpolator: Interpolator?,
        flywheel: Boolean
    ) : super(context, interpolator, flywheel) {
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration)
    }

    fun initViewPagerScroll(viewPager: ViewPager?) {
        try {
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            mScroller[viewPager] = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}