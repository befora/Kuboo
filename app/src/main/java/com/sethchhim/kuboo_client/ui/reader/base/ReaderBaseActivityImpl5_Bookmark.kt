package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Temporary
import java.lang.Exception

@SuppressLint("Registered")
open class ReaderBaseActivityImpl5_Bookmark : ReaderBaseActivityImpl4_Content() {

    protected fun saveEpubBookmark(chapterNumber: Int, progressStart: Float) {
        currentBook.bookMark = "$chapterNumber#$progressStart"
        updateBookmark()
    }

    protected fun saveComicBookmark(position: Int) {
        currentBook.currentPage = viewModel.getReaderTrueIndexAt(position)
        intent.putExtra(Constants.ARG_BOOK, currentBook)
        updateBookmark()
    }

    protected fun savePdfBookmark(position: Int) {
        currentBook.currentPage = position
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
        when (isDownload()) {
            true -> viewModel.addDownload(currentBook)
            false -> {
                viewModel.addRecent(currentBook)
                viewModel.putRemoteUserApi(currentBook)
            }
        }

        try {
            var isMatchFound = false
            val iterator = Temporary.USER_API_UPDATE_LIST.listIterator()
            iterator.forEach {
                val isMatch = it.isMatch(currentBook)
                if (isMatch) {
                    isMatchFound = true
                    iterator.set(currentBook)
                }
            }
            if (!isMatchFound) Temporary.USER_API_UPDATE_LIST.add(currentBook)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}