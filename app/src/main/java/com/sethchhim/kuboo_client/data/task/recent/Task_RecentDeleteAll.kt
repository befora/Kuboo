package com.sethchhim.kuboo_client.data.task.recent

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber

class Task_RecentDeleteAll : Task_LocalBase() {

    val liveData = MutableLiveData<Boolean>()

    init {
        executors.diskIO.execute {
            try {
                val sizeBefore = viewModel.getRecentSize()
                appDatabaseDao.deleteRecentAll()
                executors.mainThread.execute { liveData.value = true }
                Timber.d("Recent delete all: sizeBefore[$sizeBefore] sizeAfter[${viewModel.getRecentSize()}]")
            } catch (e: Exception) {
                executors.mainThread.execute { liveData.value = false }
                Timber.e("message[${e.message}]")
            }
        }
    }

}