package com.sethchhim.kuboo_client.data.task.pdf

import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sethchhim.kuboo_client.data.model.GlidePdf
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream


open class Task_GetPdfImageInputStream(private val document: Document, private val glidePdf: GlidePdf) : Task_LocalBase() {

    internal val liveData = MutableLiveData<InputStream>()

    init {
        executors.diskIO.execute {
            try {
                val page = document.loadPage(glidePdf.position)
                val matrix = AndroidDrawDevice.fitPage(page, glidePdf.width, glidePdf.height)
                val bitmap = AndroidDrawDevice.drawPage(page, matrix)
                bitmap?.let {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, byteArrayOutputStream)
                    val bytes = byteArrayOutputStream.toByteArray()
                    byteArrayOutputStream.close()
                    bitmap.recycle()
                    val byteArrayInputStream = ByteArrayInputStream(bytes)
                    executors.mainThread.execute { liveData.value = byteArrayInputStream }
                }
            } catch (e: Exception) {
                Timber.e(e)
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}

