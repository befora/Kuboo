package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.widget.ListAdapter
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.getBrowserContentType
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.MEDIA
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.MEDIA_FORCE_LIST
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Pagination
import timber.log.Timber

open class BrowserBaseFragmentImpl1_Content : BrowserBaseFragmentImpl0_View() {

    protected lateinit var contentListAdapter: ListAdapter
    internal lateinit var contentAdapter: BrowserContentAdapter
    protected lateinit var pathAdapter: BrowserPathAdapter

    private var contentLiveData = MutableLiveData<List<Book>>()
    private var paginationLiveData = MutableLiveData<Pagination>()

    protected lateinit var paginationHandler: PaginationHandler

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
            observe(this@BrowserBaseFragmentImpl1_Content, Observer { result ->
                handleResult(book, loadState, result)
            })
        }
    }

    private fun handleResult(book: Book, loadState: Boolean, result: List<Book>?) {
        when (result == null) {
            true -> onPopulateContentFail()
            false -> when (result!!.isEmpty()) {
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
        false -> when (result!!.isEmpty()) {
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

    private fun onPopulatePaginationLinksSuccess(book: Book, result: Pagination, list: List<Book>) {
        Timber.i("onPopulatePaginationLinksSuccess result: previousBook[${result.previous}] nextBook[${result.next}]")
        paginationHandler.process(result, book, list)
    }

    private fun populatePaginationLinks(book: Book, list: List<Book>) {
        viewModel.cancelAllNetworkCallsByTag("Task_RemotePagination")
        paginationLiveData.removeAllObservers(this)
        paginationLiveData = viewModel.getPaginationByBook(book).apply {
            observe(this@BrowserBaseFragmentImpl1_Content, Observer { result ->
                val isValid = result != null && (result.previous.isNotEmpty() || result.next.isNotEmpty())
                if (isValid) onPopulatePaginationLinksSuccess(book, result!!, list)
            })
        }
    }

    open fun onSwipeRefresh() {
        //override in children
    }

}