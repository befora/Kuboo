package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.FOLDER
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.MEDIA
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class BrowserContentRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    private val gridLayoutManager = BrowserContentGridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, Settings.REVERSE_LAYOUT)
    internal var contentType = FOLDER

    init {
        setHasFixedSize(true)
        layoutManager = gridLayoutManager
        itemAnimator = SlideInUpAnimator().apply { moveDuration = 50 }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val modifiedVelocityY = (velocityY * 0.65).toInt()
        return super.fling(velocityX, modifiedVelocityY)
    }

    internal fun setSpanCountPortrait(browserContentType: BrowserContentType) {
        when (browserContentType) {
            MEDIA -> setSpanCount(3)
            FOLDER -> setSpanCount(1)
        }
        this.contentType = browserContentType
    }

    internal fun setSpanCountLandscape(browserContentType: BrowserContentType) {
        when (browserContentType) {
            MEDIA -> setSpanCount(5)
            FOLDER -> setSpanCount(2)
        }
        this.contentType = browserContentType
    }

    internal fun setSpanCountPortraitHiDpi(browserContentType: BrowserContentType) {
        when (browserContentType) {
            MEDIA -> setSpanCount(4)
            FOLDER -> setSpanCount(2)
        }
        this.contentType = browserContentType
    }

    internal fun setSpanCountLandscapeHiDpi(browserContentType: BrowserContentType) {
        when (browserContentType) {
            MEDIA -> setSpanCount(5)
            FOLDER -> setSpanCount(3)
        }
        this.contentType = browserContentType
    }

    private fun setSpanCount(spanCount: Int) {
        if (gridLayoutManager.spanCount != spanCount) gridLayoutManager.spanCount = spanCount
    }

}