package com.sethchhim.kuboo_remote.task

import android.accounts.NetworkErrorException
import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.Settings.isDebugOkHttp
import org.apache.commons.io.input.BoundedInputStream
import timber.log.Timber

class Task_RemoteFirstBook(val kubooRemote: KubooRemote, val login: Login, val book: Book, val stringUrl: String) {

    private val okHttpHelper = kubooRemote.okHttpHelper
    private val parseService = okHttpHelper.parseService

    internal val liveData = MutableLiveData<Book>()

    init {
        kubooRemote.networkIO.execute {
            try {
                val tag = "${javaClass.simpleName}.${book.id}"
                val call = okHttpHelper.getCall(login, stringUrl, tag)
                val response = call.execute()
                val inputStream = response.body()?.byteStream()
                if (response.isSuccessful && inputStream != null) {
                    val boundInputStream = BoundedInputStream(inputStream, 50 * 1024)
                    val inputAsString = boundInputStream.bufferedReader().use { it.readText() }
                    val result = parseService.parseOpds(login, inputAsString, 1)
                    kubooRemote.mainThread.execute {
                        when (result.isEmpty()) {
                            true -> {
                                liveData.value = null
                                if (isDebugOkHttp) Timber.w("Result is empty!")
                            }
                            false -> {
                                liveData.value = result[0]
                            }
                        }
                        boundInputStream.close()
                        inputStream.close()
                    }
                } else {
                    throw NetworkErrorException("Response is not successful!")
                }
                response.close()
            } catch (e: Exception) {
                if (isDebugOkHttp) Timber.e("message[${e.message}] url[$stringUrl]")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        }
    }

}