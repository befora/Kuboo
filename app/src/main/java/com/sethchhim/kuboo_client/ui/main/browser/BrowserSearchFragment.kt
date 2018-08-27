package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Constants.ARG_SEARCH
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_remote.model.Book

class BrowserSearchFragment : BrowserBaseFragment() {

    private lateinit var stringQuery: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        stringQuery = arguments?.getString(ARG_SEARCH) ?: ""
        mainActivity.title = stringQuery
        mainActivity.collapseMenuItemSearch()
        pathHorizontalScrollView.gone()
        populateSearch()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
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

    companion object {
        fun newInstance(stringQuery: String) = BrowserSearchFragment().apply {
            arguments = Bundle().apply { putString(ARG_SEARCH, stringQuery) }
        }
    }

}