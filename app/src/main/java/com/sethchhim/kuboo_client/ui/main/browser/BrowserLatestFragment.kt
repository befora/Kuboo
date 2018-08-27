package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Constants.URL_PATH_LATEST
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_remote.model.Book

class BrowserLatestFragment : BrowserBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pathHorizontalScrollView.gone()
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
        super.onSwipeRefresh()
        populateLatest()
    }
}