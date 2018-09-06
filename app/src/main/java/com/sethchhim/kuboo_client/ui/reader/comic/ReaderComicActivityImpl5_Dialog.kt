package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.Temporary

@SuppressLint("Registered")
open class ReaderComicActivityImpl5_Dialog: ReaderComicActivityImpl4_Content() {

    override fun onSnackBarEndAction() {
        finishBook()
    }

    override fun onSnackBarNextAction() {
        startNextBook()
    }

    override fun startNextBook() {
        isPreviewEnabled = true

        Temporary.USER_API_UPDATE_LIST.add(currentBook)
        viewModel.addFinish(currentBook)
        viewModel.clearReaderLists()
        if (isLocal) viewModel.cleanupParser()

        transitionUrl = nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)
        previewImageView.transitionName = transitionUrl

        showNewIntentTransition()
        startDownloadTracking(nextBook)
    }

    override fun finishBook() {
        Temporary.USER_API_UPDATE_LIST.add(currentBook)
        viewModel.addFinish(currentBook)
        startDownloadTracking(currentBook)
        exitActivity()
    }

}