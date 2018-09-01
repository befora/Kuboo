package com.sethchhim.kuboo_client.ui.main.browser

import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_remote.model.Book
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber

class BrowserRemoteFragment : BrowserBaseFragment() {

    override fun onButterKnifeBind(view: View) {
        super.onButterKnifeBind(view)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
        contentAdapter = BrowserContentAdapter(this, viewModel)
        contentRecyclerView.adapter = contentAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ssaa onViewCreated")
        populateRemote()
    }

    override fun onStart() {
        super.onStart()
        Timber.d("ssaa onStart")
    }

    override fun onSwipeRefresh() {
        populateRemote()
    }

    internal fun populatePrevious(book: Book) {
        populateContent(book, addPath = false)
        viewModel.decreasePathPosition()
        pathAdapter.notifyDataSetChanged()
    }

    internal fun resetBrowser() {
        viewModel.clearPathList()
        populateRemote()
    }

    private fun populateRemote() {
        val isPathEmpty = viewModel.getPathList().isEmpty()
        when (isPathEmpty) {
            true -> populateContent(getRootBook())
            false -> populateCurrentBook()
        }
    }

    private fun populateCurrentBook() {
        resetRecyclerView()
        val currentBook = viewModel.getCurrentBook()
        when (currentBook != null) {
            true -> populateContent(currentBook!!, addPath = false)
            false -> {
                onPopulateContentFail()
                toast(R.string.browser_something_went_wrong)
            }
        }
    }

}