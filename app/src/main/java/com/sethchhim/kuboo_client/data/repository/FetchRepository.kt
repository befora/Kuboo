package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.service.NotificationService
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.Status
import timber.log.Timber

class FetchRepository(private val kubooRemote: KubooRemote, val notificationService: NotificationService) : FetchListener {

    init {
        kubooRemote.addFetchListener(this)
    }

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
        kubooRemote.getFetchDownloads().observeForever { result ->
            var downloadsCount = 0
            result?.forEach { if (it.status == Status.QUEUED) downloadsCount++ }

            kubooRemote.getFetchDownload(download).observeForever {
                if (it?.status == Status.DOWNLOADING) notificationService.startNotification(download, downloadsCount)
            }
        }
    }

    override fun onQueued(download: Download) {
        Timber.i("onQueued $download")
        //no notification required
    }

//    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
//        Timber.i("onQueued $download")
//        //no notification required
//    }

    override fun onRemoved(download: Download) {
        Timber.i("onRemoved $download")
        notificationService.stopNotification()
    }

    override fun onResumed(download: Download) {
        Timber.i("onResumed $download")
        //no notification required
    }

    internal fun deleteSeries(book: Book, keepBook: Boolean) = kubooRemote.deleteSeries(book, keepBook)

    internal fun deleteDownload(download: Download) = kubooRemote.deleteDownload(download)

    internal fun deleteDownloadsBefore(book: Book) = kubooRemote.deleteDownloadsBefore(book)

    internal fun getDownload(book: Book) = kubooRemote.getFetchDownload(book)

    internal fun getDownloads() = kubooRemote.getFetchDownloads()

    internal fun resumeDownload(download: Download) = kubooRemote.resume(download)

    internal fun retryDownload(download: Download) = kubooRemote.retry(download)

    internal fun startDownloads(login: Login, list: List<Book>, savePath: String) = kubooRemote.startDownloads(login, list, savePath)

}