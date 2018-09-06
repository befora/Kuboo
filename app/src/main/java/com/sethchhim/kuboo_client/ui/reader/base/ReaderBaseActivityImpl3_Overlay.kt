package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.fadeGone
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.setGuidePercent
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_local.model.ChapterInfo
import org.jetbrains.anko.sdk25.coroutines.onClick

@SuppressLint("Registered")
open class ReaderBaseActivityImpl3_Overlay : ReaderBaseActivityImpl2_Dialog() {

    protected fun setOverlay(currentPage: Int, totalPages: Int) {
        setOverlayGuideLines()
        setOverlayProgress(currentPage, totalPages)
        setOverlayChapterButton()
        setOverlayImage()
    }

    internal fun showOverlay(isFadeEnabled: Boolean = true) {
        overlayImageView.visible()
        overlayTextView1.visible()
        overlayLayout.apply {
            bringToFront()
            when (isFadeEnabled) {
                true -> fadeVisible()
                false -> visible()
            }
        }

        showStatusBar()
        supportActionBar?.show()

        intent.putExtra(Constants.ARG_OVERLAY, true)
    }

    protected fun hideOverlay(isFadeEnabled: Boolean = true) {
        overlayImageView.gone()
        overlayTextView1.gone()

        when (isFadeEnabled) {
            true -> overlayLayout.fadeGone(200)
            false -> overlayLayout.gone()
        }

        hideStatusBar()
        hideReaderToolbar()

        intent.putExtra(Constants.ARG_OVERLAY, false)
    }

    protected fun hideOverlayHardwareNavigation() = if (isOverlayShown()) hideOverlay() else exitActivity()

    protected fun hideOverlaySoftwareNavigation() {
        if (isOverlayShown()) hideOverlay()
        exitActivity()
    }

    private fun isOverlayShown() = overlayLayout.isShown

    protected fun restoreOverlay() {
        intent.getBooleanExtra(Constants.ARG_OVERLAY, false).apply {
            if (this) showOverlay(isFadeEnabled = false)
        }
    }

    protected fun setOverlayGuideLines() {
        guidelineHorizontal.setGuidePercent(when (systemUtil.isOrientationLandscape()) {
            true -> 0.5F
            false -> 0.3F
        })
        guidelineVertical.setGuidePercent(when (systemUtil.isOrientationLandscape()) {
            true -> 0.15F
            false -> 0.3F
        })
    }

    protected fun setOverlayPageNumberText(progress: Int) {
        overlayPageNumberTextView.text = (progress + 1).toString()
    }

    protected fun setOverlayPosition(position: Int) {
        overlaySeekBar.progress = position
        overlayPageNumberTextView.text = (position + 1).toString()
    }

    protected open fun setOverlayChapterButton() {
        //override in children
    }

    protected open fun setOverlayImage() {
        //override in children
    }

    private fun setOverlayProgress(currentPage: Int, totalPages: Int) {
        if (currentPage == -1 && totalPages == -1) {
            overlayTextLayout.gone()
            overlaySeekBar.gone()
        } else {
            overlayTextView1.typeface = systemUtil.robotoCondensedRegular
            overlayTextView1.text = currentBook.content
            overlayTotalPagesTextView.text = (totalPages).toString()
            overlayTextLayout.visible()
            overlaySeekBar.setLayoutDirection()
            overlaySeekBar.max = totalPages - 1
            overlaySeekBar.progress = currentPage
            overlaySeekBar.visible()
        }
    }

    protected fun ImageView.loadOverlayImage(any: Any) {
        Glide.with(this@ReaderBaseActivityImpl3_Overlay)
                .load(any)
                .apply(RequestOptions()
                        .priority(Priority.LOW)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .dontAnimate()
                        .dontTransform())
                .into(this)
    }

    protected fun handleChapterInfo(chapterInfo: ChapterInfo) {
        if (chapterInfo.containsBookmarks()) {
            val popupMenu = PopupMenu(this, overlayChapterButton)
            chapterInfo.bookmarks.forEachIndexed { index, pair ->
                popupMenu.menu.add(pair.second)
                popupMenu.menu.getItem(index).setOnMenuItemClickListener {
                    onChapterInfoSelected(pair.first)
                    return@setOnMenuItemClickListener true
                }
            }
            overlayChapterButton.onClick {
                popupMenu.show()
            }
            overlayChapterButton.visible()
        } else {
            overlayChapterButton.gone()
        }
    }

    open fun onChapterInfoSelected(position: Int) {
        //override in children
    }

}