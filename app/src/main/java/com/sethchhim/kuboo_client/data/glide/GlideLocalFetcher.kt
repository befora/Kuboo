package com.sethchhim.kuboo_client.data.glide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.model.GlideLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject


class GlideLocalFetcher internal constructor(private val glideLocal: GlideLocal) : DataFetcher<InputStream> {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject
    lateinit var viewModel: ViewModel

    lateinit var inputStream: InputStream

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        when (glideLocal.singleInstance) {
            true -> loadSingleInstance(callback)
            false -> loadMultiInstance(callback)
        }
    }

    private fun loadSingleInstance(callback: DataFetcher.DataCallback<in InputStream>) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getLocalImageInputStreamSingleInstance(glideLocal.book.filePath, glideLocal.position).observeForever { result ->
                handleResult(callback, result)
            }
        }
    }

    private fun loadMultiInstance(callback: DataFetcher.DataCallback<in InputStream>) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getLocalImageInputStream(glideLocal.position).observeForever { result ->
                handleResult(callback, result)
            }
        }
    }

    private fun handleResult(callback: DataFetcher.DataCallback<in InputStream>, result: InputStream?) {
        if (result != null) {
            inputStream = result.scaleToTargetWidth()
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

    private fun InputStream.scaleToTargetWidth(): InputStream {
        return if (Settings.MAX_PAGE_WIDTH >= Settings.DEFAULT_MAX_PAGE_WIDTH) {
            this
        } else {
            val bitmap = BitmapFactory.decodeStream(this)
            this.close()
            bitmap
                    .resize(Settings.MAX_PAGE_WIDTH, 9999)
                    .toInputStream()
        }
    }

    private fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
        try {
            if (maxHeight > 0 && maxWidth > 0) {
                val ratioBitmap = width.toFloat() / height.toFloat()
                val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

                var finalWidth = maxWidth
                var finalHeight = maxHeight
                if (ratioMax > ratioBitmap) {
                    finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
                } else {
                    finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
                }
                val newBitmap = Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
                this.recycle()
                return newBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    private fun Bitmap.toInputStream(): InputStream {
        val baos = ByteArrayOutputStream()
        try {
            compress(Bitmap.CompressFormat.JPEG, 100, baos)
            recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val bytes = baos.toByteArray()
        baos.close()
        return ByteArrayInputStream(bytes)

    }

    override fun getDataClass() = InputStream::class.java

    override fun getDataSource() = DataSource.LOCAL

}