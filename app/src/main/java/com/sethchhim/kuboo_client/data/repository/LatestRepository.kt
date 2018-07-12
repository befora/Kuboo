package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_remote.model.Book

class LatestRepository {

    private val latestList = mutableListOf<Book>()

    internal fun getLatestList() = latestList

    internal fun setLatestList(list: List<Book>) {
        latestList.clear()
        list.forEach {
            latestList.add(it)
        }
    }

}