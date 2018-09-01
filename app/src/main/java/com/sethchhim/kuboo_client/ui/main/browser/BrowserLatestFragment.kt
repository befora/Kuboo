package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.view.View
import com.sethchhim.kuboo_client.Constants.URL_PATH_LATEST
import com.sethchhim.kuboo_remote.model.Book

class BrowserLatestFragment : BrowserBaseFragment() {

    init {
        isPathEnabled = false
    }

    override fun onButterKnifeBind(view: View) {
        super.onButterKnifeBind(view)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onStart() {
        super.onStart()
        populateLatest()
    }

    private fun populateLatest() {
        setStateLoading()
        resetRecyclerView()
        paginationHandler.reset()
        viewModel.getLatestListFromServer().observe(this, Observer {
            val book = Book().apply { linkSubsection = URL_PATH_LATEST }
            handleMediaResult(book, it)
        })
    }

    override fun onSwipeRefresh() {
        populateLatest()
    }

}