package com.sethchhim.kuboo_remote.service.remote

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.sethchhim.kuboo_remote.BuildConfig
import com.sethchhim.kuboo_remote.client.OkHttpClient
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Authentication
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Func
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import timber.log.Timber
import java.io.File
import java.net.URL
import java.util.concurrent.Executor


class FetchService(val context: Context, okHttpClient: OkHttpClient, val mainThread: Executor) {

    private val CONCURRENT_LIMIT = 1
    private val NAMESPACE = "kuboo_fetch"
    private val NAMESPACE_DEBUG = "kuboo_fetch_debug"
    private val REPORTING_INTERVAL = 800L

    private val fetchConfiguration = FetchConfiguration.Builder(context)
            .setHttpDownloader(OkHttpDownloader(okHttpClient))
            .setDownloadConcurrentLimit(CONCURRENT_LIMIT)
            .setProgressReportingInterval(REPORTING_INTERVAL)
            .setNamespace(if (BuildConfig.DEBUG) NAMESPACE_DEBUG else NAMESPACE)
            .build()

    private val fetch = Fetch.getInstance(fetchConfiguration)

    private fun onRequestQueueFail(error: Error) {
        Timber.d("onRequestQueueFail $error")
    }

    private fun onRequestQueueSuccess(request: Request) {
        Timber.d("onRequestQueueSuccess $request")
    }

    private fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

    internal fun download(login: Login, downloadList: List<Book>, savePath: String) {
        downloadList.forEach {
            val request = createRequest(login, it, savePath)
            deleteMatchingRequestsWithDifferentFileName(request)
            fetch.enqueue(request, Func { r -> onRequestQueueSuccess(r) }, Func { e -> onRequestQueueFail(e) })
        }
    }

    private fun deleteMatchingRequestsWithDifferentFileName(request: Request) {
        getDownloads().observeForever {
            it?.let {
                it.forEach {
                    val isRequestExistWithDifferentFileName = it.url == request.url && it.file != request.file
                    if (isRequestExistWithDifferentFileName) delete(it)
                }
            }
        }
    }

    private fun createRequest(login: Login, book: Book, savePath: String): Request {
        val stringUrl = book.server + book.linkAcquisition
        val stringFileName = URL(stringUrl).guessFileName()
        val stringSavePath = "$savePath${File.separator}$stringFileName"
        return Request(stringUrl, stringSavePath).apply {
            enqueueAction = EnqueueAction.DO_NOT_ENQUEUE_IF_EXISTING
            addHeader(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
            this.groupId = book.getXmlId()
            this.tag = "${book.id}"
        }
    }

    internal fun addListener(listener: FetchListener) = fetch.addListener(listener)

    internal fun resume(download: Download) = fetch.resume(download.id)

    internal fun resumeAll() = getDownloads().observeForever { result ->
        result?.forEach { download ->
            when {
                download.status == Status.PAUSED -> fetch.resume(download.id)
                download.status == Status.CANCELLED -> fetch.retry(download.id)
                download.status == Status.FAILED -> fetch.retry(download.id)
            }
        }
    }


    internal fun retry(download: Download) = fetch.retry(download.id)

    internal fun cancel(download: Download) = fetch.cancel(download.id)

    internal fun cancelAll() = fetch.cancelAll()

    internal fun pauseAll() = getDownloads().observeForever {
        it?.forEach { download ->
            fetch.pause(download.id)
        }
    }

    internal fun remove(download: Download) = fetch.remove(download.id)

    internal fun pause(download: Download) = fetch.pause(download.id)

    internal fun pauseGroup(group: Int) = fetch.pauseGroup(group)

    internal fun delete(download: Download) = fetch.delete(download.id)

    internal fun delete(book: Book) = fetch.getDownloads(Func { result ->
        result.forEach {
            val isMatchUrl = it.url == book.getAcquisitionUrl()
            if (isMatchUrl) delete(it)
        }
    })

    internal fun deleteGroup(group: Int) = fetch.deleteGroup(group)

    internal fun deleteNotInList(doNotDeleteList: MutableList<Book>) {
        fetch.getDownloads(Func { fetchDownloads ->
            fetchDownloads
                    .groupBy { it.group }
                    .forEach { entry ->
                        entry.value.forEach { download ->
                            val isDoNotDeleteListValid = doNotDeleteList[0].getXmlId() == entry.key
                            val isContains = doNotDeleteList.any { it.getAcquisitionUrl() == download.url }
                            if (isDoNotDeleteListValid && !isContains) {
                                delete(download)
                            }
                        }
                    }
        })
    }

    internal fun removeListener(fetchListener: FetchListener) = fetch.removeListener(fetchListener)

    internal fun isQueueEmpty(liveData: MutableLiveData<Boolean>) {
        getDownloads().observeForever { result ->
            var listCount = 0
            result?.forEach {
                val isQueued = it.status == Status.QUEUED
                val isDownloading = it.status == Status.DOWNLOADING
                if (isQueued || isDownloading) listCount += 1
            }
            mainThread.execute { liveData.value = listCount == 0 }
        }
    }

    internal fun isPauseEmpty(liveData: MutableLiveData<Boolean>) {
        getDownloads().observeForever { result ->
            var listCount = 0
            result?.forEach {
                val isQueued = it.status == Status.QUEUED
                val isDownloading = it.status == Status.DOWNLOADING
                val isPaused = it.status == Status.PAUSED
                if (isQueued || isDownloading || isPaused) listCount += 1
            }
            mainThread.execute { liveData.value = listCount == 0 }
        }
    }

    fun getDownload(book: Book): MutableLiveData<Download> {
        val liveData = MutableLiveData<Download>()
        fetch.getDownloads(Func { result ->
            result.forEach {
                val isMatchId = it.tag == book.getIdString()
                val isMatchXmlId = it.group == book.getXmlId()
                if (isMatchId && isMatchXmlId) {
                    liveData.value = it
                    return@forEach
                }
            }
        })
        return liveData
    }

    fun getDownload(download: Download): MutableLiveData<Download> {
        val liveData = MutableLiveData<Download>()
        fetch.getDownloads(Func { result ->
            result.forEach {
                val isMatchId = it.id == download.id
                if (isMatchId) {
                    liveData.value = it
                    return@forEach
                }
            }
        })
        return liveData
    }

    fun getDownloads(): MutableLiveData<List<Download>> {
        val liveData = MutableLiveData<List<Download>>()
        fetch.getDownloads(Func { result -> liveData.value = result })
        return liveData
    }

}