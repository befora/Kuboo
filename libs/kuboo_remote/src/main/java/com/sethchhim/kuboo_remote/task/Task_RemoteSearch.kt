package com.sethchhim.kuboo_remote.task

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import timber.log.Timber
import java.net.MalformedURLException
import java.net.SocketTimeoutException

class Task_RemoteSearch(kubooRemote: KubooRemote, val login: Login, val stringQuery: String) {

    private val okHttpHelper = kubooRemote.okHttpHelper
    private val parseService = okHttpHelper.parseService
    private val URL_PATH_SEARCH = "?search=true&searchstring="

    internal val liveData = MutableLiveData<List<Book>>()

    init {
        kubooRemote.networkIO.execute {
            val stringUrl = login.server + URL_PATH_SEARCH + stringQuery
            try {
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val inputStream = response.body()?.byteStream()
                if (response.isSuccessful && inputStream != null) {
                    val inputAsString = inputStream.bufferedReader().use { it.readText() }
                    val result = parseService.parseOpds(login, inputAsString)
                    kubooRemote.mainThread.execute { liveData.value = result }
                    inputStream.close()
                    if (isDebugOkHttp) Timber.d("Found remote list: ${result.size} items $stringUrl")
                } else {
                    kubooRemote.mainThread.execute { liveData.value = null }
                }
                response.close()
            } catch (e: SocketTimeoutException) {
                if (isDebugOkHttp) Timber.w("Connection timed out! $stringUrl")
                kubooRemote.mainThread.execute { liveData.value = null }
            } catch (e: MalformedURLException) {
                if (isDebugOkHttp) Timber.e("URL is bad! $stringUrl")
                kubooRemote.mainThread.execute { liveData.value = null }
            } catch (e: Exception) {
                if (isDebugOkHttp) Timber.e("Something went wrong! $stringUrl")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        }
    }

}