package com.sethchhim.kuboo_client.data.task.reader

import android.graphics.BitmapFactory
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.model.Progress
import java.io.InputStream

class Task_ReaderSingleToDualLocal(list: List<PageUrl>) : Task_ReaderSingleToDual() {

    private val resultList = mutableListOf<PageUrl>()

    init {
        list.forEachIndexed { index, pageUrl ->
            viewModel.getLocalImageInputStream(index).observeForever {
                val isFirst = index == 0
                val isWide = it?.let {
                    val isBitmapWide = isBitmapWide(it)
                    it.close()
                    isBitmapWide
                } ?: false
                if (isFirst || isWide) pageUrl.page1 = Constants.KEY_SINGLE
                resultList.add(pageUrl)

                liveDataProgress.value = Progress(resultList.size, list.size - 1)
                val isEnd = resultList.size == list.size
                if (isEnd) {
                    liveDataResult.value = resultList.processWideList()
                    liveDataProgress.value = Progress(-1, -1)
                }
            }
        }
    }

    private fun isBitmapWide(inputStream: InputStream): Boolean {
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, bitmapOptions)
        return bitmapOptions.outWidth >= bitmapOptions.outHeight
    }
}
