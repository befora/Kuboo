package com.sethchhim.kuboo_client.ui.main.browser

import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_remote.model.Book
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber

class BrowserRemoteFragment : BrowserBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        populateRemote()
    }

    internal fun populatePrevious(book: Book) {
        Timber.i("populatingPrevious ${book.title}")
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
            true -> {
                Timber.i("Path licenseList not detected, loading root browser directory.")
                populateContent(getRootBook())
            }
            false -> {
                Timber.i("Path licenseList detected! Resuming browser data.")
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
    }

    private fun onSwipeRefresh() {
        val book = viewModel.getCurrentBook()
        when (book != null) {
            true -> {
                resetRecyclerView()
                populateContent(book!!, addPath = false)
            }
            false -> Timber.e("No licenseList available to populate swipe refresh!")
        }
    }

}