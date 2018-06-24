package com.sethchhim.kuboo_client.data.task.favorite

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.favoriteListToBookList
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_FavoriteDelete(book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                appDatabaseDao.deleteAllThatMatch(book)
                val result = appDatabaseDao.getAllBookFavorite()
                executors.mainThread.execute { liveData.value = result.favoriteListToBookList() }
                Timber.d("Favorite deleteDownload: title[${book.title}] favoriteSize[${result.size}]")
            } catch (e: Exception) {
                Timber.e("message[${e.message}] title[${book.title}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatch(book: Book) {
        getAllBookFavorite().forEach {
            val isMatch = it.id == book.id && it.title == book.title && it.server == book.server
            if (isMatch) appDatabaseDao.deleteFavorite(it)
        }
    }

}


