package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import com.sethchhim.kuboo_remote.model.Book

@SuppressLint("Registered")
open class ReaderBaseActivityImpl6_Tracking : ReaderBaseActivityImpl5_Neighbors() {

    protected fun startDownloadTracking(book: Book) {
        if (isLocal && book.isFavorite) {
            viewModel.deleteFetchDownloadsBefore(book)
            trackingService.startTrackingByBook(viewModel.getActiveLogin(), book)
        }
    }

}