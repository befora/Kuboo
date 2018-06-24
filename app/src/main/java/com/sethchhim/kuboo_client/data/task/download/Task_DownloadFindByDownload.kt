package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.tonyodev.fetch2.Download
import timber.log.Timber

class Task_DownloadFindByDownload(download: Download) : Task_LocalBase() {

    internal val liveData = MutableLiveData<com.sethchhim.kuboo_client.data.model.Download>()

    init {
        executors.diskIO.execute {
            try {
                appDatabaseDao.getAllBookDownload().forEach {
                    val isMatch = it.server + it.linkAcquisition == download.url
                    if (isMatch) {
                        Timber.d("Found download: title[${it.title}] url[${download.url}]")
                        it.filePath = download.file
                        executors.mainThread.execute { liveData.value = it }
                        return@forEach
                    } else {
                        executors.mainThread.execute { liveData.value = null }
                    }
                }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] url[${download.url}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}