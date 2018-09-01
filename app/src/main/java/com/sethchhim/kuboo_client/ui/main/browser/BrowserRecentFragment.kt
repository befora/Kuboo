package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.view.View
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_remote.model.Book

class BrowserRecentFragment : BrowserBaseFragment() {

    init {
        isPathEnabled = false
    }

    override fun onButterKnifeBind(view: View) {
        super.onButterKnifeBind(view)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onStart() {
        super.onStart()
        populateRecent()
    }

    private fun populateRecent() {
        setStateLoading()
        resetRecyclerView()
        paginationHandler.reset()
        viewModel.getRecentListFromDao().observe(this, Observer {
            val book = Book().apply { linkSubsection = Constants.URL_PATH_GRID_DIRECTORY }
            handleMediaResult(book, it)
        })
    }

    override fun onSwipeRefresh() {
        populateRecent()
    }

}