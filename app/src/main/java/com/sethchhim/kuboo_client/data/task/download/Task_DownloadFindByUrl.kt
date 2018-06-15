package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.data.model.Download
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber

class Task_DownloadFindByUrl(stringUrl: String) : Task_LocalBase() {

    internal val liveData = MutableLiveData<Download>()

    init {
        executors.diskIO.execute {
            try {
                appDatabaseDao.getAllBookDownload().forEach {
                    val isMatch = it.server + it.linkAcquisition == stringUrl
                    if (isMatch) {
                        Timber.d("Found download: title[${it.title}] url[$stringUrl]")
                        executors.mainThread.execute { liveData.value = it }
                        return@forEach
                    }
                }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] url[$stringUrl]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}