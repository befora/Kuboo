package com.sethchhim.kuboo_client.data.task.log

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.data.model.Log
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber

class Task_LogGetAll : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Log>>()

    init {
        executors.diskIO.execute {
            try {
                val result = appDatabaseDao.getAllLog()
                executors.mainThread.execute { liveData.value = result }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}