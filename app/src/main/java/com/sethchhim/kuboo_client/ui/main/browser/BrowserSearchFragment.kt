package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Constants.ARG_SEARCH
import com.sethchhim.kuboo_client.Extensions.gone

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

    private fun onSwipeRefresh() = populateSearch()

    private fun populateSearch() {
        setStateLoading()
        resetRecyclerView()
        paginationHandler.reset()
        viewModel.getListByQuery(stringQuery).observe(this, Observer { result -> handleMediaResult(null, result) })
    }

    companion object {
        fun newInstance(stringQuery: String) = BrowserSearchFragment().apply {
            arguments = Bundle().apply { putString(ARG_SEARCH, stringQuery) }
        }
    }

}