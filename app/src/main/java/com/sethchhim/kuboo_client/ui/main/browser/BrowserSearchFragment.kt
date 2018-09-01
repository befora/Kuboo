package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_remote.model.Book

class BrowserSearchFragment : BrowserBaseFragment() {

    init {
        isPathEnabled = false
    }

    private lateinit var stringQuery: String

    override fun onButterKnifeBind(view: View) {
        super.onButterKnifeBind(view)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
        contentAdapter = BrowserContentAdapter(this, viewModel)
        contentRecyclerView.adapter = contentAdapter
    }

    override fun onStart() {
        super.onStart()
        stringQuery = arguments?.getString(Constants.ARG_SEARCH) ?: ""
        mainActivity.title = stringQuery
        mainActivity.collapseMenuItemSearch()
        populateSearch()
    }

    private fun populateSearch() {
        setStateLoading()
        resetRecyclerView()
        paginationHandler.reset()
        viewModel.getListByQuery(stringQuery).observe(this, Observer { result ->
            val book = Book().apply { linkSubsection = "${Constants.URL_PATH_SEARCH}$stringQuery" }
            handleMediaResult(book, result)
        })
    }

    override fun onSwipeRefresh() {
        populateSearch()
    }

    companion object {
        fun newInstance(stringQuery: String) = BrowserSearchFragment().apply {
            arguments = Bundle().apply { putString(Constants.ARG_SEARCH, stringQuery) }
        }
    }

}