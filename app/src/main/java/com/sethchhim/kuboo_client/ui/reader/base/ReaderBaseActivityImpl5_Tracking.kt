package com.sethchhim.kuboo_client.ui.reader.base

import com.sethchhim.kuboo_remote.model.Book

open class ReaderBaseActivityImpl5_Tracking : ReaderBaseActivityImpl4_Neighbors() {

    protected fun startDownloadTracking(book: Book) {
        if (isLocal && book.isFavorite) {
            viewModel.deleteDownloadsBefore(book)
            startTrackingByBook(book)
        }
    }

}