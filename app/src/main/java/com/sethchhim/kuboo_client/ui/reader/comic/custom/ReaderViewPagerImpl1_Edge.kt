package com.sethchhim.kuboo_client.ui.reader.comic.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.sethchhim.kuboo_client.Settings.RTL
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivity

open class ReaderViewPagerImpl1_Edge(context: Context, attrs: AttributeSet) : ReaderViewPagerImp0_Rtl(context, attrs) {

    private val readerComicActivity = context as ReaderComicActivity
    private var mStartDragX: Float = 0.toFloat()

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> mStartDragX = motionEvent.x
        }
        return try {
            super.onInterceptTouchEvent(motionEvent)
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        val isFirstOrLastPage = currentItem == 0 || currentItem == adapter!!.count - 1
        if (isFirstOrLastPage) motionEvent.searchHorizontalEdges()

        return try {
            super.onTouchEvent(motionEvent)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun onEndEdgeSwipe() = readerComicActivity.onSwipeOutOfBoundsStart()

    private fun onStartEdgeSwipe() = readerComicActivity.onSwipeOutOfBoundsEnd()

    private fun MotionEvent.searchHorizontalEdges() {
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> {
                if (RTL) {
                    if (currentItem == 0 && x < mStartDragX) onEndEdgeSwipe()
                    if (currentItem == adapter!!.count - 1 && x > mStartDragX) onStartEdgeSwipe()
                } else {
                    if (currentItem == 0 && x > mStartDragX) onEndEdgeSwipe()
                    if (currentItem == adapter!!.count - 1 && x < mStartDragX) onStartEdgeSwipe()
                }
            }
        }
    }

}


