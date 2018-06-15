package com.sethchhim.kuboo_remote.task

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Pagination
import com.sethchhim.kuboo_remote.service.remote.ParseService
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import org.apache.commons.io.input.BoundedInputStream
import timber.log.Timber
import java.net.MalformedURLException
import java.net.SocketTimeoutException

class Task_RemotePagination(val kubooRemote: KubooRemote, val login: Login, val stringUrl: String) {

    private val executors = kubooRemote.appExecutors
    private val okHttpHelper = kubooRemote.okHttpHelper

    internal val liveData = MutableLiveData<Pagination>()

    init {
        executors.networkIO.execute {
            try {
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val inputStream = response.body()?.byteStream()
                if (response.isSuccessful && inputStream != null) {
                    val boundInputStream = BoundedInputStream(inputStream, 8 * 1024)
                    val inputAsString = boundInputStream.bufferedReader().use { it.readText() }
                    val result = ParseService().parsePagination(inputAsString)
                    if (isDebugOkHttp) Timber.w("Result: previous[${result.previous}] next[${result.next}]")
                    executors.mainThread.execute { liveData.value = result }
                    boundInputStream.close()
                    inputStream.close()
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
                if (isDebugOkHttp) Timber.e("Something went wrong! $stringUrl")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}