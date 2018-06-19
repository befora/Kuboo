package com.sethchhim.kuboo_remote.service.remote

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.sethchhim.kuboo_remote.client.OkHttpClient
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Authentication
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2downloaders.OkHttpDownloader
import timber.log.Timber
import java.io.File
import java.net.URL
import java.util.concurrent.Executor

class FetchService(val context: Context, okHttpClient: OkHttpClient, val mainThread: Executor) {

    private val CONCURRENT_LIMIT = 1
    private val NAMESPACE = "kuboo_fetch"
    private val GROUP_ID = 12345
    private val REPORTING_INTERVAL = 1000.toLong()
    var DOWNLOAD_SAVE_PATH = ""

    private val fetch: Fetch = Fetch.Builder(context, NAMESPACE)
            .setDownloader(OkHttpDownloader(okHttpClient))
            .setDownloadConcurrentLimit(CONCURRENT_LIMIT)
            .setProgressReportingInterval(REPORTING_INTERVAL)
            .build()

    private fun onRequestQueueFail(error: Error) {
        Timber.d("onRequestQueueFail ${error.name}")
    }

    private fun onRequestQueueSuccess(download: Download) {
        Timber.d("onRequestQueueSuccess size[${download.url}]")
    }

    private fun getRequest(login: Login, stringUrl: String, savePath: String): Request {
        return Request(stringUrl, savePath).apply {
            addHeader(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
            groupId = GROUP_ID
        }
    }

    private fun getFilePath(stringUrl: String): String {
        val downloadPath = context.filesDir.path
        val fileName = URL(stringUrl).guessFileName()
        val file = File(downloadPath + File.separator + fileName)
        return file.absolutePath ?: ""
    }

    private fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

    private fun Download.print() =
            "id[$id]" +
                    " url[$url]" +
                    " file[$file]" +
                    " group[$group]" +
                    " priority[$priority]" +
                    " downloaded[$downloaded]" +
                    " total[$total]" +
                    " status[$status]" +
                    " error[$error]" +
                    " networkType[$networkType]" +
                    " progress[$progress]" +
                    " created[$created]"

    internal fun download(login: Login, list: List<Book>, savePath: String) {
        val requestList = mutableListOf<Request>().apply {
            list.forEach {
                val stringUrl = it.server + it.linkAcquisition
                val fileName = URL(stringUrl).guessFileName()
                val saveFilePath = "$savePath${File.separator}$fileName"
                add(getRequest(login, stringUrl, saveFilePath))
            }
        }

        requestList.forEach {
            Timber.d("Fetch enqueue ${it.url}")
            fetch.enqueue(it, object : Func<Download> {
                override fun call(t: Download) = onRequestQueueSuccess(t)
            }, object : Func<Error> {
                override fun call(t: Error) = onRequestQueueFail(t)
            })
        }
    }

    internal fun addListener(listener: FetchListener) = fetch.addListener(listener)

    internal fun getDownloadAt(position: Int): MutableLiveData<Download> {
        val liveData = MutableLiveData<Download>()
        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(t: List<Download>) {
                mainThread.execute { liveData.value = t[position] }
            }
        })
        return liveData
    }

    internal fun getDownloadsList(liveData: MutableLiveData<List<Download>>) {
        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(t: List<Download>) {
                mainThread.execute {
                    liveData.value = t
                }
            }
        })
    }

    internal fun resume(download: Download) = fetch.resume(download.id)

    internal fun resumeAll() {
        getDownloadsList(MutableLiveData<List<Download>>().apply {
            observeForever {
                it?.forEach { download ->
                    if (download.status == Status.PAUSED) fetch.resume(download.id)
                    else if (download.status == Status.CANCELLED) fetch.retry(download.id)
                }
            }
        })
    }

    internal fun retry(download: Download) = fetch.retry(download.id)

    internal fun cancel(download: Download) = fetch.cancel(download.id)

    internal fun cancelAll() = fetch.cancelGroup(GROUP_ID)

    internal fun pauseAll() = fetch.pauseGroup(GROUP_ID)

    internal fun remove(download: Download) = fetch.remove(download.id)

    internal fun delete(download: Download) = fetch.delete(download.id)

    internal fun removeListener(fetchListener: FetchListener) = fetch.removeListener(fetchListener)

    internal fun isQueueEmpty(liveData: MutableLiveData<Boolean>) {
        getDownloadsList(MutableLiveData<List<Download>>().apply {
            observeForever { result ->
                var listCount = 0
                result?.forEach {
                    val isQueued = it.status == Status.QUEUED
                    val isDownloading = it.status == Status.DOWNLOADING
                    if (isQueued || isDownloading) listCount += 1
                }
                mainThread.execute { liveData.value = listCount == 0 }
            }
        })
    }

    internal fun isPauseEmpty(liveData: MutableLiveData<Boolean>) {
        getDownloadsList(MutableLiveData<List<Download>>().apply {
            observeForever { result ->
                var listCount = 0
                result?.forEach {
                    val isQueued = it.status == Status.QUEUED
                    val isDownloading = it.status == Status.DOWNLOADING
                    val isPaused = it.status == Status.PAUSED
                    if (isQueued || isDownloading || isPaused) listCount += 1
                }
                mainThread.execute { liveData.value = listCount == 0 }
            }
        })
    }
}
