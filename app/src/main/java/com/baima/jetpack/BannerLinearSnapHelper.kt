/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.rakuten.golf.gora2.activity.home.customview

import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import kotlin.math.abs
import kotlin.math.roundToInt

class BannerLinearSnapHelper(onScrollListener: ((Boolean) -> Unit)) :
        BannerSnapHelper(onScrollListener) {

    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null

    override fun calculateDistanceToFinalSnap(
            layoutManager: RecyclerView.LayoutManager, targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = firstItemDistanceToStart(
                    targetView,
                    layoutManager as LinearLayoutManager
            )
        } else {
            out[0] = 0
        }
        out[1] = 0
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager.canScrollVertically()) {
            return findStartView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findStartView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager?,
            velocityX: Int,
            velocityY: Int
    ): Int {
        if (layoutManager !is ScrollVectorProvider) {
            return RecyclerView.NO_POSITION
        }
        val itemCount = layoutManager.itemCount
        Log.e("xia","itemcount $itemCount")
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val currentPosition = layoutManager.getPosition(currentView)
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val vectorProvider = layoutManager as ScrollVectorProvider
        // deltaJumps sign comes from the velocity which may not match the order of children in
        // the LayoutManager. To overcome this, we ask for a vector from the LayoutManager to
        // get the direction.
        val vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1)
                ?: // cannot get a vector for the given position.
                return RecyclerView.NO_POSITION
        var vDeltaJump: Int
        var hDeltaJump: Int
        if (layoutManager.canScrollHorizontally()) {
            hDeltaJump = estimateNextPositionDiffForFling(
                    layoutManager,
                    getHorizontalHelper(layoutManager), velocityX, 0
            )
            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump
            }
        } else {
            hDeltaJump = 0
        }
        if (layoutManager.canScrollVertically()) {
            vDeltaJump = estimateNextPositionDiffForFling(
                    layoutManager,
                    getVerticalHelper(layoutManager), 0, velocityY
            )
            if (vectorForEnd.y < 0) {
                vDeltaJump = -vDeltaJump
            }
        } else {
            vDeltaJump = 0
        }
        val deltaJump = if (layoutManager.canScrollVertically()) vDeltaJump else hDeltaJump
        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION
        }
        var targetPos = currentPosition + deltaJump
        if (targetPos < 0) {
            targetPos = 0
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1
        }
        return targetPos
    }

    private fun firstItemDistanceToStart(targetView: View, layoutManager: LinearLayoutManager): Int {
        val helper = getHorizontalHelper(layoutManager)
        val parentWidth = layoutManager.width
        val childWidth = layoutManager.getDecoratedMeasuredWidth(targetView)
        val offset = (parentWidth - 2 * childWidth) / 2
        val viewStart = helper.getDecoratedStart(targetView) - offset
        return (viewStart - helper.startAfterPadding)
    }

    fun firstItemDistanceToStart(layoutManager: LinearLayoutManager): Int {
        val targetView = layoutManager.findViewByPosition(0) ?: return 0
        val helper = getHorizontalHelper(layoutManager)
        val parentWidth = layoutManager.width
        val childWidth = layoutManager.getDecoratedMeasuredWidth(targetView)
        val offset = (parentWidth - 2 * childWidth) / 2
        return (helper.getDecoratedStart(targetView) - helper.startAfterPadding) + offset
    }

    private fun estimateNextPositionDiffForFling(
            layoutManager: RecyclerView.LayoutManager,
            helper: OrientationHelper, velocityX: Int, velocityY: Int
    ): Int {
        val distances = calculateScrollDistance(velocityX, velocityY)
        val distancePerChild = computeDistancePerChild(layoutManager, helper)
        if (distancePerChild <= 0) {
            return 0
        }
        val distance =
                if (abs(distances[0]) > abs(distances[1])) distances[0] else distances[1]
        return (distance / distancePerChild).roundToInt()
    }

    private fun findStartView(layoutManager: RecyclerView.LayoutManager,
                              helper: OrientationHelper): View? {
        if (layoutManager is LinearLayoutManager) {
            val firstChild = layoutManager.findFirstVisibleItemPosition()
            val isLastItem = (layoutManager
                    .findLastCompletelyVisibleItemPosition()
                    == layoutManager.getItemCount() - 1)
            if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                return null
            }
            val child = layoutManager.findViewByPosition(firstChild)
            return if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                    && helper.getDecoratedEnd(child) > 0) {
                child
            } else {
                if (layoutManager.findLastCompletelyVisibleItemPosition()
                        == layoutManager.getItemCount() - 1) {
                    null
                } else {
                    layoutManager.findViewByPosition(firstChild + 1)
                }
            }
        }
        return null
    }

    private fun computeDistancePerChild(
            layoutManager: RecyclerView.LayoutManager,
            helper: OrientationHelper
    ): Float {
        var minPosView: View? = null
        var maxPosView: View? = null
        var minPos = Int.MAX_VALUE
        var maxPos = Int.MIN_VALUE
        val childCount = layoutManager.childCount
        Log.e("xia","count $childCount")
        if (childCount == 0) {
            return INVALID_DISTANCE
        }

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val pos = layoutManager.getPosition(child!!)

            if (pos == RecyclerView.NO_POSITION) {
                continue
            }
            if (pos < minPos) {
                minPos = pos
                minPosView = child
            }
            if (pos > maxPos) {
                maxPos = pos
                maxPosView = child
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE
        }
        val start =
                helper.getDecoratedStart(minPosView).coerceAtMost(helper.getDecoratedStart(maxPosView))
        val end =
                helper.getDecoratedEnd(minPosView).coerceAtLeast(helper.getDecoratedEnd(maxPosView))
        val distance = end - start

        return if (distance == 0) {
            INVALID_DISTANCE
        } else 1f * distance / (maxPos - minPos + 1)
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mVerticalHelper == null || mVerticalHelper!!.layoutManager != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper!!
    }

    private fun getHorizontalHelper(
            layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (mHorizontalHelper == null || mHorizontalHelper!!.layoutManager != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }

    companion object {
        private const val INVALID_DISTANCE = 1f
    }
}

