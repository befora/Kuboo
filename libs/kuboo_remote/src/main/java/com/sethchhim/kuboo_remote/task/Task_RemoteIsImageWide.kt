package com.sethchhim.kuboo_remote.task

import android.arch.lifecycle.MutableLiveData
import android.graphics.BitmapFactory
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber
import java.io.InputStream

class Task_RemoteIsImageWide(kubooRemote: KubooRemote, login: Login, stringUrl: String) {

    private val executors = kubooRemote.appExecutors
    private val okHttpHelper = kubooRemote.okHttpHelper

    internal val liveData = MutableLiveData<Boolean>()

    init {
        executors.networkIO.execute {
            try {
                login.setTimeAccessed()
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                val inputStream = response.body()?.byteStream()
                if (response.isSuccessful && inputStream != null) {
                    val isWide = isBitmapWide(inputStream)
                    inputStream.close()
                    response.close()
                    executors.mainThread.execute { liveData.value = isWide }
                } else {
                    Timber.w("code[${response.code()}] message[${response.message()}] url[$stringUrl]")
                    executors.mainThread.execute { liveData.value = false }
                }
            } catch (e: Exception) {
                Timber.w("message[${e.message}] url[$stringUrl]")
                executors.mainThread.execute { liveData.value = false }
            }
        }
    }

    private fun isBitmapWide(inputStream: InputStream): Boolean {
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, bitmapOptions)
        return bitmapOptions.outWidth >= bitmapOptions.outHeight
    }

}