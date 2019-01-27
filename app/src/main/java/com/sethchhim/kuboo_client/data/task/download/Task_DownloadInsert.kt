package com.sethchhim.kuboo_client.data.task.download

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.Extensions.toDownload
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.model.Download
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_DownloadInsert(book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    private var download = book.toDownload()

    init {
        executors.diskIO.execute {
            try {
                appDatabaseDao.deleteAllThatMatch(download)
                appDatabaseDao.insertDownload(download)
                val result = appDatabaseDao.getAllBookDownload()
                executors.mainThread.execute { liveData.value = result.downloadListToBookList() }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] $download")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatch(download: Download) {
        //deleteDownload all that match same series
        getAllBookDownload().forEach {
            val isMatch = it.id == download.id
            if (isMatch) appDatabaseDao.deleteDownload(it)
        }
    }

}