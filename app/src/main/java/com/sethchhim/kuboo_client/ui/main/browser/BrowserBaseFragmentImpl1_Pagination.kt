package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Pagination
import timber.log.Timber

open class BrowserBaseFragmentImpl1_Pagination : BrowserBaseFragmentImpl0_View() {

    protected var isPaginationEnabled = true
    protected lateinit var paginationHandler: PaginationHandler
    private var paginationLiveData = MutableLiveData<Pagination>()

    protected fun setPagination() = when (isPaginationEnabled) {
        true -> paginationLayout.visible()
        false -> paginationLayout.gone()
    }

    private fun onPopulatePaginationLinksSuccess(book: Book, result: Pagination, list: List<Book>) {
        Timber.i("onPopulatePaginationLinksSuccess result: previousBook[${result.previous}] nextBook[${result.next}]")
        paginationHandler.process(result, book, list)
    }

    protected fun populatePaginationLinks(book: Book, list: List<Book>) {
        viewModel.cancelAllNetworkCallsByTag("Task_RemotePagination")
        paginationLiveData.removeAllObservers(this)
        paginationLiveData = viewModel.getPaginationByBook(book).apply {
            observe(this@BrowserBaseFragmentImpl1_Pagination, Observer { result ->
                val isValid = result != null && (result.previous.isNotEmpty() || result.next.isNotEmpty())
                if (isValid) onPopulatePaginationLinksSuccess(book, result!!, list)
            })
        }
    }

}