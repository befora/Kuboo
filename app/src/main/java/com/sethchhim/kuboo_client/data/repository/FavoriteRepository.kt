package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_client.data.task.favorite.Task_FavoriteDelete
import com.sethchhim.kuboo_client.data.task.favorite.Task_FavoriteGetAll
import com.sethchhim.kuboo_client.data.task.favorite.Task_FavoriteInsert
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class FavoriteRepository {

    private val favoriteList = mutableListOf<Book>()

    internal fun getFavoriteList() = favoriteList

    internal fun getFavoriteListFromDao() = Task_FavoriteGetAll().liveData

    internal fun getFavoriteItemAt(position: Int): Book? {
        try {
            return favoriteList[position]
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("FavoriteItem not found at position[$position]")
        }
        return null
    }

    internal fun getFavoriteSize() = favoriteList.size

    internal fun addFavorite(book: Book) = Task_FavoriteInsert(book).liveData

    internal fun removeFavorite(book: Book) = Task_FavoriteDelete(book).liveData

    internal fun setFavoriteList(list: List<Book>) {
        favoriteList.clear()
        list.forEach {
            favoriteList.add(it)
        }
    }

    internal fun isFavorite(book: Book): Boolean {
        favoriteList.forEach {
            if (it.isMatch(book)) return true
        }
        return false
    }

}