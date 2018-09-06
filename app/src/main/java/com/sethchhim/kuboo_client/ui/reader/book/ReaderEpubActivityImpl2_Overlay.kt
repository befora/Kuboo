package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint

@SuppressLint("Registered")
open class ReaderEpubActivityImpl2_Overlay : ReaderEpubActivityImpl1_Preview() {

    override fun setOverlayImage() {
        when (isDownload()) {
            true -> epubReaderView.getCoverImage()?.let { overlayImageView.loadOverlayImage(it) }
            false -> overlayImageView.loadOverlayImage(currentBook.getPreviewUrl())
        }
    }

    override fun setOverlayChapterButton() {
        //TODO
    }

    override fun onChapterInfoSelected(position: Int) {
        //TODO
    }
}