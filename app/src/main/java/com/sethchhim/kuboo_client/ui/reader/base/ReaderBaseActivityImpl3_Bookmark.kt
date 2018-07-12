package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Constants

@SuppressLint("Registered")
open class ReaderBaseActivityImpl3_Bookmark : ReaderBaseActivityImpl2_Content() {

    protected fun saveEpubBookmark(chapterNumber: Int, progressStart: Float) {
        currentBook.bookMark = "$chapterNumber#$progressStart"
        updateBookmark()
    }

    protected fun saveComicBookmark(position: Int) {
        currentBook.currentPage = viewModel.getReaderTrueIndexAt(position)
        intent.putExtra(Constants.ARG_BOOK, currentBook)
        updateBookmark()
    }

    protected fun getChapterFromEpubBookmark(bookmark: String): Int {
        return when (bookmark.contains("#")) {
            true -> bookmark.substringBeforeLast("#").toInt()
            false -> 0
        }
    }

    protected fun getProgressFromEpubBookmark(bookmark: String): Float {
        return when (bookmark.contains("#")) {
            true -> bookmark.substringAfterLast("#").toFloat()
            false -> 0.0F
        }
    }

    private fun updateBookmark() {
        when (isDownload) {
            true -> viewModel.addDownload(currentBook)
            false -> {
                viewModel.addRecent(currentBook)
                viewModel.putRemoteUserApi(currentBook)
            }
        }
    }

}