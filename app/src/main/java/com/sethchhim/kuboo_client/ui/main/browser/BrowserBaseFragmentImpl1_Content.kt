package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v7.app.AlertDialog
import android.widget.TextView
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.isBrowserMediaType
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.FOLDER
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType.MEDIA
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Pagination
import timber.log.Timber

open class BrowserBaseFragmentImpl1_Content : BrowserBaseFragmentImpl0_View() {

    protected lateinit var contentAdapter: BrowserContentAdapter
    protected lateinit var pathAdapter: BrowserPathAdapter

    private var isFirstInstance = true

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

    protected fun getRootBook() = Book().apply {
        title = getString(R.string.browser_folder)
        content = Constants.TAG_ROOT_BOOK
        server = viewModel.getActiveServer()
        linkSubsection = Constants.URL_PATH_ROOT
    }

    internal fun populatePaginationContent(book: Book) {
        viewModel.updatePathLinkSubsection(book)
        populateContent(book, addPath = false)
    }

    protected fun handleResult(result: List<Book>?) = when (result == null) {
        true -> onPopulateContentFail()
        false -> when (result!!.isEmpty()) {
            true -> onPopulateContentEmpty()
            false -> onPopulateContentSuccess(result)
        }
    }

    protected fun onDownloadMenuItemClicked(): Boolean {
        val selectedList = viewModel.getSelectedList()
        dialogUtil.getDialogDownload(mainActivity, selectedList).apply {
            setButton(AlertDialog.BUTTON_POSITIVE, "${context.getString(R.string.dialog_download)} (${selectedList.size})") { _, _ ->
                viewModel.startDownloads(selectedList, savePath = Settings.DOWNLOAD_SAVE_PATH)
                contentAdapter.disableSelectionMode()
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel)) { dialog, _ -> dialog.dismiss() }
            show()

            findViewById<TextView>(android.R.id.message)?.apply {
                textSize = 11F
            }
        }
        return true
    }

    protected fun onMarkFinishedAddMenuItemClicked(): Boolean {
        viewModel.addFinishFromSelectedList().observe(this, Observer { result ->
            result?.let { if (it) contentAdapter.disableSelectionMode() }
        })

        if (!Settings.MARK_FINISHED) dialogUtil.getSnackBarMarkFinished(contentSwipeRefreshLayout).apply {
            setAction(R.string.main_settings) { mainActivity.showFragmentSettings() }
            show()
        }
        return true
    }

    protected fun onMarkFinishedDeleteMenuItemClicked(): Boolean {
        viewModel.removeFinishFromSelectedList().observe(this, Observer { result ->
            result?.let { if (it) contentAdapter.disableSelectionMode() }
        })
        return true
    }

    protected fun onPopulateContentFail() {
        Timber.e("onPopulateContentFail")
        setStateDisconnected()
    }

    protected fun resetAllColorState() {
        if (!isFirstInstance) contentAdapter.resetAllColorState()
        isFirstInstance = false
    }

    private fun getContentType() = when (viewModel.getCurrentBook()?.isBrowserMediaType() ?: true) {
        true -> MEDIA
        false -> FOLDER
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

    private fun onPopulateContentEmpty() {
        Timber.w("onPopulateContentEmpty")
        setStateEmpty()
    }

    private fun onPopulateContentSuccess(result: List<Book>) {
        Timber.i("onPopulateContentSuccess result: ${result.size}")
        setStateConnected()
        setContentSpanCount(resources.configuration.orientation, MEDIA)
        contentAdapter.update(result)
    }

    private fun onPopulateContentSuccess(book: Book, result: List<Book>, loadState: Boolean) {
        Timber.i("onPopulateContentSuccess result: ${result.size}")
        val newResult = mutableListOf<Book>().apply { addAll(result) }

        if (Settings.FAVORITE) {
            val isRootBook = book.content == Constants.TAG_ROOT_BOOK
            if (isRootBook) newResult.addAll(viewModel.getFavoriteList())
        }

        setStateConnected()
        setContentSpanCount(resources.configuration.orientation, getContentType())
        populatePaginationLinks(book, newResult)
        contentAdapter.update(newResult)
        if (loadState) loadRecyclerViewState(book)
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

}


