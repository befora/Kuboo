package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.data.task.download.*
import com.sethchhim.kuboo_client.service.NotificationService
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.Status
import timber.log.Timber

class DownloadsRepository(private val kubooRemote: KubooRemote, val notificationService: NotificationService) {

    init {
        kubooRemote.addFetchListener(getFetchListener())
    }

    private val downloadsList = mutableListOf<Download>()

    internal fun getNeighbors(book: Book) = Task_DownloadNeighbors(book).liveData

    internal fun getDownloadList() = downloadsList

    internal fun getDownloadListFromKubooService(liveData: MutableLiveData<List<Download>>) = kubooRemote.getDownloadsList(liveData)

    internal fun getDownloadsListFromAppDatabase() = Task_DownloadGetAll().liveData

    internal fun getDownloadAt(position: Int) = downloadsList[position]

    internal fun getDownloadBookByUrl(stringUrl: String) = Task_DownloadFindByUrl(stringUrl).liveData

    internal fun getDownloadsSize() = downloadsList.size

    internal fun addDownloads(login: Login, list: List<Book>) = list.forEach {
        Task_DownloadInsert(it)
        addDownload(login, it)
    }

    private fun addDownload(login: Login, book: Book) = kubooRemote.download(login, book.server + book.linkAcquisition)

    internal fun deleteDownload(download: Download) {
        Task_DownloadDelete(download)
        kubooRemote.delete(download)
    }

    internal fun removeDownloads(download: Download) = kubooRemote.cancel(download)

    internal fun setDownloadList(list: List<Download>) {
        downloadsList.clear()
        downloadsList.addAll(list)
    }

    internal fun isDownloadsQueuedEmpty(): Boolean {
        var listCount = 0
        downloadsList.forEach {
            val isQueued = it.status == Status.QUEUED
            val isDownloading = it.status == Status.DOWNLOADING
            if (isQueued || isDownloading) listCount += 1
        }
        Timber.d("Queued Count: $listCount")
        return listCount > 0
    }

    internal fun isDownloadsPausedEmpty(): Boolean {
        var listCount = 0
        downloadsList.forEach {
            val isQueued = it.status == Status.QUEUED
            val isDownloading = it.status == Status.DOWNLOADING
            val isPaused = it.status == Status.PAUSED
            if (isQueued || isDownloading || isPaused) listCount += 1
        }
        return listCount > 0
    }

    private fun getFetchListener() = object : FetchListener {
        override fun onCancelled(download: Download) {
            Timber.i("onCancelled $download")
            notificationService.stopNotification()
        }

        override fun onCompleted(download: Download) {
            Timber.i("onCompleted $download")
            kubooRemote.isQueueEmpty(MutableLiveData<Boolean>().apply {
                observeForever { result ->
                    if (result == true) notificationService.stopNotification()
                }
            })
        }

        override fun onDeleted(download: Download) {
            Timber.i("onDeleted $download")
            notificationService.stopNotification()
        }

        override fun onError(download: Download) {
            Timber.i("onError $download")
            //TODO notification.showError
        }

        override fun onPaused(download: Download) {
            Timber.i("onPaused $download")
            kubooRemote.isPauseEmpty(MutableLiveData<Boolean>().apply {
                observeForever { result ->
                    if (result == false) notificationService.pauseNotification()
                }
            })
        }

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            Timber.i("onProgress $download")
            kubooRemote.getDownloadsList(MutableLiveData<List<Download>>().apply {
                observeForever { result ->
                    var downloadsCount = 0
                    result?.forEach { if (it.status == Status.QUEUED) downloadsCount++ }
                    notificationService.startNotification(download, downloadsCount)
                }
            })
        }

        override fun onQueued(download: Download) {
            Timber.i("onQueued $download")
            //no notification required
        }

        override fun onRemoved(download: Download) {
            Timber.i("onRemoved $download")
            notificationService.stopNotification()
        }

        override fun onResumed(download: Download) {
            Timber.i("onResumed $download")
            //no notification required
        }
    }

    fun getDownloadByBook(book: Book): Download? {
        downloadsList.forEach {
            if (it.url == book.server + book.linkAcquisition) return it
        }
        return null
    }

    fun isDownloadContains(book: Book): Boolean {
        downloadsList.forEach {
            if (it.url == book.server + book.linkAcquisition) return true
        }
        return false
    }

}