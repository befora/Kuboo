package com.sethchhim.kuboo_client.ui.main.home

import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.home.adapter.LatestAdapter
import com.sethchhim.kuboo_client.ui.main.home.adapter.RecentAdapter
import com.sethchhim.kuboo_remote.model.Book

open class HomeFragmentImpl1_Content : HomeFragmentImpl0_View() {

    protected lateinit var latestAdapter: LatestAdapter
    protected lateinit var recentAdapter: RecentAdapter

    protected fun isHomeRequireLatest() = Settings.HOME_LAYOUT == 1

    //recent
    protected fun populateRecent() {
        setRecentStateLoading()
        viewModel.getRecentListFromDao().observe(this, Observer { handleRecentResult(it) })
    }

    internal fun handleRecentResult(result: List<Book>?) = when (result == null) {
        true -> onPopulateRecentFail()
        false -> when (result!!.isEmpty()) {
            true -> onPopulateRecentEmpty()
            false -> onPopulateRecentSuccess(result)
        }
    }

    private fun onPopulateRecentEmpty() = setRecentStateEmpty()

    private fun onPopulateRecentFail() = setRecentStateDisconnected()

    private fun onPopulateRecentSuccess(result: List<Book>) {
        setRecentStateConnected()
        recentAdapter.update(result)
    }

    //latest
    protected fun populateLatest() {
        setLatestStateLoading()
        viewModel.getLatestListFromServer().observe(this, Observer { handleLatestResult(it) })
    }

    private fun handleLatestResult(result: List<Book>?) = when (result == null) {
        true -> onPopulateLatestFail()
        false -> when (result!!.isEmpty()) {
            true -> onPopulateLatestEmpty()
            false -> onPopulateLatestSuccess(result)
        }
    }

    private fun onPopulateLatestEmpty() = setLatestStateEmpty()

    private fun onPopulateLatestFail() = setLatestStateDisconnected()

    private fun onPopulateLatestSuccess(result: List<Book>) {
        setLatestStateConnected()
        latestAdapter.update(result)
    }

}