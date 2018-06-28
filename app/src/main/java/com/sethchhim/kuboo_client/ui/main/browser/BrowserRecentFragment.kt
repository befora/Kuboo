package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Extensions.gone

class BrowserRecentFragment : BrowserBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pathHorizontalScrollView.gone()
        populateRecent()
    }

    private fun populateRecent() {
        setStateLoading()
        resetRecyclerView()
        paginationHandler.reset()
        viewModel.getRecentListFromDao().observe(this, Observer { handleMediaResult(null, it) })
    }

    private fun onSwipeRefresh() = populateRecent()

}