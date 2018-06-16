package com.sethchhim.kuboo_remote.task

import android.accounts.NetworkErrorException
import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Neighbors
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import timber.log.Timber
import java.net.MalformedURLException
import java.net.SocketTimeoutException

class Task_RemoteNeighbors(kubooRemote: KubooRemote, login: Login, book: Book, stringUrl: String) {

    internal val liveData = MutableLiveData<Neighbors>()

    private val okHttpHelper = kubooRemote.okHttpHelper
    private val parseService = okHttpHelper.parseService

    init {
        kubooRemote.networkIO.execute {
            try {
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val inputStream = response.body()?.byteStream()
                if (response.isSuccessful && inputStream != null) {
                    val inputAsString = inputStream.bufferedReader().use { it.readText() }
                    val result = parseService.parseNeighbor(login, book, inputAsString)

                    kubooRemote.mainThread.execute {
                        liveData.value = result
                    }
                    inputStream.close()
                } else {
                    throw NetworkErrorException("Response is not successful! $stringUrl")
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