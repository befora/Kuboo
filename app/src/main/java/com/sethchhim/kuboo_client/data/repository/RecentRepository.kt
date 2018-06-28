package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_client.data.task.recent.*
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class RecentRepository {

    private val recentList = mutableListOf<Book>()

    internal fun getRecentList() = recentList

    internal fun getRecentListFromDao(login: Login) = Task_RecentGetAll(login).liveData

    internal fun getRecentItemAt(position: Int): Book? {
        try {
            return recentList[position]
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("RecentItem not found at position[$position]")
        }
        return null
    }

    internal fun getRecentByBook(login: Login?, book: Book) = Task_RecentFindByBook(login, book).liveData

    internal fun getRecentByXmlId(login: Login?, book: Book) = Task_RecentFindByXmlId(login, book).liveData

    internal fun getRecentSize() = recentList.size

    internal fun setRecentList(list: List<Book>) {
        recentList.clear()
        list.forEach {
            recentList.add(it)
        }
    }

    internal fun addRecent(login: Login, book: Book, setTimeAccessed: Boolean) = Task_RecentInsert(login, book, setTimeAccessed).liveData

    internal fun removeRecent(login: Login, book: Book) = Task_RecentDelete(login, book).liveData

    internal fun isActiveServerContains(book: Book): Boolean {
        recentList.forEach {
            if (it.isMatch(book)) return true
        }
        return false
    }

}