package com.sethchhim.kuboo_client.ui.main.browser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.getBrowserContentType
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.Temporary
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.MEDIA
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.MEDIA_FORCE_LIST
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

open class BrowserBaseFragmentImpl2_Content : BrowserBaseFragmentImpl1_Pagination() {

    internal lateinit var contentAdapter: BrowserContentAdapter
    protected lateinit var pathAdapter: BrowserPathAdapter

    private var contentLiveData = MutableLiveData<List<Book>>()


    internal fun populateContent(book: Book, loadState: Boolean = true, addPath: Boolean = true) {
        setStateLoading()
        viewModel.cancelAllNetworkCallsByTag("Task_RemoteBookList")

        saveRecyclerViewState()
        resetRecyclerView()
        paginationHandler.reset()

        if (addPath) {
            viewModel.addPath(book)
            pathAdapter.notifyDataSetChanged()
        }

        contentLiveData.removeAllObservers(this)
        contentLiveData = viewModel.getListByBook(book).apply {
            observe(this@BrowserBaseFragmentImpl2_Content, Observer { result ->
                handleResult(book, loadState, result)
            })
        }
    }

    private fun handleResult(book: Book, loadState: Boolean, result: List<Book>?) {
        when (result == null) {
            true -> onPopulateContentFail()
            false -> when (result.isEmpty()) {
                true -> onPopulateContentEmpty()
                false -> onPopulateContentSuccess(book, result, loadState)
            }
        }
    }

    private fun onPopulateContentSuccess(book: Book, result: List<Book>, loadState: Boolean) {
        Timber.i("onPopulateMediaSuccess result: ${result.size}")
        val newResult = mutableListOf<Book>().apply { addAll(result) }

        if (Settings.FAVORITE) {
            val isRootBook = book.content == Constants.TAG_ROOT_BOOK
            if (isRootBook) {
                viewModel.getFavoriteList().forEach {
                    if (it.isMatchServer(book)) newResult.add(it)
                }
            }
        }

        setStateConnected()

        val browserContentType = book.getBrowserContentType()
        setContentSpanCount(resources.configuration.orientation, browserContentType)
        mainActivity.toggleMenuItemBrowserLayout(browserContentType)
        populatePaginationLinks(book, newResult)
        contentAdapter.update(newResult)
        if (loadState) loadRecyclerViewState(book)
    }

    protected fun onPopulateContentEmpty() {
        Timber.w("onPopulateContentEmpty")
        setStateEmpty()
    }

    protected fun onPopulateContentFail() {
        Timber.e("onPopulateContentFail")
        setStateDisconnected()
    }

    protected fun getRootBook() = Book().apply {
        title = getString(R.string.browser_folder)
        content = Constants.TAG_ROOT_BOOK
        server = viewModel.getActiveServer()
        linkSubsection = Constants.URL_PATH_ROOT
    }

    /** handle content but force media item type */
    protected fun handleMediaResult(book: Book?, result: List<Book>?) = when (result == null) {
        true -> onPopulateContentFail()
        false -> when (result.isEmpty()) {
            true -> onPopulateContentEmpty()
            false -> onPopulateMediaSuccess(book, result)
        }
    }

    private fun onPopulateMediaSuccess(book: Book?, result: List<Book>) {
        Timber.i("onPopulateMediaSuccess result: ${result.size}")
        setStateConnected()
        setContentSpanCount(resources.configuration.orientation, when (Settings.BROWSER_MEDIA_FORCE_LIST) {
            true -> MEDIA_FORCE_LIST
            false -> MEDIA
        })

        book?.let {
            populatePaginationLinks(it, result)
            mainActivity.toggleMenuItemBrowserLayout(book.getBrowserContentType())
        }
        contentAdapter.update(result)
    }

    /** populate pagination content*/
    internal fun populatePaginationContent(book: Book) {
        viewModel.updatePathLinkSubsection(book)
        populateContent(book, addPath = false)
    }

    protected fun enableSelection(isCustomImplementation: Boolean) {
        if (!isCustomImplementation) {
            contentAdapter.resetAllColorState()
            mainActivity.enableSelectionMenuState()
        }
    }

    protected fun disableSelection(isCustomImplementation: Boolean) {
        if (!isCustomImplementation) mainActivity.disableSelectionMenuState()
    }

    protected fun handleNeededAdapterUpdate() {
        if (::contentAdapter.isInitialized) {
            contentAdapter.apply {
                if (Temporary.USER_API_UPDATE_LIST.isNotEmpty()) {
                    Temporary.USER_API_UPDATE_LIST.forEach { queueBook ->
                        viewModel.getBrowserContentList().forEachIndexed { index, browser ->
                            if (browser.book.isMatch(queueBook)) contentAdapter.updateMediaColorStateFromRemoteUserApi(index, browser.book)
                        }
                    }
                    Temporary.USER_API_UPDATE_LIST.clear()
                }
            }
        }
    }
}