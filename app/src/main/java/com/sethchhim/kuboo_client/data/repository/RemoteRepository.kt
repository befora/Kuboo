package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_client.Constants.URL_PATH_LATEST
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import java.io.File

class RemoteRepository(private val kubooRemote: KubooRemote) {

    //common
    internal fun pingServer(login: Login, stringUrl: String) = kubooRemote.pingServer(login, stringUrl)

    internal fun getListByBook(login: Login, book: Book) = kubooRemote.getListByUrl(login, login.server + book.linkSubsection)

    internal fun getListByUrl(login: Login, stringUrl: String) = kubooRemote.getListByUrl(login, stringUrl)

    internal fun getListByQuery(login: Login, stringQuery: String) = kubooRemote.getListByQuery(login, stringQuery)

    internal fun getTlsCipherSuite() = kubooRemote.getTlsCipherSuite()

    internal fun isConnectedEncrypted() = kubooRemote.isConnectedEncrypted()

    internal fun cancelAllNetworkCallsByTag(tag: String) = kubooRemote.cancelAllByTag(tag)

    //latest
    private val latestList = mutableListOf<Book>()

    internal fun syncLatestList(list: List<Book>) {
        latestList.clear()
        latestList.addAll(list)
    }

    internal fun getLatestList(login: Login) = kubooRemote.getListByUrl(login, login.server + URL_PATH_LATEST)

    internal fun getPaginationByBook(login: Login, book: Book) = kubooRemote.getPaginationByBook(login, login.server + book.linkSubsection)

    internal fun getFirstByBook(login: Login, book: Book) = kubooRemote.getFirstByBook(login, book, login.server + book.linkSubsection)

    internal fun getItemCountByBook(login: Login, book: Book) = kubooRemote.getItemCountByBook(login, login.server + book.linkSubsection)

    internal fun getNeighbors(login: Login, book: Book, stringUrl: String) = kubooRemote.getNeighbors(login, book, stringUrl)

    internal fun getFile(login: Login, stringUrl: String, saveDir: File) = kubooRemote.getFile(login, login.server + stringUrl, saveDir)


    //bookmark
    internal fun getRemoteUserApi(login: Login, book: Book) = kubooRemote.getRemoteUserApi(login, book)

    internal fun putRemoteUserApi(login: Login, book: Book) = kubooRemote.putRemoteUserApi(login, book)

    internal fun addFinishedToRemoteUserApi(login: Login, book: Book) = kubooRemote.addFinishedToRemoteUserApi(login, book)

    internal fun addFinishedToRemoteUserApi(login: Login, list: List<Book>) = kubooRemote.addFinishedToRemoteUserApi(login, list)

    internal fun removeFinishedFromRemoteUserApi(login: Login, list: List<Book>) = kubooRemote.removeFinishedFromRemoteUserApi(login, list)

}