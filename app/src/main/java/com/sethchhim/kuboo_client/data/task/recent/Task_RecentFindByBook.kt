package com.sethchhim.kuboo_client.data.task.recent

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.recentListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class Task_RecentFindByBook(login: Login?, book: Book) : Task_LocalBase() {

    internal val liveData = MutableLiveData<Book>()

    init {
        executors.diskIO.execute {
            try {
                val result = appDatabaseDao
                        .getAllBookRecent()
                        .recentListToBookList()

                var resultBook: Book? = null
                if (login != null) {
                    val resultFilteredByActiveServer = mutableListOf<Book>().apply {
                        result?.forEach { if (it.server == login.server) add(it) }
                    }
                    resultFilteredByActiveServer.forEach {
                        if (book.isMatch(it)) {
                            resultBook = it
                            return@forEach
                        }
                    }
                } else {
                    result?.forEach {
                        if (book.isMatch(it)) {
                            resultBook = it
                            return@forEach
                        }
                    }
                }

                executors.mainThread.execute { liveData.value = resultBook }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}