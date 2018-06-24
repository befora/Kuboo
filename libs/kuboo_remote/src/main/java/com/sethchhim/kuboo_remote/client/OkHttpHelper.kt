package com.sethchhim.kuboo_remote.client

import android.accounts.NetworkErrorException
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.service.remote.ParseService
import com.sethchhim.kuboo_remote.util.Authentication
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Request
import okhttp3.RequestBody
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL

class OkHttpHelper(private val okHttpClient: OkHttpClient) {

    internal val parseService = ParseService()

    @Throws(NetworkErrorException::class, MalformedURLException::class)
    fun getCall(login: Login, stringUrl: String, tag: String, cacheControl: CacheControl? = null): Call {
        val url = URL(stringUrl)
        val request = when (cacheControl == null) {
            true -> Request.Builder()
                    .url(url)
                    .get()
                    .header(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
                    .tag(tag)
                    .build()
            false -> Request.Builder()
                    .url(url)
                    .get()
                    .cacheControl(cacheControl!!)
                    .header(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
                    .tag(tag)
                    .build()
        }
        return okHttpClient.newCall(request)
    }

    @Throws(NetworkErrorException::class, MalformedURLException::class)
    fun putCall(login: Login, stringUrl: String, requestBody: RequestBody): Call {
        val url = URL(stringUrl)
        val request = Request.Builder()
                .url(url)
                .header(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
                .put(requestBody)
                .build()
        return okHttpClient.newCall(request)
    }

    @Throws(NetworkErrorException::class, MalformedURLException::class)
    fun postCall(login: Login, stringUrl: String, requestBody: RequestBody): Call {
        val url = URL(stringUrl)
        val request = Request.Builder()
                .url(url)
                .header(Authentication.getAuthorizationHeaderName(), Authentication.getAuthorizationHeaderValue(login))
                .post(requestBody)
                .build()
        return okHttpClient.newCall(request)
    }

    fun getTlsCipherSuite() = okHttpClient.sslHandshakeInterceptor.tlsCipherSuite

    fun cancelAllByTag(tag: String) {
        val list = mutableListOf<Call>()
        list.addAll(okHttpClient.dispatcher().runningCalls())
        list.addAll(okHttpClient.dispatcher().queuedCalls())
        list.forEach {
            if (it.request().tag() == tag) {
                Timber.d("cancel tag $tag")
                it.cancel()
            }
        }
    }
}