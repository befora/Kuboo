package com.sethchhim.kuboo_remote.task

import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login

open class Task_RemoteUserApiBase(val kubooRemote: KubooRemote, val login: Login, val book: Book) {

    protected val okHttpHelper = kubooRemote.okHttpHelper
    protected val stringUrl = getRemoteBookmarkUrl(book)

    private fun getRemoteBookmarkUrl(book: Book): String {
        val stringBookmarkRequestBook = "/user-api/bookmark?isBook=true&docId={ID}"
        val stringBookmarkRequestComic = "/user-api/bookmark?isBook=false&docId={ID}"

        var stringUrl = book.server
        if (stringUrl.contains("/opds-books/")) {
            val bits = stringUrl.split("/opds-books/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            stringUrl = bits[bits.size - 1]
            stringUrl += stringBookmarkRequestBook
        } else if (stringUrl.contains("/opds-comics/")) {
            val bits = stringUrl.split("/opds-comics/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            stringUrl = bits[bits.size - 1]
            stringUrl += stringBookmarkRequestComic
        }

        stringUrl = stringUrl.replace("{ID}", book.id.toString())
        return stringUrl
    }

}