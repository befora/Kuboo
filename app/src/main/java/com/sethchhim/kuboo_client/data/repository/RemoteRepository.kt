package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Constants.URL_PATH_LATEST
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class RemoteRepository(private val kubooRemote: KubooRemote, private val systemUtil: SystemUtil) {

    //common
    internal fun pingServer(login: Login, stringUrl: String) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.pingServer(login, stringUrl)
        false -> MutableLiveData<Response>().apply { this.delayedFail() }
    }

    internal fun getListByBook(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getListByUrl(login, login.server + book.linkSubsection)
        false -> MutableLiveData<List<Book>>().apply { this.delayedFail() }
    }

    internal fun getListByUrl(login: Login, stringUrl: String) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getListByUrl(login, stringUrl)
        false -> MutableLiveData<List<Book>>().apply { this.delayedFail() }
    }

    internal fun getListByQuery(login: Login, stringQuery: String) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getListByQuery(login, stringQuery)
        false -> MutableLiveData<List<Book>>().apply { this.delayedFail() }
    }

    internal fun getTlsCipherSuite() = kubooRemote.getTlsCipherSuite()

    internal fun isConnectedEncrypted() = kubooRemote.isConnectedEncrypted()

    internal fun cancelAllNetworkCallsByTag(tag: String) = kubooRemote.cancelAllByTag(tag)

    //latest
    private val latestList = mutableListOf<Book>()

    internal fun syncLatestList(list: List<Book>) {
        latestList.clear()
        latestList.addAll(list)
    }

    internal fun getLatestList(login: Login) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getListByUrl(login, login.server + URL_PATH_LATEST)
        false -> MutableLiveData<List<Book>>().apply { this.delayedFail() }
    }

    internal fun getPaginationByBook(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getPaginationByBook(login, login.server + book.linkSubsection)
        false -> MutableLiveData<Pagination>().apply { this.delayedFail() }
    }

    internal fun getFirstByBook(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getFirstByBook(login, book, login.server + book.linkSubsection)
        false -> MutableLiveData<Book>().apply { this.delayedFail() }
    }

    internal fun getItemCountByBook(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getItemCountByBook(login, login.server + book.linkSubsection)
        false -> MutableLiveData<String>().apply { this.delayedFail() }
    }

    internal fun getNeighbors(login: Login, book: Book, stringUrl: String) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getNeighbors(login, book, stringUrl)
        false -> MutableLiveData<Neighbors>().apply { this.delayedFail() }
    }

    internal fun getNeighborsNextPage(login: Login, book: Book, stringUrl: String) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getNeighborsNextPage(login, book, stringUrl)
        false -> MutableLiveData<Neighbors>().apply { this.delayedFail() }
    }

    internal fun getSeriesNeighborsRemote(login: Login, book: Book, stringUrl: String, seriesLimit: Int) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getSeriesNeighborsRemote(login, book, stringUrl, seriesLimit)
        false -> MutableLiveData<List<Book>>().apply { this.delayedFail() }
    }

    internal fun getSeriesNeighborsNextPageRemote(login: Login, stringUrl: String, seriesLimit: Int) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getSeriesNeighborsNextPageRemote(login, stringUrl, seriesLimit)
        false -> MutableLiveData<List<Book>>().apply { this.delayedFail() }
    }

    internal fun getRemoteFile(login: Login, stringUrl: String, saveDir: File) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getFile(login, login.server + stringUrl, saveDir)
        false -> MutableLiveData<File>().apply { this.delayedFail() }
    }

    //bookmark
    internal fun getRemoteUserApi(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.getRemoteUserApi(login, book)
        false -> MutableLiveData<Book>().apply { this.delayedFail() }
    }

    internal fun putRemoteUserApi(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.putRemoteUserApi(login, book)
        false -> MutableLiveData<Boolean>().apply { this.delayedFail() }
    }

    internal fun addFinishedToRemoteUserApi(login: Login, book: Book) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.addFinishedToRemoteUserApi(login, book)
        false -> MutableLiveData<Boolean>().apply { this.delayedFail() }
    }

    internal fun addFinishedToRemoteUserApi(login: Login, list: List<Book>) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.addFinishedToRemoteUserApi(login, list)
        false -> MutableLiveData<Boolean>().apply { this.delayedFail() }
    }

    internal fun removeFinishedFromRemoteUserApi(login: Login, list: List<Book>) = when (systemUtil.isNetworkAllowed()) {
        true -> kubooRemote.removeFinishedFromRemoteUserApi(login, list)
        false -> MutableLiveData<Boolean>().apply { this.delayedFail() }
    }

    private fun <T> MutableLiveData<T>.delayedFail() = GlobalScope.launch(Dispatchers.Main) {
        delay(50)
        value = null
    }

}
