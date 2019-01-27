package com.sethchhim.kuboo_remote.task

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.Constants
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Response
import okhttp3.CacheControl
import timber.log.Timber

class Task_Ping(val kubooRemote: KubooRemote, val login: Login, val stringUrl: String) {

    private val okHttpHelper = kubooRemote.okHttpHelper

    internal val liveData = MutableLiveData<Response>()

    init {
        kubooRemote.networkIO.execute {
            try {
                login.setTimeAccessed()
                val call = okHttpHelper.getCall(login, stringUrl, Constants.KEY_TASK_PING, cacheControl = CacheControl.FORCE_NETWORK)
                val response = call.execute()
                handleResponse(response)
                response.close()
            } catch (e: Exception) {
                val message = e.message
                Timber.e("message[$message] url[$stringUrl]")
                if (message?.toLowerCase() == "socket closed") {
                    //call was cancelled, do nothing
                } else {
                    kubooRemote.mainThread.execute { liveData.value = null }
                }
            }
        }
    }

    private fun handleResponse(response: okhttp3.Response) {
        val responseString = "${response.code()} ${response.message()}"
        Timber.d("response[$responseString] url[$stringUrl]")
        kubooRemote.mainThread.execute { liveData.value = Response(response.code(), response.message(), response.isSuccessful) }
    }

    private fun handleAuthentication() {
        Task_Authenticate(kubooRemote, login).liveData.observeForever { result ->
            when (result) {
                true -> retry()
                false -> kubooRemote.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun retry() {
        Timber.d("retry")
        kubooRemote.networkIO.execute {
            try {
                login.setTimeAccessed()
                val call = okHttpHelper.getCall(login, stringUrl, javaClass.simpleName)
                val response = call.execute()
                handleResponse(response)
                response.close()
            } catch (e: Exception) {
                Timber.e("message[${e.message}] url[$stringUrl]")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        }
    }

}