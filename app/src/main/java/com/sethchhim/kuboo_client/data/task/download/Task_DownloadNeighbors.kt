package com.sethchhim.kuboo_client.data.task.download

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Neighbors
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import org.jetbrains.anko.collections.forEachWithIndex
import timber.log.Timber

class Task_DownloadNeighbors(val book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<Neighbors>()

    private val neighbors = Neighbors().apply { currentBook = book }

    init {
        viewModel.getFetchDownloads().observeForever { fetchList ->
            viewModel.getDownloadListLiveData().observeForever { downloadList ->
                downloadList?.let {
                    searchForNeighbors(fetchList, downloadList.downloadListToBookList())
                }
            }
        }
    }

    private fun searchForNeighbors(fetchList: List<Download>, downloadList: List<Book>) {
        var position = -2

        val list = downloadList.filter { it.getXmlId() == book.getXmlId() }

        list.forEachWithIndex { i, b ->
            if (b.isMatch(book)) {
                position = i
                return@forEachWithIndex
            }
        }

        try {
            val previousPosition = position - 1
            val previousDownload = list[previousPosition]
            val isPreviousDownloadFinished = fetchList.singleOrNull { it.url == previousDownload.getAcquisitionUrl() }?.status == Status.COMPLETED
            if (isPreviousDownloadFinished) {
                neighbors.previousBook = previousDownload
                Timber.i("Found previousBook! position[$previousPosition] title[${neighbors.previousBook?.title}] isPreviousDownloadFinished[$isPreviousDownloadFinished]")
            }
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("Failed to find previous download neighbor!")
        }

        try {
            val nextPosition = position + 1
            val nextDownload = list[nextPosition]
            val isNextDownloadFinished = fetchList.singleOrNull { it.url == nextDownload.getAcquisitionUrl() }?.status == Status.COMPLETED
            if (isNextDownloadFinished) {
                neighbors.nextBook = nextDownload
                Timber.i("Found nextBook! position[$nextPosition] title[${neighbors.nextBook?.title}] isNextDownloadFinished[$isNextDownloadFinished]")
            }
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("Failed to find next download neighbor!")
        }

        liveData.value = neighbors
    }

}