package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.enum.Source

@SuppressLint("Registered")
open class ReaderComicActivityImpl5_Dialog : ReaderComicActivityImpl4_Content() {

    override fun onSnackBarEndAction() {
        finishBook()
    }

    override fun onSnackBarNextAction() {
        startNextBook()
    }

    override fun startNextBook() {
        super.startNextBook()
        isPreviewEnabled = true

        viewModel.clearReaderLists()
        if (isLocal) viewModel.cleanupParser()

        transitionUrl = nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)
        if (source != Source.RECENT) previewImageView.transitionName = transitionUrl

        showNewIntentTransition()
        startDownloadTracking(nextBook)
    }

    override fun finishBook() {
        super.finishBook()
        startDownloadTracking(currentBook)
        exitActivity()
    }

}