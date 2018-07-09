package com.sethchhim.kuboo_client.data.task.recent

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.recentListToBookList
import com.sethchhim.kuboo_client.Extensions.toBook
import com.sethchhim.kuboo_client.Extensions.toRecent
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class Task_RecentInsert(login: Login, book: Book, setTimeAccessed: Boolean) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        if (!book.isBannedFromRecent()) executors.diskIO.execute {
            try {
                if (setTimeAccessed) book.setTimeAccessed()
                when (book.isComic()) {
                    true -> appDatabaseDao.deleteAllThatMatchSeries(book)
                    false -> appDatabaseDao.deleteAllThatMatchBook(book)
                }
                appDatabaseDao.insertRecent(book.toRecent())
                Timber.d("Recent insert: title[${book.title}]  page[${book.currentPage}] bookMark[${book.bookMark}]")
                val result = appDatabaseDao.getAllBookRecent()
                        .filter { it.server == login.server }
                        .sortedByDescending { it.timeAccessed }
                        .recentListToBookList()
                executors.mainThread.execute { liveData.value = result }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] title[${book.title}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatchBook(book: Book) {
        getAllBookRecent()
                .filter { book.isMatch(it.toBook()) }
                .forEach { appDatabaseDao.deleteRecent(it) }
    }

    private fun AppDatabaseDao.deleteAllThatMatchSeries(book: Book) {
        getAllBookRecent()
                .filter { book.isMatchXmlId(it.toBook()) }
                .forEach { appDatabaseDao.deleteRecent(it) }
    }

}