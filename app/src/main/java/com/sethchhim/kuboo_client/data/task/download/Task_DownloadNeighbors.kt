package com.sethchhim.kuboo_client.data.task.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.Extensions.observeOnce
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
        viewModel.getFetchDownloads().observeOnce(Observer { fetchList ->
            viewModel.getDownloadListLiveData().observeOnce(Observer { downloadList ->
                searchForNeighbors(fetchList, downloadList.downloadListToBookList())
            })
        })
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
            val previousFetch = fetchList.singleOrNull { it.url == previousDownload.getAcquisitionUrl() }
            val isPreviousDownloadValid = isFetchValid(previousFetch)
            if (isPreviousDownloadValid) {
                neighbors.previousBook = previousDownload
                Timber.i("Found previousBook! position[$previousPosition] title[${neighbors.previousBook?.title}] isPreviousDownloadValid[$isPreviousDownloadValid]")
            }
        } catch (e: Exception) {
            Timber.e("Failed to find previous download neighbor!")
        }

        try {
            val nextPosition = position + 1
            val nextDownload = list[nextPosition]
            val nextFetch = fetchList.singleOrNull { it.url == nextDownload.getAcquisitionUrl() }
            val isNextDownloadValid = isFetchValid(nextFetch)
            if (isNextDownloadValid) {
                neighbors.nextBook = nextDownload
                Timber.i("Found nextBook! position[$nextPosition] title[${neighbors.nextBook?.title}] isNextDownloadValid[$isNextDownloadValid]")
            }
        } catch (e: Exception) {
            Timber.e("Failed to find next download neighbor!")
        }

        liveData.value = neighbors
    }

    private fun isFetchValid(fetchItem: Download?): Boolean {
        return fetchItem?.status == Status.COMPLETED
    }

}