package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.service.NotificationService
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.Status
import com.tonyodev.fetch2core.DownloadBlock
import timber.log.Timber
import javax.inject.Inject

class FetchRepository : FetchListener {

    init {
        BaseApplication.appComponent.inject(this)
        kubooRemote.addFetchListener(this)
    }

    @Inject lateinit var downloadsRepository: DownloadsRepository
    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var notificationService: NotificationService
    @Inject lateinit var systemUtil: SystemUtil

    override fun onCancelled(download: Download) {
        Timber.i("onCancelled $download")
        notificationService.cancelProgress()
    }

    override fun onCompleted(download: Download) {
        Timber.i("onCompleted $download")
        notificationService.increaseCompletedCount()
        kubooRemote.isQueueEmpty(MutableLiveData<Boolean>().apply {
            observeForever { result ->
                if (result == true) {
                    notificationService.cancelProgress()
                    notificationService.startCompleted(download)
                }
            }
        })
    }

    override fun onAdded(download: Download) {}

    override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}

    override fun onDeleted(download: Download) {
        Timber.i("onDeleted $download")
        notificationService.cancelProgress()
        downloadsRepository.deleteDownload(download)
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Timber.i("onError $download")
        notificationService.cancelProgress()
    }

    override fun onPaused(download: Download) {
        Timber.i("onPaused $download")
        kubooRemote.isPauseEmpty(MutableLiveData<Boolean>().apply {
            observeForever { result ->
                if (result == false) notificationService.pauseProgress()
            }
        })
    }

    override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
        Timber.i("onProgress $download")
        kubooRemote.getFetchDownloads().observeForever { result ->
            var downloadsCount = 0
            result?.forEach { if (it.status == Status.QUEUED) downloadsCount++ }

            kubooRemote.getFetchDownload(download).observeForever {
                if (it?.status == Status.DOWNLOADING) notificationService.startProgress(download, downloadsCount)
            }
        }
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Timber.i("onQueued $download")
        //no notification required
    }

    override fun onRemoved(download: Download) {
        Timber.i("onRemoved $download")
        notificationService.cancelProgress()
    }

    override fun onResumed(download: Download) {
        Timber.i("onResumed $download")
        //no notification required
    }

    override fun onWaitingNetwork(download: Download) {}

    override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}


    internal fun deleteDownload(download: Download) = kubooRemote.deleteDownload(download)

    internal fun deleteDownload(book: Book) = kubooRemote.deleteDownload(book)

    internal fun deleteSeries(book: Book, keepBook: Boolean) = kubooRemote.deleteSeries(book, keepBook)

    internal fun deleteFetchDownloadsNotInList(doNotDeleteList: MutableList<Book>) = kubooRemote.deleteFetchDownloadsNotInList(doNotDeleteList)

    internal fun getDownload(book: Book) = kubooRemote.getFetchDownload(book)

    internal fun getDownloads() = kubooRemote.getFetchDownloads()

    internal fun resumeDownload(download: Download): Any = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.resume(download)
        false -> Timber.w("Network is not allowed! wifiOnly[${Settings.WIFI_ONLY}] isWifiEnabled[${systemUtil.isWifiEnabled()}]")
    }

    internal fun retryDownload(download: Download): Any = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.retry(download)
        false -> Timber.w("Network is not allowed! wifiOnly[${Settings.WIFI_ONLY}] isWifiEnabled[${systemUtil.isWifiEnabled()}]")
    }

    internal fun startDownloads(login: Login, list: List<Book>, savePath: String) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.startDownloads(login, list, savePath)
        false -> Timber.w("Network is not allowed! wifiOnly[${Settings.WIFI_ONLY}] isWifiEnabled[${systemUtil.isWifiEnabled()}]")
    }

}