package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.*
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class BrowserContentRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    internal var contentType = FOLDER
    private val gridLayoutManager = BrowserContentGridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, Settings.REVERSE_LAYOUT)
    private val fadeInUpAnimator = FadeInUpAnimator().apply {
        removeDuration = 0
        addDuration = 275
        moveDuration = 275
        changeDuration = 275
        setInterpolator(OvershootInterpolator())
    }

    init {
        setHasFixedSize(true)
        itemAnimator = fadeInUpAnimator
        layoutManager = gridLayoutManager
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val modifiedVelocityY = (velocityY * 0.65).toInt()
        return super.fling(velocityX, modifiedVelocityY)
    }

    internal fun setSpanCountPortrait(browserContentType: BrowserContentType) {
        when (browserContentType) {
            FOLDER -> setSpanCount(1)
            MEDIA_FORCE_LIST -> setSpanCount(1)
            MEDIA -> setSpanCount(3)
        }
        this.contentType = browserContentType
    }

    internal fun setSpanCountLandscape(browserContentType: BrowserContentType) {
        when (browserContentType) {
            FOLDER -> setSpanCount(2)
            MEDIA_FORCE_LIST -> setSpanCount(2)
            MEDIA -> setSpanCount(5)
        }
        this.contentType = browserContentType
    }

    internal fun setSpanCountPortraitHiDpi(browserContentType: BrowserContentType) {
        when (browserContentType) {
            FOLDER -> setSpanCount(1)
            MEDIA_FORCE_LIST -> setSpanCount(1)
            MEDIA -> setSpanCount(4)
        }
        this.contentType = browserContentType
    }

    internal fun setSpanCountLandscapeHiDpi(browserContentType: BrowserContentType) {
        when (browserContentType) {
            FOLDER -> setSpanCount(2)
            MEDIA_FORCE_LIST -> setSpanCount(2)
            MEDIA -> setSpanCount(5)
        }
        this.contentType = browserContentType
    }

    private fun setSpanCount(spanCount: Int) {
        if (gridLayoutManager.spanCount != spanCount) gridLayoutManager.spanCount = spanCount
    }

}