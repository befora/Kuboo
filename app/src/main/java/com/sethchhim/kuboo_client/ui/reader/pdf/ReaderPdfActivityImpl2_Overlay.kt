package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.data.model.GlidePdf

@SuppressLint("Registered")
open class ReaderPdfActivityImpl2_Overlay : ReaderPdfActivityImpl1_Search() {

    override fun setOverlayImage() {
        when (isDownload()) {
            true -> overlayImageView.loadOverlayImage(GlidePdf(currentBook, 0, systemUtil.getSystemWidth(), systemUtil.getSystemHeight()))
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