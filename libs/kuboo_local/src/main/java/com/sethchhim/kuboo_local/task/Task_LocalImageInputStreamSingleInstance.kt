package com.sethchhim.kuboo_local.task

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_local.KubooLocal
import timber.log.Timber
import java.io.InputStream

class Task_LocalImageInputStreamSingleInstance(kubooLocal: KubooLocal, filePath: String, position: Int) {

    private val parser = Task_LocalFileParser(filePath).parser

    internal val liveData = MutableLiveData<InputStream>()

    init {
        kubooLocal.diskIO.execute {
            try {
                val startTime = System.currentTimeMillis()
                val result = parser.getPage(position)
                kubooLocal.mainThread.execute {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    Timber.d("Parse image byte array: position[$position] size[$result] time[$elapsedTime] available[${result.available()}]")
                    liveData.value = result
                }
            } catch (e: Exception) {
                Timber.e("message[${e.message}]")
                kubooLocal.mainThread.execute { liveData.value = null }
            } catch (e: OutOfMemoryError) {
                Timber.e("message[${e.message}]")
                kubooLocal.mainThread.execute { liveData.value = null }
            }
        }
    }

}