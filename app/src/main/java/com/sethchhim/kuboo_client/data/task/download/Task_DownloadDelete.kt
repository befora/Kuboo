package com.sethchhim.kuboo_client.data.task.download

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.Extensions.toDownload
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_DownloadDelete(book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    private val download = book.toDownload()

    init {
        executors.diskIO.execute {
            try {
                appDatabaseDao.getAllBookDownload()
                        .filter { it.id == download.id }
                        .forEach { appDatabaseDao.deleteDownload(it) }
                val result = appDatabaseDao.getAllBookDownload().downloadListToBookList()
                executors.mainThread.execute { liveData.value = result }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] $download")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}