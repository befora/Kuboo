package com.sethchhim.kuboo_remote.task

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import timber.log.Timber
import java.net.MalformedURLException
import java.net.SocketTimeoutException

class Task_RemoteBookList(kubooRemote: KubooRemote, val login: Login, val stringUrl: String) {

    private val executors = kubooRemote.appExecutors
    private val okHttpHelper = kubooRemote.okHttpHelper
    private val parseService = okHttpHelper.parseService

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        executors.networkIO.execute {
            try {
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val inputStream = response.body()?.byteStream()
                if (response.isSuccessful && inputStream != null) {
                    val inputAsString = inputStream.bufferedReader().use { it.readText() }
                    val result = parseService.parseOpds(login, inputAsString)
                    executors.mainThread.execute { liveData.value = result }
                    inputStream.close()
                    if (isDebugOkHttp) Timber.d("Found remote list: ${result.size} items $stringUrl tag[${call.request().tag()}]")
                } else {
                    executors.mainThread.execute { liveData.value = null }
                }
                response.close()
            } catch (e: SocketTimeoutException) {
                if (isDebugOkHttp) Timber.w("Connection timed out! $stringUrl")
                executors.mainThread.execute { liveData.value = null }
            } catch (e: MalformedURLException) {
                if (isDebugOkHttp) Timber.e("URL is bad! $stringUrl")
                executors.mainThread.execute { liveData.value = null }
            } catch (e: Exception) {
                if (e.message == "Canceled" || e.message == "Socket closed") {
                    if (isDebugOkHttp) Timber.d("Network call canceled. $stringUrl")
                    //no action needed
                } else {
                    e.printStackTrace()
                    if (isDebugOkHttp) Timber.e("Something went wrong! $stringUrl")
                    executors.mainThread.execute { liveData.value = null }
                }
            }
        }
    }

}