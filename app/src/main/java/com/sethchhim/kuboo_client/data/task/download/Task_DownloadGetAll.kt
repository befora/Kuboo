package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_DownloadGetAll : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                val result = appDatabaseDao.getAllBookDownload()
                        .downloadListToBookList()
                val sortedList = result.sortedWith(compareBy({ it.getXmlId() }, { it.id }))
                executors.mainThread.execute { liveData.value = sortedList }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}