package com.sethchhim.kuboo_remote.task

import android.accounts.NetworkErrorException
import android.arch.lifecycle.MutableLiveData
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import okio.BufferedSource
import okio.Okio
import timber.log.Timber
import java.io.File
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

class Task_RemoteDownloadFile(val kubooRemote: KubooRemote, val login: Login, val stringUrl: String, val saveDir: File) {

    private val okHttpHelper = kubooRemote.okHttpHelper
    internal val liveData = MutableLiveData<File>()

    init {
        kubooRemote.networkIO.execute {
            try {
                val startTime = System.currentTimeMillis()
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val responseBody = response?.body()
                if (responseBody != null) {
                    val contentLength = responseBody.contentLength()
//                    kubooRemote.mainThread.download { onRemoteDownloadFileUpdate.onStart(contentLength.toInt()) }
                    val source = responseBody.source()

                    val fileName = URL(stringUrl).guessFileName()
                    val saveFilePath = "$saveDir${File.separator}$fileName"

                    val file = File(saveFilePath)
                    if (file.exists()) {
                        if (file.length() >= contentLength) {
                            kubooRemote.mainThread.execute {
                                val stopTime = System.currentTimeMillis()
                                val elapsedTime = stopTime - startTime
                                if (isDebugOkHttp) Timber.i("File already exists. Download cancelled. [$saveFilePath] [$elapsedTime] [$stringUrl] ")
                                liveData.value = file
                            }
                        } else {
                            if (file.length() > 0) {
                                if (isDebugOkHttp) Timber.i("Resuming download. oldSize[${file.length()}] totalSize[$contentLength] [$saveFilePath] [$stringUrl] ")
                                source.skip(file.length())
                            }
                            file.write(source, contentLength, liveData)
                            val stopTime = System.currentTimeMillis()
                            val elapsedTime = stopTime - startTime
                            if (isDebugOkHttp) Timber.i("File download complete! [$saveFilePath] [$elapsedTime] [$stringUrl]")
                        }
                    } else {
                        file.write(source, contentLength, liveData)
                        val stopTime = System.currentTimeMillis()
                        val elapsedTime = stopTime - startTime
                        if (isDebugOkHttp) Timber.i("File download complete! [$saveFilePath] [$elapsedTime] [$stringUrl]")
                    }
                    response.close()
                } else {
                    throw NetworkErrorException()
                }
            } catch (e: SocketTimeoutException) {
                if (isDebugOkHttp) Timber.w("Connection timed out!")
                kubooRemote.mainThread.execute { liveData.value = null }
            } catch (e: MalformedURLException) {
                if (isDebugOkHttp) Timber.e("URL is bad!")
                kubooRemote.mainThread.execute { liveData.value = null }
            } catch (e: Exception) {
                if (isDebugOkHttp) Timber.e("Something went wrong!")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

    private fun File.write(source: BufferedSource, contentLength: Long, fileLiveData: MutableLiveData<File>) {
        try {
            val DOWNLOAD_CHUNK_SIZE = 2048L

            val bufferedSink = Okio.buffer(Okio.sink(this))

            var read = 0
            var totalRead = 0
            while ({ read = source.read(bufferedSink.buffer(), DOWNLOAD_CHUNK_SIZE).toInt(); read }() != -1) {
                totalRead += read
//            val progress = (totalRead * 100 / contentLength).toInt()
//            kubooRemote.mainThread.download { onRemoteDownloadFileUpdate.onUpdate(progress) }
            }

            bufferedSink.writeAll(source)
            bufferedSink.flush()
            bufferedSink.close()
            kubooRemote.mainThread.execute {
                fileLiveData.value = this
            }
        } catch (e: Exception) {
            Timber.e(e)
            kubooRemote.mainThread.execute { fileLiveData.value = null }
        } catch (e: OutOfMemoryError) {
            Timber.e(e)
            kubooRemote.mainThread.execute { fileLiveData.value = null }
        }
    }

}