package com.libs.nelson.marqueeviewlib

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class MarqueeView : ViewGroup {

    private val mMaxWidth = Integer.MAX_VALUE
    private val mMaxHeight = Integer.MAX_VALUE
    private val DIRTY_TAG = "DIRTY"

    private var mItemLayout = -1

    private var mOrientation = LinearLayout.VERTICAL
    private var mIsReverse = false

    private var mAnimatorRunnable: AnimatorRunnable? = null
    private var mAnimationPercent = 0f
    private var mDefaultAnimationDuration = 1000L
    private var mDefaultItemStayDuration = 1000L

    private var mAdapter: MarqueeView.MarqueeViewAdapter? = null
    private val mItemViewCache = ArrayList<View>()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context?.obtainStyledAttributes(
                attrs, R.styleable.MarqueeView, defStyleAttr, 0)

        val index = a?.getInt(R.styleable.MarqueeView_orientation, -1)
        index?.let {
            if (index >= 0) {
                setOrientation(index)
            }
        }

        val animatorDuration = a?.getInt(R.styleable.MarqueeView_animator_duration, 1000)
        val stayDuration = a?.getInt(R.styleable.MarqueeView_stay_duration, 1000)

        animatorDuration?.let { mDefaultAnimationDuration = it.toLong() }
        stayDuration?.let { mDefaultItemStayDuration = it.toLong() }
        a?.getBoolean(R.styleable.MarqueeView_reverse_animator, false)?.let { mIsReverse = it }
        a?.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childCount = childCount

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val ps = paddingStart
        val pe = paddingEnd
        val pt = paddingTop
        val pb = paddingBottom

        var childMaxWidth = 0
        var childMaxHeight = 0
        var child: View
        for (i in 0 until childCount) {
            child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize - ps - pe, MeasureSpec.getMode(widthMeasureSpec)), View.MeasureSpec.makeMeasureSpec(heightSize - pt - pb, View.MeasureSpec.getMode(heightMeasureSpec)))

            childMaxWidth = Math.max(childMaxWidth, child.measuredWidth)
            childMaxHeight = Math.max(childMaxHeight, child.measuredHeight)
        }

        val width = resolveAdjustedSize(childMaxWidth + ps + pe, mMaxWidth, widthMeasureSpec)
        val height = resolveAdjustedSize(childMaxHeight + pt + pb, mMaxHeight, heightMeasureSpec)

        val finalWidthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.getMode(widthMeasureSpec))
        val finalHeightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.getMode(heightMeasureSpec))
        setMeasuredDimension(finalWidthSpec, finalHeightSpec)

    }

    private fun resolveAdjustedSize(desiredSize: Int, maxSize: Int,
                                    measureSpec: Int): Int {
        var result = desiredSize
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        when (specMode) {
            View.MeasureSpec.UNSPECIFIED ->
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = Math.min(desiredSize, maxSize)
            View.MeasureSpec.AT_MOST ->
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                result = Math.min(Math.min(desiredSize, specSize), maxSize)
            View.MeasureSpec.EXACTLY ->
                // No choice. Do what we are told.
                result = specSize
        }
        return result
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        if (childCount == 0) {
            return
        }
        if (childCount == 1) {
            val child = getChildAt(0)
            child.layout(paddingLeft, paddingTop, paddingLeft + child.measuredWidth, paddingTop + child.measuredHeight)
            return
        }

        var child0 = getChildAt(0)
        var child1 = getChildAt(1)

        if (mIsReverse) {
            child1 = getChildAt(0)
            child0 = getChildAt(1)

        }

        if (mOrientation == LinearLayout.VERTICAL) {
            child0.layout(paddingLeft, paddingTop - (child0.measuredHeight * mAnimationPercent).toInt()
                    , paddingLeft + child0.measuredWidth, paddingTop + child0.measuredHeight - (child0.measuredHeight * mAnimationPercent).toInt())
            child1.layout(paddingLeft, paddingTop + child0.measuredHeight - (child1.measuredHeight * mAnimationPercent).toInt(),
                    paddingLeft + child1.measuredWidth, paddingTop + child0.measuredHeight + child1.measuredHeight - (child1.measuredHeight * mAnimationPercent).toInt())
        } else {
            child0.layout(paddingLeft - (child0.measuredWidth * mAnimationPercent).toInt(), paddingTop,
                    paddingLeft + child0.measuredWidth - (child0.measuredWidth * mAnimationPercent).toInt(), paddingTop + child0.measuredHeight)
            child1.layout(paddingLeft + child0.measuredWidth - (child0.measuredWidth * mAnimationPercent).toInt(),
                    paddingTop, paddingLeft + child0.measuredWidth + child1.measuredWidth - (child1.measuredWidth * mAnimationPercent).toInt(), paddingTop + child1.measuredHeight)
        }
    }

    fun setOrientation(@LinearLayoutCompat.OrientationMode orientation: Int) {
        if (mOrientation != orientation) {
            mOrientation = orientation
            requestLayout()
        }
    }

    inner class AnimatorRunnable : Runnable {
        private val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        private var currentPosition = 0


        override fun run() {

            mAdapter?.let {
                valueAnimator.duration = 0
                valueAnimator.addUpdateListener { animation ->
                    val normalPercent = animation.animatedValue.toString().toFloat()
                    mAnimationPercent = if (mIsReverse) {
                        1 - normalPercent
                    } else {
                        normalPercent
                    }
                    requestLayout()
                }

                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        currentPosition = 0

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (childCount > 1) {
                            removeViewAt(0)
                        }
                        currentPosition++
                        if (currentPosition >= it.getItemCount()) {
                            currentPosition = 0
                        }
                        valueAnimator.start()

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        valueAnimator.startDelay = mDefaultItemStayDuration
                        valueAnimator.duration = mDefaultAnimationDuration
                        for (i in 0 until childCount) {
                            val item = getChildAt(i)
                            item?.let {
                                if (item.tag == DIRTY_TAG) {
                                    removeView(item)
                                }
                            }

                        }
                        if (currentPosition < it.getItemCount()) {
                            addView(getItemView(it.getItemLayout(), currentPosition))
                        }

                    }

                })
                if (!valueAnimator.isStarted) {
                    valueAnimator.start()
                }
            }

        }

        fun stop() {
            removeCallbacks(this)
            if (valueAnimator.isStarted) {
                valueAnimator.cancel()
            }
            valueAnimator.removeAllListeners()
            valueAnimator.removeAllUpdateListeners()
            for (i in 0 until childCount) {
                getChildAt(i).tag = DIRTY_TAG
            }
        }

    }

    private fun reset() {
        mAnimatorRunnable?.stop()
        mAnimationPercent = 0f
        mAnimatorRunnable = AnimatorRunnable()
        mItemViewCache.clear()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        reset()
        post(mAnimatorRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimatorRunnable?.stop()

    }

    private fun getItemView(layout: Int, position: Int): View {
        if (layout != mItemLayout) {
            mItemViewCache.clear()
            removeAllViews()
            mItemLayout = layout
        }
        var finalItem: View? = mItemViewCache.find {
            it.parent == null
        }
        finalItem ?: let {
            val itemView = LayoutInflater.from(context).inflate(layout, this, false)
            mItemViewCache.add(itemView)
            finalItem = itemView
        }
        mAdapter?.onBindItemView(finalItem!!, position)
        return finalItem!!
    }

    fun setAdapter(adapter: MarqueeViewAdapter) {
        mAdapter = adapter
        reset()
    }


    abstract class MarqueeViewAdapter {

        abstract fun getItemLayout(): Int

        abstract fun onBindItemView(itemView: View, position: Int)

        abstract fun getItemCount(): Int

    }
}