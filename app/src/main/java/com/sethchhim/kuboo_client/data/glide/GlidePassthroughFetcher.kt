package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.IOException
import java.io.InputStream

class GlidePassthroughFetcher internal constructor(private val inputStream: InputStream) : DataFetcher<InputStream> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        callback.onDataReady(inputStream)
    }

    override fun cleanup() {
        try {
            inputStream.close()
        } catch (e: IOException) {
        }
    }

    override fun cancel() {
        try {
            inputStream.close()
        } catch (e: IOException) {
        }
    }

    override fun getDataClass() = InputStream::class.java

    override fun getDataSource() = DataSource.LOCAL

}