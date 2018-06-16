package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.tonyodev.fetch2.Download
import timber.log.Timber

class Task_DownloadDelete(download: Download) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                appDatabaseDao.deleteAllThatMatch(download)
                val result = appDatabaseDao.getAllBookDownload()
                executors.mainThread.execute { liveData.value = result.downloadListToBookList() }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] url[${download.url}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatch(download: Download) {
        getAllBookDownload().forEach {
            val isMatch = it.server + it.linkAcquisition == download.url
            if (isMatch) {
                appDatabaseDao.deleteDownload(it)
                Timber.d("Successfully deleted download: url[${it.linkAcquisition}]")
            }
        }
    }

}


