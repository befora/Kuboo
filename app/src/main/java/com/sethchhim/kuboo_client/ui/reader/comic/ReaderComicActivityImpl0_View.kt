package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.setGuidePercent
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.reader.base.ReaderBaseActivity
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderViewPagerImpl1_Edge
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
open class ReaderComicActivityImpl0_View : ReaderBaseActivity() {

    protected lateinit var viewPager: ReaderViewPagerImpl1_Edge

    protected fun initContentUi() {
        val contentView = layoutInflater.inflate(R.layout.reader_layout_comic_content, null, false)
        contentFrameLayout.addView(contentView)

        viewPager = findViewById(R.id.reader_layout_base_content_readerViewPagerImpl1_Edge)
        viewPager.offscreenPageLimit = getOffScreenPageLimit()
    }

    protected fun onLoadPreviewSuccessFirstRun() {
        supportStartPostponedEnterTransition()
        showEnterTransition()
    }

    protected fun onLoadPreviewSuccessResume() {
        supportStartPostponedEnterTransition()
        previewImageView.isAnimatingTransition = false
        previewImageView.gone()
        showViewPager()
    }

    protected fun onLoadPreviewFail() {
        supportStartPostponedEnterTransition()
        previewImageView.isAnimatingTransition = false
        previewImageView.gone()
        showViewPager()
    }

    protected fun setOverlay(totalPages: Int) {
        overlayTextView1.typeface = systemUtil.robotoCondensedRegular
        overlayTextView1.text = currentBook.content
        overlayTotalPagesTextView.text = (totalPages).toString()
        overlayTextLayout.visible()

        overlaySeekBar.max = totalPages - 1
        overlaySeekBar.progress = viewPager.currentItem
        overlaySeekBar.visible()

        guidelineHorizontal.setGuidePercent(when (systemUtil.isOrientationLandscape()) {
            true -> 0.5F
            false -> 0.3F
        })
        guidelineVertical.setGuidePercent(when (systemUtil.isOrientationLandscape()) {
            true -> 0.15F
            false -> 0.3F
        })
    }

    private fun showEnterTransition() {
        launch(UI) {
            try {
                delay(1200, TimeUnit.MILLISECONDS)
                previewImageView.slideOut()
                showViewPager()
                delay(300)
                previewImageView.isAnimatingTransition = false
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

    private fun showViewPager() {
        forceOrientation()
        viewPager.fadeVisible()
    }

    private fun getOffScreenPageLimit() = when (systemUtil.isOrientationPortrait()) {
        true -> when (isLocal) {
            true -> 4
            false -> 2
        }
        false -> when (isLocal) {
            true -> 2
            false -> 1
        }
    }
}