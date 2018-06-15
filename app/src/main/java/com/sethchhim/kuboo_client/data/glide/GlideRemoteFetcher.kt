package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Synthetic
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Extensions.toGlideUrl
import com.sethchhim.kuboo_client.data.ViewModel
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class GlideRemoteFetcher internal constructor(private val client: Call.Factory, private val url: GlideUrl) : DataFetcher<InputStream> {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var viewModel: ViewModel

    @Synthetic private var stream: InputStream? = null
    @Synthetic private var responseBody: ResponseBody? = null
    @Volatile private var call: Call? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        //basic authentication
        val activeLogin = viewModel.getActiveLogin()
        val stringUrl = url.toStringUrl()
        val authGlideUrl = stringUrl.toGlideUrl(activeLogin)

        //request
        val requestBuilder = Request.Builder().url(authGlideUrl.toStringUrl())
        for ((key, value) in authGlideUrl.headers) {
            requestBuilder.addHeader(key, value)
        }
        val request = requestBuilder.build()

        //call
        call = client.newCall(request)
        call!!.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onLoadFailed(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val contentLength = responseBody!!.contentLength()
                    stream = ContentLengthInputStream.obtain(responseBody!!.byteStream(), contentLength)
                }
                callback.onDataReady(stream)
            }
        })
    }

    override fun cleanup() {
        try {
            call?.cancel()
        } catch (e: IOException) {
        }
    }

    override fun cancel() {
        try {
            call?.cancel()
        } catch (e: IOException) {
        }
    }

    override fun getDataClass() = InputStream::class.java

    override fun getDataSource() = DataSource.REMOTE

}