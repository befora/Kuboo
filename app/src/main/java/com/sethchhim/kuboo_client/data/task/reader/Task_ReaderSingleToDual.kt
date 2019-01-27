package com.sethchhim.kuboo_client.data.task.reader

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Constants.KEY_SINGLE
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.model.Progress
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase

open class Task_ReaderSingleToDual : Task_LocalBase() {

    protected val liveDataProgress = MutableLiveData<Progress>()
    protected val liveDataResult = MutableLiveData<List<PageUrl>>()

    internal val liveDataList = listOf<Any>(liveDataProgress, liveDataResult)

    protected fun MutableList<PageUrl>.processWideList(): MutableList<PageUrl> {
        //sort (again)
        sortBy { it.page0 }

        //process dual pane
        var listSize = size

        for (index in 2..listSize) {
            if (index < listSize) {
                val previousPosition = index - 1
                val previousItem = get(previousPosition)
                val currentItem = get(index)

                val isPreviousSingle = previousItem.page1 == KEY_SINGLE
                val isCurrentSingle = currentItem.page1 == KEY_SINGLE
                if (!isPreviousSingle && !isCurrentSingle) {
                    previousItem.page1 = currentItem.page0
                    remove(currentItem)
                    listSize -= 1
                }
            }
        }

        //quick fix for empty values
        forEach {
            if (it.page1.isEmpty()) it.page1 = KEY_SINGLE
        }

        return this
    }

}

