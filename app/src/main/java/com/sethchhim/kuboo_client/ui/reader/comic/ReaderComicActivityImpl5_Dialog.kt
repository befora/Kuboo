package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.Temporary
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
        isPreviewEnabled = true

        Temporary.USER_API_UPDATE_LIST.add(currentBook)
        viewModel.addFinish(currentBook)
        deleteFinishedDownload(currentBook)
        viewModel.clearReaderLists()
        if (isLocal) viewModel.cleanupParser()

        transitionUrl = nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)
        if (source != Source.RECENT) previewImageView.transitionName = transitionUrl

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