package com.sethchhim.kuboo_client.data

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.data.model.Dimension
import com.sethchhim.kuboo_client.data.model.GlideEpub
import com.sethchhim.kuboo_client.data.model.GlidePdf
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.repository.*
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.tonyodev.fetch2.Download
import timber.log.Timber
import java.io.File

class ViewModel(internal val browserRepository: BrowserRepository,
                private val downloadsRepository: DownloadsRepository,
                private val favoriteRepository: FavoriteRepository,
                private val fetchRepository: FetchRepository,
                private val latestRepository: LatestRepository,
                private val localRepository: LocalRepository,
                private val logRepository: LogRepository,
                private val loginRepository: LoginRepository,
                private val pdfRepository: PdfRepository,
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

    internal fun addRecent(book: Book, setTimeAccessed: Boolean = true) = recentRepository.addRecent(getActiveLogin(), book, setTimeAccessed)

    internal fun removeRecent(book: Book) = recentRepository.removeRecent(getActiveLogin(), book)

    internal fun setRecentList(list: List<Book>) = recentRepository.setRecentList(list)

    //latest
    internal fun getLatestListFromServer() = remoteRepository.getLatestList(getActiveLogin())

    internal fun getLatestList() = latestRepository.getLatestList()

    internal fun setLatestList(list: List<Book>) = latestRepository.setLatestList(list)

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

    internal fun getPathItemId(position: Int) = browserRepository.getPathItemId(position)

    internal fun getCurrentBook() = browserRepository.getCurrentBook()

    internal fun getPreviousBook() = browserRepository.getPreviousBook()

    internal fun addPath(book: Book) = browserRepository.addPath(book)

    internal fun clearPathList() = browserRepository.clearPathList()

    internal fun updatePathLinkSubsection(book: Book) = browserRepository.updatePathLinkSubsection(book)

    internal fun decreasePathPosition() = browserRepository.decreasePathPosition()

    internal fun setPathPosition(position: Int) = browserRepository.setPathPosition(position)

    //downloads
    internal fun getDownloadListLiveData() = downloadsRepository.getDownloadListLiveData()

    internal fun getDownloadList(favoriteCompressed: Boolean = false) = downloadsRepository.getDownloadList(favoriteCompressed)

    internal fun addDownload(book: Book) = downloadsRepository.addDownload(book)

    internal fun deleteDownload(book: Book) = downloadsRepository.deleteDownload(book)

    //fetch
    internal fun startFetchDownloads(login: Login, list: List<Book>, savePath: String) {
        list.forEach {
            it.currentPage = 0
            it.isFinished
        }
        val isSavePathValid = savePath.isNotEmpty()
        when (isSavePathValid) {
            true -> {
                fetchRepository.startDownloads(login, list, savePath)
                downloadsRepository.addDownloads(list, savePath)
            }
            false -> Timber.e("Fetch download cancelled because save path is not valid! size[${list.size}] savePath[$savePath]")
        }
    }

    internal fun deleteFetchDownloadsNotInList(doNotDeleteList: MutableList<Book>) = fetchRepository.deleteFetchDownloadsNotInList(doNotDeleteList)

    internal fun getFetchDownload(book: Book) = fetchRepository.getDownload(book)

    internal fun getFetchDownloads() = fetchRepository.getDownloads()

    internal fun resumeFetchDownload(download: Download) = fetchRepository.resumeDownload(download)

    internal fun retryFetchDownload(download: Download) = fetchRepository.retryDownload(download)

    internal fun deleteFetchSeries(book: Book, keepBook: Boolean) = fetchRepository.deleteSeries(book, keepBook)

    internal fun deleteFetchDownload(book: Book) = fetchRepository.deleteDownload(book)

    internal fun deleteFetchDownload(download: Download) = fetchRepository.deleteDownload(download)

    //reader licenseList
    internal fun createRemoteSinglePaneReaderList(book: Book) = readerRepository.createRemoteList(book)

    internal fun createLocalSinglePaneList(book: Book) = readerRepository.createLocalList(book)

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

    internal fun getNeighborsNextPageRemote(book: Book, stringUrl: String) = remoteRepository.getNeighborsNextPage(getActiveLogin(), book, stringUrl)

    internal fun getNeighborsDownload(book: Book) = downloadsRepository.getDownloadNeighbors(book)

    internal fun getSeriesNeighborsRemote(login: Login, book: Book, stringUrl: String, seriesLimit: Int) = remoteRepository.getSeriesNeighborsRemote(login, book, stringUrl, seriesLimit)

    internal fun getSeriesNeighborsNextPageRemote(login: Login, stringUrl: String, seriesLimit: Int) = remoteRepository.getSeriesNeighborsNextPageRemote(login, stringUrl, seriesLimit)

    internal fun getLocalComicInfo() = localRepository.getLocalComicInfo()

    internal fun getLocalImageInputStream(position: Int) = localRepository.getLocalImageInputStream(position)

    internal fun getLocalImageInputStreamSingleInstance(filePath: String, position: Int) = localRepository.getLocalImageInputStreamSingleInstance(filePath, position)

    internal fun getRemoteFile(stringUrl1: String, saveDir: File) = remoteRepository.getRemoteFile(getActiveLogin(), stringUrl1, saveDir)

    internal fun cleanupParser() = localRepository.cleanupParser()

    internal fun clearContentList() = browserRepository.clearContentList()

    internal fun singleToDualLocal(list: List<PageUrl>) = readerRepository.singleToDualLocal(list)

    internal fun singleToDualRemote(lifecycleOwner: LifecycleOwner, list: List<PageUrl>) = readerRepository.singleToDualRemote(lifecycleOwner, list)

    internal fun getReaderDimensionAt(position: Int) = readerRepository.getReaderDimensionAt(position)

    internal fun setReaderDimension(position: Int, dimension: Dimension) = readerRepository.setReaderDimension(position, dimension)

//    internal fun preloadImageByteArray(position: Int) = readerRepository.preloadImageByteArray(position)

    //bookmark
    internal fun addFinish(book: Book) = remoteRepository.addFinishedToRemoteUserApi(getActiveLogin(), book)

    internal fun addFinishFromSelectedList() = remoteRepository.addFinishedToRemoteUserApi(getActiveLogin(), getSelectedList())

    internal fun removeFinishFromSelectedList() = remoteRepository.removeFinishedFromRemoteUserApi(getActiveLogin(), getSelectedList())

    internal fun getRemoteUserApi(book: Book) = remoteRepository.getRemoteUserApi(getActiveLogin(), book)

    internal fun putRemoteUserApi(book: Book) = remoteRepository.putRemoteUserApi(getActiveLogin(), book)

    //log
    internal fun getLogList() = logRepository.getLogList()

    //pdf
    internal fun initPdf(filePath: String) = pdfRepository.initPdf(filePath)

    internal fun getPdfDocument() = pdfRepository.document

    internal fun getPdfImageInputStream(glidePdf: GlidePdf) = pdfRepository.getPdfImageInputStream(glidePdf)

    internal fun getPdfImageInputStreamSingleInstance(glidePdf: GlidePdf) = pdfRepository.getPdfImageInputStreamSingleInstance(glidePdf)

    internal fun getPdfOutline() = pdfRepository.getPdfOutline()

    internal fun getPdfPageCount() = pdfRepository.getPdfPageCount()

    internal fun getEpubCoverInputStream(glideEpub: GlideEpub) = localRepository.getEpubCoverInputStream(glideEpub)

}