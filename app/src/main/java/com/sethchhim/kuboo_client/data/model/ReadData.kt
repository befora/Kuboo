package com.sethchhim.kuboo_client.data.model

import android.widget.ImageView
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.ui.base.custom.OnLoadCallback
import com.sethchhim.kuboo_remote.model.Book

data class ReadData(var book: Book,
                    var bookmarksEnabled: Boolean = true,
                    var source: Source,
                    var sharedElement: ImageView? = null,
                    var onLoadCallback: OnLoadCallback? = null)

internal fun ReadData.copyProgress(book: Book?): ReadData {
    book?.let {
        apply {
            this.book.currentPage = book.currentPage
            this.book.totalPages = book.totalPages
            this.book.bookMark = book.bookMark
        }
    }
    return this
}

