package com.sethchhim.kuboo_client.data.task.reader

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sethchhim.kuboo_client.Constants.KEY_SINGLE
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.model.Progress
import com.sethchhim.kuboo_remote.KubooRemote
import org.jetbrains.anko.collections.forEachWithIndex

class Task_ReaderSingleToDualRemote(lifecycleOwner: LifecycleOwner, kubooRemote: KubooRemote, list: List<PageUrl>) : Task_ReaderSingleToDual() {

    private val resultList = mutableListOf<PageUrl>()
    private val loginItem = viewModel.getActiveLogin()

    init {
        kubooRemote.cancelAllByTag("Task_RemoteIsImageWide")
        list.forEachWithIndex { index, pageUrl ->
            val startIndex = 0
            val lastIndex = pageUrl.page0.lastIndexOf("=") + 1
            val stringUrl = pageUrl.page0.substring(startIndex, lastIndex).plus("10")

            kubooRemote.isImageWide(loginItem, stringUrl).observe(lifecycleOwner, Observer { result ->
                val isFirst = index == 1
                val isWide = result ?: false
                if (isFirst || isWide) pageUrl.page1 = KEY_SINGLE
                resultList.add(pageUrl)

                executors.mainThread.execute { liveDataProgress.value = Progress(resultList.size, list.size - 1) }
                val isEnd = resultList.size == list.size
                if (isEnd) {
                    liveDataResult.value = resultList.processWideList()
                    liveDataProgress.value = Progress(-1, -1)
                }
            })
        }
    }

}

