package com.sethchhim.kuboo_client.ui.reader.comic.custom

import android.content.Context
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.OverScroller
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.reader.base.ReaderBaseActivity
import com.sethchhim.kuboo_client.ui.reader.pdf.ReaderPdfActivity
import timber.log.Timber

class ReaderPageImageView : AppCompatImageView {

    private val ZOOM_DURATION = 200
    private var mViewMode = PageViewMode.ASPECT_FIT
    private val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    private val m = FloatArray(9)
    private val mMatrix = Matrix()
    private val mScroller = OverScroller(context)
    private val mScaleGestureDetector = ScaleGestureDetector(context, PrivateScaleDetector())
    private val mDragGestureDetector = GestureDetector(context, PrivateDragListener())

    private var mHaveFrame = false
    private var mMaxScale: Float = 0.toFloat()
    private var mMinScale: Float = 0.toFloat()
    private var mOuterTouchListener: View.OnTouchListener? = null
    private var mOriginalScale: Float = 0.toFloat()
    private var mSkipScaling = false
    private var mTranslateRightEdge = false

    //0 = Both, 1 = Left, 2 = Right
    internal var navigationButtonType = 0

    private val currentScale: Float
        get() {
            mMatrix.getValues(m)
            return m[Matrix.MSCALE_X]
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    private fun init() {
        scaleType = ImageView.ScaleType.MATRIX
        imageMatrix = mMatrix
        mScroller.setFriction(ViewConfiguration.getScrollFriction() * 2)
        setScaleType(init = true)

        super.setOnTouchListener { v, event ->
            mScaleGestureDetector.onTouchEvent(event)
            mDragGestureDetector.onTouchEvent(event)
            if (mOuterTouchListener != null) mOuterTouchListener!!.onTouch(v, event)
            true
        }
    }

    internal fun setScaleType(init: Boolean = false) {
        when (Settings.SCALE_TYPE) {
            0 -> mViewMode = PageViewMode.ASPECT_FILL
            1 -> mViewMode = PageViewMode.ASPECT_FIT
            2 -> mViewMode = PageViewMode.FIT_WIDTH
        }

        if (!init) {
            mSkipScaling = false
            requestLayout()
            invalidate()
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        mHaveFrame = true
        scale()
        return changed
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mSkipScaling = false
        scale()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun setOnTouchListener(l: View.OnTouchListener) {
        mOuterTouchListener = l
    }

    fun setTranslateToRightEdge(translate: Boolean) {
        mTranslateRightEdge = translate
    }

    private fun scale() {
        val drawable = drawable
        if (drawable == null || !mHaveFrame || mSkipScaling) return

        val drawableWidth = drawable.intrinsicWidth
        val dheight = drawable.intrinsicHeight

        val width = width
        val height = height

        when {
            mViewMode === PageViewMode.ASPECT_FILL -> {
                val scale: Float
                var dx = 0f

                if (drawableWidth * height > width * dheight) {
                    scale = height.toFloat() / dheight.toFloat()
                    if (mTranslateRightEdge)
                        dx = width - drawableWidth * scale
                } else {
                    scale = width.toFloat() / drawableWidth.toFloat()
                }

                mMatrix.setScale(scale, scale)
                mMatrix.postTranslate((dx + 0.5f).toInt().toFloat(), 0f)
            }
            mViewMode === PageViewMode.ASPECT_FIT -> {
                val mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), dheight.toFloat())
                val mTempDst = RectF(0f, 0f, width.toFloat(), height.toFloat())

                mMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER)
            }
            mViewMode === PageViewMode.FIT_WIDTH -> {
                val widthScale = width.toFloat() / drawable.intrinsicWidth
                mMatrix.setScale(widthScale, widthScale)
                mMatrix.postTranslate(0f, 0f)
            }

            // calculate min/max scale
        }

        // calculate min/max scale
        val heightRatio = height.toFloat() / dheight
        val w = drawableWidth * heightRatio
        if (w < width) {
            mMinScale = height * 0.75f / dheight
            mMaxScale = Math.max(drawableWidth, width) * 1.5f / drawableWidth
        } else {
            mMinScale = width * 0.75f / drawableWidth
            mMaxScale = Math.max(dheight, height) * 1.5f / dheight
        }
        imageMatrix = mMatrix
        mOriginalScale = currentScale
        mSkipScaling = true
    }

    private inner class PrivateScaleDetector : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mMatrix.getValues(m)

