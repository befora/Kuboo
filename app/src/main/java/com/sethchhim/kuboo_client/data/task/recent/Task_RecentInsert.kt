package com.sethchhim.kuboo_client.data.task.recent

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.recentListToBookList
import com.sethchhim.kuboo_client.Extensions.toBook
import com.sethchhim.kuboo_client.Extensions.toRecent
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.model.Recent
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class Task_RecentInsert(login: Login, book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                book.setTimeAccessed()
                if (book.isComic()) appDatabaseDao.deleteAllThatMatchSeries(book) else appDatabaseDao.deleteAllThatMatchBook(book)
                appDatabaseDao.insertRecent(book.toRecent())
                Timber.d("Recent insert: title[${book.title}]  page[${book.currentPage}] bookMark[${book.bookMark}]")
                val result = appDatabaseDao.getAllBookRecent()
                val resultFilteredByActiveServer = mutableListOf<Recent>().apply {
                    result.forEach { if (it.server == login.server) add(it) }
                }
                val resultSortedByDescending = resultFilteredByActiveServer.sortedByDescending { it.timeAccessed }
                executors.mainThread.execute { liveData.value = resultSortedByDescending.recentListToBookList() }
            } catch (e: Exception) {
                Timber.e("message[${e.message}] title[${book.title}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatchSeries(book: Book) {
        //delete all that match same series
        getAllBookRecent().forEach {
            if (book.isMatchXmlId(it.toBook())) {
                Timber.d("Recent delete from same series: title[${it.title}] page[${it.currentPage}] bookMark[${it.bookMark}]")
                appDatabaseDao.deleteRecent(it)
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatchBook(book: Book) {
        //delete all that match same series
        getAllBookRecent().forEach {
            if (book.isMatch(it.toBook())) {
                Timber.d("Recent delete from same book: title[${it.title}] page[${it.currentPage}] bookMark[${it.bookMark}]")
                appDatabaseDao.deleteRecent(it)
            }
        }
    }

}




