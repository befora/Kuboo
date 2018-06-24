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
    private val REPORTING_INTERVAL = 1000.toLong()

//    private val fetchConfiguration = FetchConfiguration.Builder(context)
//            .setHttpDownloader(OkHttpDownloader(okHttpClient))
//            .setDownloadConcurrentLimit(CONCURRENT_LIMIT)
//            .setProgressReportingInterval(REPORTING_INTERVAL)
//            .setNamespace(NAMESPACE)
//            .build()
//
//    private val fetch = Fetch.getInstance(fetchConfiguration)

    private val fetch = Fetch.Builder(context, NAMESPACE)
            .setDownloader(OkHttpDownloader(okHttpClient))
            .setDownloadConcurrentLimit(CONCURRENT_LIMIT)
            .setProgressReportingInterval(REPORTING_INTERVAL)
            .build()

    private fun onRequestQueueFail(error: com.tonyodev.fetch2.Error) {
        Timber.d("onRequestQueueFail $error")
    }

    private fun onRequestQueueSuccess(download: Download) {
        Timber.d("onRequestQueueSuccess $download")
    }


//    private fun onRequestQueueFail(error: Error) {
//        Timber.d("onRequestQueueFail $error")
//    }
//
//    private fun onRequestQueueSuccess(request: Request) {
//        Timber.d("onRequestQueueSuccess $request")
//    }

    private fun getRequest(login: Login, stringUrl: String, savePath: String, id: String, xmlId: Int): Request {
        return Request(stringUrl, savePath).apply {
            addHeader(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
            this.groupId = xmlId
            this.tag = id
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
                val id = "${it.id}"
                val xmlId = it.getXmlId()
                add(getRequest(login, stringUrl, saveFilePath, id, xmlId))
            }
        }

        requestList.forEach {
            fetch.enqueue(it, object : Func<Download> {
                override fun call(t: Download) {
                    onRequestQueueSuccess(t)
                }
            }, object : Func<com.tonyodev.fetch2.Error> {
                override fun call(t: com.tonyodev.fetch2.Error) {
                    onRequestQueueFail(t)
                }
            })
//            fetch.enqueue(it, { updatedRequest ->
//                onRequestQueueSuccess(updatedRequest)
//            }, { error ->
//                onRequestQueueFail(error)
//            })
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

    internal fun resume(download: Download) = fetch.resume(download.id)

    internal fun resumeAll() = getDownloads().observeForever {
        it?.forEach { download ->
            if (download.status == Status.PAUSED) fetch.resume(download.id)
            else if (download.status == Status.CANCELLED) fetch.retry(download.id)
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

    internal fun deleteGroup(group: Int) = fetch.deleteGroup(group)

    internal fun deleteBefore(book: Book) {
//        fetch.getDownloads {
//
//        }

        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(it: List<Download>) {
                it.forEach {
                    val isMatchSeries = it.group == book.getXmlId()
                    val id = try {
                        it.tag!!.toInt()
                    } catch (e: Exception) {
                        0
                    }
                    val isBefore = id < book.id
                    if (isMatchSeries && isBefore) delete(it)
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

    fun getDownload(download: Download, func2: Func2<Download?>) = fetch.getDownload(download.id, func2)

    fun getDownload(book: Book): MutableLiveData<Download> {
        val liveData = MutableLiveData<Download>()
//        fetch.getDownloads {
//
//        }

        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(it: List<Download>) {
                it.forEach {
                    val isMatchId = it.tag == book.getIdString()
                    val isMatchXmlId = it.group == book.getXmlId()
                    if (isMatchId && isMatchXmlId) {
                        liveData.value = it
                        return@forEach
                    }
                }
            }
        })
        return liveData
    }

    fun getDownload(download: Download): MutableLiveData<Download> {
        val liveData = MutableLiveData<Download>()
//        fetch.getDownloads {
//
//        }

        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(it: List<Download>) {
                it.forEach {
                    val isMatchId = it.id == download.id
                    if (isMatchId) {
                        liveData.value = it
                        return@forEach
                    }
                }
            }
        })
        return liveData
    }

    fun getDownloads(): MutableLiveData<List<Download>> {
        val liveData = MutableLiveData<List<Download>>()
//        fetch.getDownloads {
//
//        }

        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(it: List<Download>) {
                liveData.value = it
            }
        })
        return liveData
    }

}