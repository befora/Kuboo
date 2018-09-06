package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.data.model.GlideLocal
import com.sethchhim.kuboo_local.model.ChapterInfo

@SuppressLint("Registered")
open class ReaderComicActivityImpl2_Overlay : ReaderComicActivityImpl1_Preview() {

    override fun setOverlayImage() {
        when (isDownload()) {
            true -> overlayImageView.loadOverlayImage(GlideLocal(currentBook, 0))
            false -> overlayImageView.loadOverlayImage(currentBook.getPreviewUrl())
        }
    }

    override fun setOverlayChapterButton() {
        val comicInfo = when (isLocal) {
            true -> viewModel.getLocalComicInfo()
            false -> ChapterInfo() //TODO ubooquity server does not support comic info yet
        }
        handleChapterInfo(comicInfo)
    }

    override fun onChapterInfoSelected(position: Int) {
        viewPager.currentItem = position
    }

}