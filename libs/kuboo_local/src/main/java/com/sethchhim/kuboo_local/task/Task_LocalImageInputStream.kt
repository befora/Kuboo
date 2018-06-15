package com.sethchhim.kuboo_local.task

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_local.KubooLocal
import timber.log.Timber
import java.io.InputStream

class Task_LocalImageInputStream(kubooLocal: KubooLocal, position: Int) {

    private val executors = kubooLocal.appExecutors
    private val parser = kubooLocal.parser

    internal val liveData = MutableLiveData<InputStream>()

    init {
        try {
            executors.diskIO.execute {
                val startTime = System.currentTimeMillis()
                val result = parser.getPage(position)
                executors.mainThread.execute {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    liveData.value = result
                    Timber.d("Parse image byte array: position[$position] size[${result}] time[$elapsedTime]")
                }
            }
        } catch (e: Exception) {
            Timber.e("message[${e.message}]")
            executors.mainThread.execute { liveData.value = null }
        }
    }
}