abstract class BannerSnapHelper(onScrollListener: ((Boolean) -> Unit)) : OnFlingListener() {
    var mRecyclerView: RecyclerView? = null
    private var mGravityScroller: Scroller? = null

    // Handles the snap on scroll case.
    private val mScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                var mScrolled = false
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                        mScrolled = false
                        onScrollListener.invoke(false) // Scroll finished

                        snapToTargetExistingView()
                    }
                    if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                        onScrollListener.invoke(true) // Scrolling
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dx != 0 || dy != 0) {
                        mScrolled = true
                    }
                }
            }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        val layoutManager = mRecyclerView!!.layoutManager ?: return false
        val minFlingVelocity = mRecyclerView!!.minFlingVelocity
        return ((abs(velocityY) > minFlingVelocity || abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY))
    }

    @Throws(IllegalStateException::class)
    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (mRecyclerView === recyclerView) {
            return  // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks()
        }
        mRecyclerView = recyclerView
        if (mRecyclerView != null) {
            setupCallbacks()
            mGravityScroller = Scroller(
                    mRecyclerView!!.context,
                    DecelerateInterpolator()
            )
            snapToTargetExistingView()
        }
    }

    @Throws(IllegalStateException::class)
    private fun setupCallbacks() {
        check(mRecyclerView!!.onFlingListener == null) { "An instance of OnFlingListener already set." }
        mRecyclerView!!.addOnScrollListener(mScrollListener)
        mRecyclerView!!.onFlingListener = this
    }

    private fun destroyCallbacks() {
        mRecyclerView!!.removeOnScrollListener(mScrollListener)
        mRecyclerView!!.onFlingListener = null
    }

    open fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray {
        val outDist = IntArray(2)
        mGravityScroller!!.fling(
                0,
                0,
                velocityX,
                velocityY,
                Int.MIN_VALUE,
                Int.MAX_VALUE,
                Int.MIN_VALUE,
                Int.MAX_VALUE
        )
        outDist[0] = mGravityScroller!!.finalX
        outDist[1] = mGravityScroller!!.finalY
        return outDist
    }

    private fun snapFromFling(
            layoutManager: RecyclerView.LayoutManager, velocityX: Int,
            velocityY: Int
    ): Boolean {
        if (layoutManager !is ScrollVectorProvider) {
            return false
        }
        val smoothScroller = createScroller(layoutManager) ?: return false
        val targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY)
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false
        }
        smoothScroller.targetPosition = targetPosition
        Log.e("xia","fling $targetPosition")
        layoutManager.startSmoothScroll(smoothScroller)
        return true
    }

    fun snapToTargetExistingView() {
        if (mRecyclerView == null) {
            return
        }
        val layoutManager = mRecyclerView!!.layoutManager ?: return
        val snapView = findSnapView(layoutManager) ?: return
        val snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView)
        if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
            mRecyclerView!!.smoothScrollBy(snapDistance[0], snapDistance[1])
        }
    }

    fun snapToTargetExistingView(pos: Int) {
        if (mRecyclerView == null) {
            return
        }
        val layoutManager = mRecyclerView!!.layoutManager as LinearLayoutManager
        var snapView = layoutManager.findViewByPosition(pos) ?: return
        val helper = OrientationHelper.createHorizontalHelper(layoutManager)
        val parentWidth = layoutManager.width
        val childWidth = layoutManager.getDecoratedMeasuredWidth(snapView)
        val offset = (parentWidth - 2 * childWidth) / 2
        val viewStart = helper.getDecoratedStart(snapView) - offset
        if (viewStart < childWidth) {
            snapView = layoutManager.findViewByPosition(pos + 1) ?: return
        }
        val snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView)
        if (snapDistance!![0] != 0) {
            mRecyclerView!!.smoothScrollBy(snapDistance[0], snapDistance[1])
        }
    }

    protected open fun createScroller(
            layoutManager: RecyclerView.LayoutManager
    ): SmoothScroller? {
        return createSnapScroller(layoutManager)
    }

    private fun createSnapScroller(
            layoutManager: RecyclerView.LayoutManager
    ): LinearSmoothScroller? {
        return if (layoutManager !is ScrollVectorProvider) {
            null
        } else object : LinearSmoothScroller(mRecyclerView!!.context) {
            override fun onTargetFound(
                    targetView: View,
                    state: RecyclerView.State,
                    action: Action
            ) {
                if (mRecyclerView == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return
                }
                val snapDistances = calculateDistanceToFinalSnap(
                        mRecyclerView!!.layoutManager!!,
                        targetView
                )
                val dx = snapDistances!![0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
    }

    abstract fun calculateDistanceToFinalSnap(
            layoutManager: RecyclerView.LayoutManager,
            targetView: View
    ): IntArray?


    abstract fun findSnapView(layoutManager: RecyclerView.LayoutManager): View?

    abstract fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager?, velocityX: Int,
            velocityY: Int
    ): Int

    companion object {
        const val MILLISECONDS_PER_INCH = 100f
    }
}