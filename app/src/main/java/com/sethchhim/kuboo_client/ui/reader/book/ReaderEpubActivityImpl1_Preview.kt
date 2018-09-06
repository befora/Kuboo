package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Extensions.gone

@SuppressLint("Registered")
open class ReaderEpubActivityImpl1_Preview : ReaderEpubActivityImpl0_View() {

    override fun loadLocalPreviewImage() {
        epubReaderView.getCoverImage()?.let {
            previewImageView.loadPreviewImage(it)
        } ?: onLoadPreviewFail()
    }

    override fun onLoadPreviewSuccessFirstRun() {
        supportStartPostponedEnterTransition()
        showEnterTransition()
    }

    override fun onLoadPreviewFail() {
        supportStartPostponedEnterTransition()
        previewImageView.isAnimatingTransition = false
        previewImageView.gone()
    }

}