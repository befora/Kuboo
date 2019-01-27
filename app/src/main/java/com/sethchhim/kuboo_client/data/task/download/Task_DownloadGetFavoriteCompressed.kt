package com.sethchhim.kuboo_client.data.task.download

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.containsSeries
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class Task_DownloadGetFavoriteCompressed : Task_LocalBase() {

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.diskIO.execute {
            try {
                val result = appDatabaseDao.getAllBookDownload().downloadListToBookList()
                val favoriteCompressedList = mutableListOf<Book>()
                result.forEach {
                            val isFavorite = it.isFavorite
                            val isNotContainsSeries = !favoriteCompressedList.containsSeries(it.getXmlId())
                            when (isFavorite) {
                                true -> if (isNotContainsSeries) favoriteCompressedList.add(it)
                                false -> favoriteCompressedList.add(it)
                            }
                        }
                val sortedList = favoriteCompressedList.sortedBy { it.getXmlId() }
                executors.mainThread.execute { liveData.value = sortedList }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}