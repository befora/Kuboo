package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Extensions.gone

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
        viewModel.getLatestListFromServer().observe(this, Observer { handleMediaResult(null, it) })
    }

    private fun onSwipeRefresh() = populateLatest()

}