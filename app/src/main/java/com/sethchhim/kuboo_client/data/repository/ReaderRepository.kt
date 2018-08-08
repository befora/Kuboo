package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.LifecycleOwner
import com.sethchhim.kuboo_client.Constants.KEY_SINGLE
import com.sethchhim.kuboo_client.Extensions.printPageUrls
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.model.Dimension
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.task.reader.Task_ReaderSingleToDualLocal
import com.sethchhim.kuboo_client.data.task.reader.Task_ReaderSingleToDualRemote
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_local.KubooLocal
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class ReaderRepository(private val systemUtil: SystemUtil, private val kubooLocal: KubooLocal, private val kubooRemote: KubooRemote) {

    private val readerList = mutableListOf<PageUrl>()

    internal fun printReaderList() = readerList.printPageUrls()

    internal fun getReaderItemAt(position: Int): PageUrl? {
        try {
            return readerList[position]
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("Reader item not found at position[$position]")
        }
        return null
    }

    internal fun getReaderListSize() = readerList.size

    internal fun getReaderTrueIndexAt(position: Int): Int {
        val url = readerList.getUrlAt(position)
        return singlePaneList.getPositionByUrl(url)
    }

    internal fun getReaderPositionByTrueIndex(position: Int): Int {
        val url = singlePaneList.getUrlAt(position)
        return readerList.getPositionByUrl(url)
    }

    internal fun setReaderListType() = when (Settings.DUAL_PANE) {
        true -> setReaderList(dualPaneList.sortByRtl())
        false -> setReaderList(singlePaneList)
    }

    private fun MutableList<PageUrl>.sortByRtl(): MutableList<PageUrl> {
        //rtl mode needs to switch page positions if non-single
        forEach {
            if (it.page1 != KEY_SINGLE) {
                val pageUrlList = mutableListOf<String>()
                pageUrlList.add(it.page0)
                pageUrlList.add(it.page1)

                when (Settings.RTL) {
                    true -> pageUrlList.sortByDescending { it }
                    false -> pageUrlList.sortBy { it }
                }

                it.page0 = pageUrlList[0]
                it.page1 = pageUrlList[1]
            }
        }
        return this
    }

    internal fun clearReaderLists() {
        singlePaneList.clear()
        dualPaneList.clear()
    }

    private fun setReaderList(list: List<PageUrl>) {
        readerList.clear()
        readerList.addAll(list)
        dimensionList.clear()

        val screenWidth = systemUtil.getSystemWidth()
        val screenHeight = systemUtil.getSystemHeight()
        for (index in 0..readerList.size) {
            dimensionList.add(Dimension(screenWidth, screenHeight))
        }
    }

    //remote reader licenseList
    internal fun createRemoteList(book: Book): List<PageUrl> {
        val bookList = mutableListOf<PageUrl>()
        book.apply {
            for (index in 0 until totalPages) {
                val stringUrl = server + getPse(Settings.MAX_PAGE_WIDTH, index)
                bookList.add(PageUrl(index, stringUrl, ""))
            }
        }
        return bookList.format()
    }

    //local reader licenseList
    internal fun createLocalList(book: Book): MutableList<PageUrl> {
        val parser = kubooLocal.initParser(book.filePath)

        val bookList = mutableListOf<PageUrl>()
        val numPages = parser.numPages()
        for (index in 0 until numPages) {
            bookList.add(PageUrl(index, index.toString(), ""))
        }
        Timber.i("Local reader licenseList created: size[${bookList.size}]")
        return bookList.format()
    }

    //single pane
    private val singlePaneList = mutableListOf<PageUrl>()

    internal fun getSinglePaneList() = singlePaneList

    internal fun setSinglePaneList(list: List<PageUrl>) {
        singlePaneList.clear()
        singlePaneList.addAll(list)
    }

    internal fun singleToDualLocal(list: List<PageUrl>) = Task_ReaderSingleToDualLocal(list).liveDataList

    internal fun singleToDualRemote(lifecycleOwner: LifecycleOwner, list: List<PageUrl>) = Task_ReaderSingleToDualRemote(lifecycleOwner, kubooRemote, list).liveDataList

    //dual pane
    private val dualPaneList = mutableListOf<PageUrl>()

    internal fun setDualPaneList(list: List<PageUrl>) {
        dualPaneList.clear()
        dualPaneList.addAll(list)
    }

    internal fun isReaderDualPaneListEmpty() = dualPaneList.isEmpty()

    private fun MutableList<PageUrl>.format(): MutableList<PageUrl> {
        //fix zeros to single digit page numbers
        forEach {
            for (index in 0..9) {

                //remote formatting
                it.page0 = it.page0.replace("?page=$index&width=", "?page=0$index&width=")

                //local formatting
                it.page0 = when (it.page0) {
                    "0" -> "00"
                    "1" -> "01"
                    "2" -> "02"
                    "3" -> "03"
                    "4" -> "04"
                    "5" -> "05"
                    "6" -> "06"
                    "7" -> "07"
                    "8" -> "08"
                    "9" -> "09"
                    else -> it.page0
                }
            }
        }

        //sort
        sortedBy { it.index }

        return this
    }

    private fun List<PageUrl>.getUrlAt(position: Int): String {
        forEachIndexed { index, pageUrl ->
            if (index == position) return pageUrl.page0
        }
        Timber.e("Failed to get url at position: position[$position]")
        return ""
    }

    private fun List<PageUrl>.getPositionByUrl(url: String): Int {
        forEachIndexed { index, pageUrl ->
            if (pageUrl.page0 == url || pageUrl.page1 == url) return index
        }
        Timber.e("Failed to get position by url: url[$url]")
        return 0
    }

    //dimension
    private val dimensionList = mutableListOf<Dimension>()

    fun setReaderDimension(position: Int, dimension: Dimension) {
        try {
            dimensionList[position] = dimension
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

    fun getReaderDimensionAt(position: Int): Dimension? {
        try {
            return dimensionList[position]
        } catch (e: Exception) {
            Timber.e(e.message)
        }
        return null
    }

}