            val scale = m[Matrix.MSCALE_X]
            var scaleFactor = detector.scaleFactor
            val scaleNew = scale * scaleFactor
            var scalable = true

            if (scaleFactor > 1 && mMaxScale - scaleNew < 0) {
                scaleFactor = mMaxScale / scale
                scalable = false
            } else if (scaleFactor < 1 && mMinScale - scaleNew > 0) {
                scaleFactor = mMinScale / scale
                scalable = false
            }

            mMatrix.postScale(
                    scaleFactor, scaleFactor,
                    detector.focusX, detector.focusY)
            imageMatrix = mMatrix

            return scalable
        }
    }

    private inner class PrivateDragListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            mScroller.forceFinished(true)
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            mMatrix.postTranslate(-distanceX, -distanceY)
            imageMatrix = mMatrix
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
//            captureSwipe(e1, e2, velocityX, velocityY)
            fling(velocityX, velocityY)
            return true
        }

        private fun captureSwipe(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                }
                result = true
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

        private fun fling(velocityX: Float, velocityY: Float) {
            val imageSize = computeCurrentImageSize()
            val offset = computeCurrentOffset()

            var minX = -imageSize.x - this@ReaderPageImageView.width
            var minY = -imageSize.y - this@ReaderPageImageView.height
            var maxX = 0
            var maxY = 0

            if (offset.x > 0) {
                minX = offset.x
                maxX = offset.x
            }
            if (offset.y > 0) {
                minY = offset.y
                maxY = offset.y
            }

            mScroller.fling(
                    offset.x, offset.y,
                    velocityX.toInt(), velocityY.toInt(),
                    minX, maxX, minY, maxY)
            ViewCompat.postInvalidateOnAnimation(this@ReaderPageImageView)
        }

        private fun onSwipeTop() {
            Timber.d("onSwipeTop")

        }

        private fun onSwipeBottom() {
            Timber.d("onSwipeBottom")
        }

        private fun onSwipeLeft() {
            Timber.d("onSwipeLeft")
            if (context is ReaderPdfActivity) (context as ReaderPdfActivity).goToPreviousPage()
        }

        private fun onSwipeRight() {
            Timber.d("onSwipeRight")
            if (context is ReaderPdfActivity) (context as ReaderPdfActivity).goToNextPage()
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_UP) {
                val scale = if (mOriginalScale == currentScale) mMaxScale else mOriginalScale
                zoomAnimated(e, scale)
            }
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val leftValue = (width * 0.1).toInt()
            val rightValue = (width * 0.9).toInt()
            if (Settings.DUAL_PANE) when (navigationButtonType) {
                0 -> when {
                    e.x < leftValue -> (context as ReaderBaseActivity).goToPreviousPage()
                    e.x > rightValue -> (context as ReaderBaseActivity).goToNextPage()
                    else -> (context as ReaderBaseActivity).showOverlay()
                }
                1 -> when {
                    e.x < leftValue -> (context as ReaderBaseActivity).goToPreviousPage()
                    else -> (context as ReaderBaseActivity).showOverlay()
                }
                2 -> when {
                    e.x > rightValue -> (context as ReaderBaseActivity).goToNextPage()
                    else -> (context as ReaderBaseActivity).showOverlay()
                }
                else -> Timber.e("Failed to find navigationButtonType!")
            } else when {
                e.x < leftValue -> (context as ReaderBaseActivity).goToPreviousPage()
                e.x > rightValue -> (context as ReaderBaseActivity).goToNextPage()
                else -> (context as ReaderBaseActivity).showOverlay()
            }
            return super.onSingleTapConfirmed(e)
        }
    }

    private fun zoomAnimated(e: MotionEvent, scale: Float) {
        post(ZoomAnimation(e.x, e.y, scale))
    }

    override fun computeScroll() {
        if (!mScroller.isFinished && mScroller.computeScrollOffset()) {
            val curX = mScroller.currX
            val curY = mScroller.currY

            mMatrix.getValues(m)
            m[Matrix.MTRANS_X] = curX.toFloat()
            m[Matrix.MTRANS_Y] = curY.toFloat()
            mMatrix.setValues(m)
            imageMatrix = mMatrix
            ViewCompat.postInvalidateOnAnimation(this)
        }
        super.computeScroll()
    }

    private fun computeCurrentImageSize(): Point {
        val size = Point()
        val d = drawable
        if (d != null) {
            mMatrix.getValues(m)

            val scale = m[Matrix.MSCALE_X]
            val width = d.intrinsicWidth * scale
            val height = d.intrinsicHeight * scale

            size.set(width.toInt(), height.toInt())

            return size
        }

        size.set(0, 0)
        return size
    }

    private fun computeCurrentOffset(): Point {
        val offset = Point()

        mMatrix.getValues(m)
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]

        offset.set(transX.toInt(), transY.toInt())

        return offset
    }

    override fun setImageMatrix(matrix: Matrix) {
        super.setImageMatrix(fixMatrix(matrix))
        postInvalidate()
    }

    private fun fixMatrix(matrix: Matrix): Matrix {
        if (drawable == null)
            return matrix

        matrix.getValues(m)

        val imageSize = computeCurrentImageSize()

        val imageWidth = imageSize.x
        val imageHeight = imageSize.y
        val maxTransX = imageWidth - width
        val maxTransY = imageHeight - height

        if (imageWidth > width)
            m[Matrix.MTRANS_X] = Math.min(0f, Math.max(m[Matrix.MTRANS_X], (-maxTransX).toFloat()))
        else
            m[Matrix.MTRANS_X] = (width / 2 - imageWidth / 2).toFloat()
        if (imageHeight > height)
            m[Matrix.MTRANS_Y] = Math.min(0f, Math.max(m[Matrix.MTRANS_Y], (-maxTransY).toFloat()))
        else
            m[Matrix.MTRANS_Y] = (height / 2 - imageHeight / 2).toFloat()

        matrix.setValues(m)
        return matrix
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        if (drawable == null) return false
        if (isLandscape && !Settings.DUAL_PANE) {
            val imageHeight = computeCurrentImageSize().y.toFloat()
            val offsetY = computeCurrentOffset().y.toFloat()
            if (offsetY >= 0 && direction < 0) {
//                Timber.d("$offsetY >= 0 && $direction < 0")
                onCanNotScrollLeft()
                return false
            } else if (Math.abs(offsetY) + height >= imageHeight && direction > 0) {
//                Timber.d("Math.abs($offsetY) + $height >= $imageHeight && $direction > 0")
                onCanNotScrollRight()
                return false
            }
//            Timber.d("imageHeight[$imageHeight] height[$height] offsetY[$offsetY] direction[$direction]")
        } else {
            val imageWidth = computeCurrentImageSize().x.toFloat()
            val offsetX = computeCurrentOffset().x.toFloat()

            if (offsetX >= 0 && direction < 0) {
//                Timber.d("$offsetX >= 0 && $direction < 0")
                onCanNotScrollLeft()
                return false
            } else if (Math.abs(offsetX) + width >= imageWidth && direction > 0) {
//                Timber.d("Math.abs($offsetX) + $width >= $imageWidth && $direction > 0")
                onCanNotScrollRight()
                return false
            }
//            Timber.d("imageWidth[$imageWidth] width[$width] offsetX[$offsetX] direction[$direction]")
        }
        return true
    }

    private fun onCanNotScrollLeft() {

    }

    private fun onCanNotScrollRight() {

    }

    private inner class ZoomAnimation internal constructor(internal var mX: Float, internal var mY: Float, internal var mScale: Float) : Runnable {
        internal var mInterpolator: Interpolator
        internal var mStartScale: Float = 0.toFloat()
        internal var mStartTime: Long = 0

        init {
            mMatrix.getValues(m)

            mInterpolator = AccelerateDecelerateInterpolator()
            mStartScale = currentScale
            mStartTime = System.currentTimeMillis()
        }

        override fun run() {
            var t = (System.currentTimeMillis() - mStartTime).toFloat() / ZOOM_DURATION
            val interpolateRatio = mInterpolator.getInterpolation(t)
            t = if (t > 1f) 1f else t

            mMatrix.getValues(m)
            val newScale = mStartScale + interpolateRatio * (mScale - mStartScale)
            val newScaleFactor = newScale / m[Matrix.MSCALE_X]

            mMatrix.postScale(newScaleFactor, newScaleFactor, mX, mY)
            imageMatrix = mMatrix

            if (t < 1f) {
                post(this)
            } else {
                // set exact scale
                mMatrix.getValues(m)
                mMatrix.setScale(mScale, mScale)
                mMatrix.postTranslate(m[Matrix.MTRANS_X], m[Matrix.MTRANS_Y])
                imageMatrix = mMatrix
            }
        }
    }

    private enum class PageViewMode {
        ASPECT_FILL,
        ASPECT_FIT,
        FIT_WIDTH
    }

}