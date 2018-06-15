package com.sethchhim.kuboo_client.data.task.download

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.Extensions.toBook
import com.sethchhim.kuboo_client.Extensions.toDownload
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_DownloadInsert(book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                book.setTimeAccessed()
                appDatabaseDao.deleteAllThatMatch(book)
                appDatabaseDao.insertDownload(book.toDownload())
                val result = appDatabaseDao.getAllBookDownload()
                executors.mainThread.execute { liveData.value = result.downloadListToBookList() }
                Timber.d("Download insert: title[${book.title}] downloadSize[${result.size}]")
            } catch (e: Exception) {
                Timber.e("message[${e.message}] title[${book.title}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun AppDatabaseDao.deleteAllThatMatch(book: Book) {
        //delete all that match same series
        getAllBookDownload().forEach {
            if (book.isMatch(it.toBook())) {
                Timber.d("Download delete duplicate: title[${it.title}]")
                appDatabaseDao.deleteDownload(it)
            }
        }
    }

}




