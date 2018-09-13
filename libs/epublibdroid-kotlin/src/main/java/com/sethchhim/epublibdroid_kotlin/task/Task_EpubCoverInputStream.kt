package com.sethchhim.epublibdroid_kotlin.task

import android.arch.lifecycle.MutableLiveData
import nl.siegmann.epublib.epub.EpubReader
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.Executor

class Task_EpubCoverInputStream(mainThread: Executor, diskIO: Executor, filePath: String) {

    val liveData = MutableLiveData<InputStream>()

    init {
        diskIO.execute {
            try {
                val startTime = System.currentTimeMillis()
                val inputStream = BufferedInputStream(FileInputStream(filePath))
                val book = EpubReader().readEpub(inputStream)
                val result = book.coverImage.inputStream
                mainThread.execute {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    liveData.value = result
                    Timber.d("Parse image byte array: position[$0] size[$result] time[$elapsedTime]")
                }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                mainThread.execute { liveData.value = null }
            } catch (e: OutOfMemoryError) {
                Timber.e("message[${e.message}]")
                mainThread.execute { liveData.value = null }
            }
        }
    }

}