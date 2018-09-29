package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Neighbors
import org.jetbrains.anko.collections.forEachWithIndex
import timber.log.Timber

class Task_DownloadNeighbors(val book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<Neighbors>()

    private val neighbors = Neighbors().apply { currentBook = book }

    init {
        viewModel.getDownloadListLiveData().observeForever {
            it?.let { searchForNeighbors(it.downloadListToBookList()) }
        }
    }

    private fun searchForNeighbors(result: List<Book>) {
        var position = -2

        val downloadList = result.filter { it.getXmlId() == book.getXmlId() }

        downloadList.forEachWithIndex { i, b ->
            if (b.isMatch(book)) {
                position = i
                return@forEachWithIndex
            }
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

        liveData.value = neighbors
    }

}