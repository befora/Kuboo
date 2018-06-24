package com.sethchhim.kuboo_client.data.task.download

import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Neighbors
import org.jetbrains.anko.collections.forEachWithIndex
import timber.log.Timber

class Task_DownloadNeighbors(book: Book) : Task_LocalBase() {

    internal val neighbors = Neighbors().apply { currentBook = book }

    init {
        var position = -2

        val downloadList = viewModel.getDownloadList()
                .filter { it.getXmlId() == book.getXmlId() }
                .sortedBy { it.id }

        downloadList.forEachWithIndex { i, b ->
            if (b.isMatch(book)) {
                position = i
                return@forEachWithIndex
            }
        }

        downloadList.forEach {
            Timber.d(it.title)
        }

        try {
            val previousPosition = position - 1
            val previousDownload = downloadList[previousPosition]
            neighbors.previousBook = previousDownload
            Timber.i("Found previousBook! position[$previousPosition] title[${neighbors.previousBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("Failed to find previous download neighbor!")
        }

        try {
            val nextPosition = position + 1
            val nextDownload = downloadList[nextPosition]
            neighbors.nextBook = nextDownload
            Timber.i("Found nextBook! position[$nextPosition] title[${neighbors.nextBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("Failed to find next download neighbor!")
        }
    }

}