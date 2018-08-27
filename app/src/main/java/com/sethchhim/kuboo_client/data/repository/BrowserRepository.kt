package com.sethchhim.kuboo_client.data.repository

import android.os.Parcelable
import com.sethchhim.kuboo_client.Extensions.isFileType
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.model.Browser
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class BrowserRepository {

    //content
    internal var contentList = mutableListOf<Browser>()

    internal fun setBrowserContentList(list: List<Book>) {
        contentList.clear()
        list.forEach {
            when (it.isFileType()) {
                true -> when (Settings.BROWSER_MEDIA_FORCE_LIST) {
                    true -> contentList.add(Browser(Browser.MEDIA_FORCE_LIST, it))
                    false -> contentList.add(Browser(Browser.MEDIA, it))
                }
                false -> contentList.add(Browser(Browser.FOLDER, it))
            }
        }
    }

    internal fun getBrowserContentItemAt(position: Int): Browser? {
        try {
            return contentList[position]
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("BrowserContentItemAt not found at position[$position]")
        }
        return null
    }

    internal fun clearContentList() = contentList.clear()

    //state
    private val stateMapByLinkSubsection = mutableMapOf<String, Parcelable>()

    internal fun saveRecyclerViewState(book: Book, state: Parcelable) = stateMapByLinkSubsection.put(book.linkSubsection, state)

    internal fun loadRecyclerViewState(book: Book) = stateMapByLinkSubsection[book.linkSubsection]

    //path
    private val pathList = mutableListOf<Book>()

    private var pathPosition = 0

    internal fun getPathList() = pathList

    internal fun getPathPosition() = pathPosition

    internal fun getPathSize() = pathList.size

    internal fun getCurrentBook() = if (pathList.isNotEmpty()) pathList[pathPosition - 1] else null

    internal fun getPreviousBook() = if (pathPosition > 1) pathList[pathPosition - 2] else null

    internal fun setPathPosition(position: Int) {
        pathPosition = position
    }

    internal fun addPath(book: Book) {
        val pathPosition = getPathPosition()
        val pathSize = getPathSize()
        if (pathSize > 0 && pathPosition < pathSize) {
            trimPathAt(pathPosition)
        }
        increasePathPosition()
        pathList.add(book)
        Timber.i("Path added: title[${book.title}] pathSize[${getPathSize()}]")
    }

    internal fun clearPathList() {
        pathPosition = 0
        pathList.clear()
    }

    internal fun decreasePathPosition() {
        pathPosition -= 1
    }

    private fun increasePathPosition() {
        pathPosition += 1
    }

    internal fun updatePathLinkSubsection(book: Book) {
        pathList.forEachIndexed { index, it ->
            if (it.isMatch(book)) {
                pathList[index] = it.apply { linkSubsection = book.linkSubsection }
            }
        }
    }

    internal fun isPathListEmpty() = pathList.isEmpty()

    private fun trimPathAt(position: Int) = pathList.subList(position, pathList.size).clear()

    //selected
    private val selectedList = mutableListOf<Book>()

    internal fun getSelectedList() = selectedList

    internal fun getSelectedAt(position: Int): Book? = try {
        selectedList[position]
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    internal fun getSelectedListSize() = selectedList.size

    internal fun addSelected(book: Book) {
        selectedList.add(book)
        Timber.i("Selected licenseList add ${book.title}")
    }

    internal fun removeSelected(book: Book) {
        val removeList = mutableListOf<Book>()
        selectedList.apply {
            forEach {
                if (it.isMatch(book)) {
                    removeList.add(it)
                    Timber.i("Selected licenseList remove ${book.title}")
                }
            }
            removeAll(removeList)
        }
    }

    internal fun clearSelectedList() {
        selectedList.clear()
        Timber.i("Selected licenseList clear")
    }

    internal fun isSelected(book: Book): Boolean {
        selectedList.forEach {
            if (it.isMatch(book)) return true
        }
        return false
    }

    internal fun isSelectedListEmpty() = selectedList.isEmpty()
    fun updateBrowserItem(book: Book) {
        contentList.forEach {
            if (it.book.isMatch(book)) {
                it.book.isFinished = book.isFinished
            }
        }
    }

}