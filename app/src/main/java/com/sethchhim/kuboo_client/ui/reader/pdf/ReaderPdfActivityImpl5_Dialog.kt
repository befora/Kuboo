package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.data.model.ReadData

@SuppressLint("Registered")
open class ReaderPdfActivityImpl5_Dialog : ReaderPdfActivityImpl4_Content() {

    override fun onSnackBarEndAction() {
        finishBook()
    }

    override fun onSnackBarNextAction() {
        startNextBook()
    }

    override fun startNextBook() {
        viewModel.addFinish(currentBook)
        startDownloadTracking(nextBook)
        finish()
        startReader(ReadData(book = nextBook, bookmarksEnabled = false, sharedElement = null, source = source))
    }

    override fun finishBook() {
        viewModel.addFinish(currentBook)
        startDownloadTracking(currentBook)
        exitActivity()
    }

}