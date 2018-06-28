package com.sethchhim.kuboo_client.data.task.recent

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.recentListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class Task_RecentGetAll(login: Login) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                val result = appDatabaseDao
                        .getAllBookRecent()
                        .recentListToBookList()
                        ?.filter { it.server == login.server }
                        ?.sortedByDescending { it.timeAccessed }
                executors.mainThread.execute { liveData.value = result }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}