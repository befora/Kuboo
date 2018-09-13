package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.data.model.GlideEpub

@SuppressLint("Registered")
open class ReaderEpubActivityImpl1_Preview : ReaderEpubActivityImpl0_View() {

    override fun loadLocalPreviewImage() {
        previewImageView.loadPreviewImage(GlideEpub(currentBook, 0, singleInstance = true))
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