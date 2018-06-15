package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Neighbors
import com.tonyodev.fetch2.Download
import timber.log.Timber

class Task_DownloadNeighbors(book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<Neighbors>()
    private val neighbors = Neighbors().apply { currentBook = book }

    init {
        viewModel.getDownloadList().searchForNeighbors()
    }

    private fun MutableList<Download>.searchForNeighbors(): Neighbors {
        var position = -2
        forEachIndexed { index, it ->
            if (it.id == neighbors.currentBook?.id) position = index
        }

        try {
            val previousPosition = position - 1
            val previousDownload = this[previousPosition]
            //TODO Get previousBook from download object, need to add to fetch library
            neighbors.previousBook = null
            Timber.i("Found previousBook! position[$previousPosition] title[${neighbors.previousBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
        }

        try {
            val nextPosition = position + 1
            val nextDownload = this[nextPosition]
            //TODO Get nextBook from download object, need to add to fetch library
            neighbors.nextBook = null
            Timber.i("Found nextBook! position[$nextPosition] title[${neighbors.nextBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
        }

        return neighbors
    }


}