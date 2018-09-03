package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_client.Extensions.guessFileName
import com.sethchhim.kuboo_client.data.task.download.*
import com.sethchhim.kuboo_remote.model.Book
import java.io.File
import java.net.URL

class DownloadsRepository {

    internal fun getDownloadNeighbors(book: Book) = Task_DownloadNeighbors(book).liveData

    internal fun getDownloadListLiveData() = Task_DownloadGetLiveData().liveData

    internal fun getDownloadList(favoriteCompressed: Boolean) = when (favoriteCompressed) {
        true -> Task_DownloadGetFavoriteCompressed().liveData
        false -> Task_DownloadGetAll().liveData
    }

    internal fun addDownloads(list: List<Book>, savePath: String) = list.forEach {
        val stringUrl = it.server + it.linkAcquisition
        val fileName = URL(stringUrl).guessFileName()
        it.filePath = "$savePath${File.separator}$fileName"
        addDownload(it)
    }

    internal fun addDownload(book: Book) = Task_DownloadInsert(book).liveData

    internal fun deleteDownload(book: Book) = Task_DownloadDelete(book).liveData

}