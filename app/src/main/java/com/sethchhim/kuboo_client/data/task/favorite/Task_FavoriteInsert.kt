package com.sethchhim.kuboo_client.data.task.favorite

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.favoriteListToBookList
import com.sethchhim.kuboo_client.Extensions.toBook
import com.sethchhim.kuboo_client.Extensions.toFavorite
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_FavoriteInsert(book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                book.setTimeAccessed()
                appDatabaseDao.deleteAllThatMatch(book)
                appDatabaseDao.insertFavorite(book.toFavorite())
                val result = appDatabaseDao.getAllBookFavorite()
                executors.mainThread.execute { liveData.value = result.favoriteListToBookList() }
                Timber.d("Favorite insert: title[${book.title}] favoriteSize[${result.size}]")
            } catch (e: Exception) {
                Timber.e("message[${e.message}] title[${book.title}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatch(book: Book) {
        //delete all that match same series
        getAllBookFavorite().forEach {
            if (book.isMatch(it.toBook())) {
                Timber.d("Favorite delete duplicate item: title[${it.title}]")
                appDatabaseDao.deleteFavorite(it)
            }
        }
    }

}




