package com.sethchhim.kuboo_client.data

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.repository.*
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.tonyodev.fetch2.Download
import java.io.File

class ViewModel(internal val browserRepository: BrowserRepository,
                private val downloadsRepository: DownloadsRepository,
                private val favoriteRepository: FavoriteRepository,
                private val localRepository: LocalRepository,
                private val loginRepository: LoginRepository,
                private val readerRepository: ReaderRepository,
                private val recentRepository: RecentRepository,
                private val remoteRepository: RemoteRepository) : ViewModel() {

    //active login
    internal val activeLoginLiveData = loginRepository.getActiveLoginLiveData()

    internal fun getActiveLogin() = loginRepository.getActiveLogin()

    internal fun getActiveServer() = loginRepository.getActiveServer()

    internal fun setActiveLogin(login: Login?) = loginRepository.setActiveLogin(login)

    internal fun isActiveLoginEmpty() = loginRepository.isActiveLoginEmpty()

    //login
    internal fun getLoginList() = loginRepository.getLoginList()

    internal fun addLogin(login: Login) = loginRepository.addLogin(login)

    internal fun removeLogin(login: Login) = loginRepository.deleteLogin(login)

    internal fun getLoginAt(position: Int) = loginRepository.getLoginAt(position)

    internal fun getLoginLastAccessed() = loginRepository.getLoginLastAccessed()

    internal fun isActiveLogin(login: Login) = loginRepository.isActiveLogin(login)

    internal fun isLoginListEmpty() = loginRepository.isLoginListEmpty()

    //remote
    internal fun pingServer(stringUrl: String) = remoteRepository.pingServer(getActiveLogin(), stringUrl)

    internal fun pingActiveServer() = remoteRepository.pingServer(getActiveLogin(), getActiveServer())

    internal fun getListByBook(book: Book) = remoteRepository.getListByBook(getActiveLogin(), book)

    internal fun getListByUrl(stringUrl: String) = remoteRepository.getListByUrl(getActiveLogin(), stringUrl)

    internal fun getListByQuery(stringQuery: String) = remoteRepository.getListByQuery(getActiveLogin(), stringQuery)

    internal fun getPaginationByBook(book: Book) = remoteRepository.getPaginationByBook(getActiveLogin(), book)

    internal fun getFirstByBook(book: Book) = remoteRepository.getFirstByBook(getActiveLogin(), book)

    internal fun getItemCountByBook(book: Book) = remoteRepository.getItemCountByBook(getActiveLogin(), book)

    internal fun getTlsCipherSuite() = remoteRepository.getTlsCipherSuite()

    internal fun isConnectedEncrypted() = remoteRepository.isConnectedEncrypted()

    internal fun cancelAllNetworkCallsByTag(tag: String) = remoteRepository.cancelAllNetworkCallsByTag(tag)

    internal fun cancelAllPing() = remoteRepository.cancelAllNetworkCallsByTag(Constants.KEY_TASK_PING)

    //recent
    internal fun getRecentList() = recentRepository.getRecentList()

    internal fun getRecentListFromDao() = recentRepository.getRecentListFromDao(getActiveLogin())

    internal fun getRecentSize() = recentRepository.getRecentSize()

    internal fun getRecentAt(position: Int) = recentRepository.getRecentItemAt(position)

    internal fun getRecentByXmlId(book: Book, filterByActiveServer: Boolean) = when (filterByActiveServer) {
        true -> recentRepository.getRecentByXmlId(getActiveLogin(), book)
        false -> recentRepository.getRecentByXmlId(null, book)
    }

    internal fun getRecentByBook(book: Book, filterByActiveServer: Boolean) = when (filterByActiveServer) {
        true -> recentRepository.getRecentByBook(getActiveLogin(), book)
        false -> recentRepository.getRecentByBook(null, book)
    }

    internal fun addRecent(book: Book) = recentRepository.addRecent(getActiveLogin(), book)

    internal fun removeRecent(book: Book) = recentRepository.removeRecent(getActiveLogin(), book)

    internal fun setRecentList(list: List<Book>) = recentRepository.setRecentList(list)

    //latest
    internal fun getLatestList() = remoteRepository.getLatestList(getActiveLogin())

    internal fun syncLatestList(list: List<Book>) = remoteRepository.syncLatestList(list)

    //favorite
    internal fun getFavoriteListFromDao() = favoriteRepository.getFavoriteListFromDao()

    internal fun setFavoriteList(list: List<Book>) = favoriteRepository.setFavoriteList(list)

    internal fun getFavoriteList() = favoriteRepository.getFavoriteList()

    internal fun isFavorite(book: Book) = favoriteRepository.isFavorite(book)

    internal fun removeFavorite(book: Book) = favoriteRepository.removeFavorite(book)

    internal fun addFavorite(book: Book) = favoriteRepository.addFavorite(book)

    //browser state
    internal fun saveRecyclerViewState(book: Book, state: Parcelable) = browserRepository.saveRecyclerViewState(book, state)

    internal fun loadRecyclerViewState(book: Book) = browserRepository.loadRecyclerViewState(book)

    //browser selection
    internal fun getSelectedList() = browserRepository.getSelectedList()

    internal fun getSelectedListSize() = browserRepository.getSelectedListSize()

    internal fun addSelected(book: Book) = browserRepository.addSelected(book)

    internal fun removeSelected(book: Book) = browserRepository.removeSelected(book)

    internal fun clearSelected() = browserRepository.clearSelectedList()

    internal fun isSelected(book: Book) = browserRepository.isSelected(book)

    internal fun isSelectedListEmpty() = browserRepository.isSelectedListEmpty()

    //browser content
    internal fun getBrowserContentItemAt(position: Int) = browserRepository.getBrowserContentItemAt(position)

    internal fun getBrowserContentList() = browserRepository.contentList

    internal fun setBrowserContentList(list: List<Book>) = browserRepository.setBrowserContentList(list)

    internal fun updateBrowserItem(book: Book) = browserRepository.updateBrowserItem(book)

    //browser path
    internal fun getPathPosition() = browserRepository.getPathPosition()

    internal fun getPathList() = browserRepository.getPathList()

    internal fun isPathListEmpty() = browserRepository.isPathListEmpty()

    internal fun getPathSize() = browserRepository.getPathSize()

    internal fun getCurrentBook() = browserRepository.getCurrentBook()

    internal fun getPreviousBook() = browserRepository.getPreviousBook()

    internal fun addPath(book: Book) = browserRepository.addPath(book)

    internal fun removePath(book: Book) = browserRepository.removePathByBook(book)

    internal fun clearPathList() = browserRepository.clearPathList()

    internal fun updatePathLinkSubsection(book: Book) = browserRepository.updatePathLinkSubsection(book)

    internal fun decreasePathPosition() = browserRepository.decreasePathPosition()

    internal fun increasePathPosition() = browserRepository.increasePathPosition()

    internal fun setPathPosition(position: Int) = browserRepository.setPathPosition(position)

    internal fun trimPathAt(position: Int) = browserRepository.trimPathAt(position)

    //menu_reader

    internal fun getFile(stringUrl1: String, saveDir: File) = remoteRepository.getFile(getActiveLogin(), stringUrl1, saveDir)

    //downloads
    internal fun getDownloadList() = downloadsRepository.getDownloadList()

    internal fun getDownloadsListFromAppDatabase() = downloadsRepository.getDownloadsListFromAppDatabase()

    internal fun getDownloadListFromService(liveData: MutableLiveData<List<Download>>) = downloadsRepository.getDownloadListFromKubooService(liveData)

    internal fun getDownloadAt(position: Int) = downloadsRepository.getDownloadAt(position)

    internal fun getDownloadBookByUrl(stringUrl: String) = downloadsRepository.getDownloadBookByUrl(stringUrl)

    internal fun startDownloads(list: List<Book>) = downloadsRepository.startDownloads(getActiveLogin(), list)

    internal fun addDownload(book: Book) = downloadsRepository.addDownload(book)

    internal fun deleteDownload(download: Download) = downloadsRepository.deleteDownload(download)

    internal fun setDownloadList(list: List<Download>) = downloadsRepository.setDownloadList(list)

    internal fun getDownloadByBook(book: Book) = downloadsRepository.getDownloadByBook(book)

    internal fun isDownloadContains(book: Book) = downloadsRepository.isDownloadContains(book)

    internal fun isDownloadsQueuedEmpty() = downloadsRepository.isDownloadsQueuedEmpty()

    internal fun isDownloadsPausedEmpty() = downloadsRepository.isDownloadsPausedEmpty()

    internal fun printPath() = browserRepository.printPath()

    //reader licenseList
    internal fun createRemoteSinglePaneReaderList(book: Book) = readerRepository.createRemoteList(book)

    internal fun createLocalSinglePaneList(book: Book) = readerRepository.createLocalList(book)

    internal fun getReaderList() = readerRepository.getReaderList()

    internal fun getReaderListSize() = readerRepository.getReaderListSize()

    internal fun getReaderItemAt(position: Int) = readerRepository.getReaderItemAt(position)

    internal fun getReaderTrueIndexAt(position: Int) = readerRepository.getReaderTrueIndexAt(position)

    internal fun getReaderPositionByTrueIndex(position: Int) = readerRepository.getReaderPositionByTrueIndex(position)

    internal fun getSinglePaneList() = readerRepository.getSinglePaneList()

    internal fun setSinglePaneList(list: List<PageUrl>) = readerRepository.setSinglePaneList(list)

    internal fun setDualPaneList(list: List<PageUrl>) = readerRepository.setDualPaneList(list)

    internal fun setReaderListType() = readerRepository.setReaderListType()

    internal fun clearReaderLists() = readerRepository.clearReaderLists()

    internal fun isReaderDualPaneListEmpty() = readerRepository.isReaderDualPaneListEmpty()

    internal fun printReaderList() = readerRepository.printReaderList()

    //reader content
    internal fun getNeighborsRemote(book: Book, stringUrl: String) = remoteRepository.getNeighbors(getActiveLogin(), book, stringUrl)

    internal fun getNeighborsLocal(book: Book) = downloadsRepository.getNeighbors(book)

    internal fun getPage(position: Int) = localRepository.getPage(position)

    internal fun getLocalImageInputStream(position: Int) = localRepository.getLocalImageInputStream(position)

    internal fun cleanupParser() = localRepository.cleanupParser()

    internal fun clearContentList() = browserRepository.clearContentList()

    internal fun singleToDualLocal(list: List<PageUrl>) = readerRepository.singleToDualLocal(list)

    internal fun singleToDualRemote(lifecycleOwner: LifecycleOwner, list: List<PageUrl>) = readerRepository.singleToDualRemote(lifecycleOwner, list)

//    internal fun preloadImageByteArray(position: Int) = readerRepository.preloadImageByteArray(position)

    //bookmark
    internal fun addFinish(book: Book) = remoteRepository.addFinishedToRemoteUserApi(getActiveLogin(), book)

    internal fun addFinishFromSelectedList() = remoteRepository.addFinishedToRemoteUserApi(getActiveLogin(), getSelectedList())

    internal fun removeFinishFromSelectedList() = remoteRepository.removeFinishedFromRemoteUserApi(getActiveLogin(), getSelectedList())

    internal fun getRemoteUserApi(book: Book) = remoteRepository.getRemoteUserApi(getActiveLogin(), book)

    internal fun putRemoteUserApi(book: Book) = remoteRepository.putRemoteUserApi(getActiveLogin(), book)

}