package com.sethchhim.kuboo_client.ui.main.home

import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.ui.main.home.adapter.RecentAdapter
import com.sethchhim.kuboo_remote.model.Book

open class HomeFragmentImpl1_Content : HomeFragmentImpl0_View() {

    protected lateinit var recentAdapter: RecentAdapter

    internal fun handleResult(result: List<Book>?) = when (result == null) {
        true -> onPopulateRecentFail()
        false -> when (result!!.isEmpty()) {
            true -> onPopulateRecentEmpty()
            false -> onPopulateRecentSuccess(result)
        }
    }

    protected fun populateRecent() {
        setStateLoading()
        viewModel.getRecentListFromDao().observe(this, Observer { handleResult(it) })
    }

    private fun onPopulateRecentEmpty() = setStateEmpty()

    private fun onPopulateRecentFail() = setStateDisconnected()

    private fun onPopulateRecentSuccess(result: List<Book>) {
        setStateConnected()
        recentAdapter.update(result)
    }

}