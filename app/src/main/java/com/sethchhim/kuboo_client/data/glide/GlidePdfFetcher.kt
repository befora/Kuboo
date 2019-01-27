package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.model.GlidePdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

class GlidePdfFetcher internal constructor(private val glidePdf: GlidePdf) : DataFetcher<InputStream> {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject
    lateinit var viewModel: ViewModel

    lateinit var inputStream: InputStream

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        when (glidePdf.singleInstance) {
            true -> loadSingleInstance(callback)
            false -> loadMultiInstance(callback)
        }
    }

    private fun loadSingleInstance(callback: DataFetcher.DataCallback<in InputStream>) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getPdfImageInputStreamSingleInstance(glidePdf).observeForever { result ->
                handleResult(callback, result)
            }
        }
    }

    private fun loadMultiInstance(callback: DataFetcher.DataCallback<in InputStream>) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getPdfImageInputStream(glidePdf).observeForever { result ->
                handleResult(callback, result)
            }
        }
    }

    private fun handleResult(callback: DataFetcher.DataCallback<in InputStream>, result: InputStream?) {
        if (result != null) {
            inputStream = result
            callback.onDataReady(inputStream)
        } else {
            callback.onLoadFailed(Exception("Failed to load!"))
        }
    }

    override fun cleanup() {
        try {
            inputStream.close()
        } catch (e: Exception) {
        }
    }

    override fun cancel() {
        try {
            inputStream.close()
        } catch (e: Exception) {
        }
    }

    override fun getDataClass() = InputStream::class.java

    override fun getDataSource() = DataSource.LOCAL

}