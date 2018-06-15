package com.sethchhim.kuboo_client.data.task.favorite

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.favoriteListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_FavoriteGetAll : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                val result = appDatabaseDao.getAllBookFavorite()
                executors.mainThread.execute { liveData.value = result.favoriteListToBookList() }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}