package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.containsSeries
import com.sethchhim.kuboo_client.Extensions.guessFileName
import com.sethchhim.kuboo_client.data.task.download.*
import com.sethchhim.kuboo_remote.model.Book
import java.io.File
import java.net.URL

class DownloadsRepository {

    internal val downloadsList = mutableListOf<Book>()

    internal fun setDownloadList(newData: List<Book>) {
        downloadsList.clear()
        downloadsList.addAll(newData.sortedBy { it.id })
    }

    internal fun getDownloadNeighbors(book: Book) = Task_DownloadNeighbors(book).neighbors

    internal fun getDownloadsListFromDao() = Task_DownloadGetAll().liveData

    internal fun getDownloadListFavoriteCompressedFromDao() = Task_DownloadGetFavoriteCompressed().liveData

    internal fun getDownloadListFavoriteCompressed(): MutableList<Book> {
        val favoriteCompressedList = mutableListOf<Book>()
        downloadsList
                .sortedWith(compareBy({ it.getXmlId() }, { it.id }))
                .forEach {
                    val isFavorite = it.isFavorite
                    val isNotContainsSeries = !favoriteCompressedList.containsSeries(it.getXmlId())
                    when (isFavorite) {
                        true -> if (isNotContainsSeries) favoriteCompressedList.add(it)
                        false -> favoriteCompressedList.add(it)
                    }
                }
        return favoriteCompressedList
    }

    internal fun addDownloads(list: List<Book>, savePath: String) = list.forEach {
        val stringUrl = it.server + it.linkAcquisition
        val fileName = URL(stringUrl).guessFileName()
        it.filePath = "$savePath${File.separator}$fileName"
        addDownload(it)
    }

    internal fun addDownload(book: Book): MutableLiveData<List<Book>> {
        val liveData = MutableLiveData<List<Book>>()
        Task_DownloadInsert(book).liveData.observeForever {
            it?.let {
                setDownloadList(it)
                liveData.value = getDownloadListFavoriteCompressed()
            }
        }
        return liveData
    }

    internal fun deleteDownload(book: Book, liveData: MutableLiveData<List<Book>>? = null) {
        Task_DownloadDelete(book).liveData.observeForever {
            it?.let {
                setDownloadList(it)
                liveData?.value = getDownloadListFavoriteCompressed()
            }
        }
    }

    internal fun deleteDownloadSeries(book: Book, keepBook: Boolean, liveData: MutableLiveData<List<Book>>) {
        Task_DownloadDeleteSeries(book, keepBook).liveData.observeForever {
            it?.let {
                setDownloadList(it)
                liveData.value = getDownloadListFavoriteCompressed()
            }
        }
    }

    internal fun deleteDownloadsBefore(book: Book) = downloadsList
            .filter {
                val isMatchSeries = it.getXmlId() == book.getXmlId()
                val isBefore = it.id < book.id
                isMatchSeries && isBefore
            }
            .forEach { deleteDownload(it) }

    internal fun getFirstDownloadInSeries(book: Book) = downloadsList
            .filter { it.getXmlId() == book.getXmlId() }
            .sortedBy { it.id }[0]

    internal fun isDownloadContains(book: Book): Boolean {
        downloadsList.forEach {
            if (it.isMatch(book)) return true
        }
        return false
    }

}