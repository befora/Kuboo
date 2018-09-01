package com.sethchhim.kuboo_client.ui.main.downloads

import android.arch.lifecycle.Observer
import android.support.design.widget.TabLayout
import com.sethchhim.kuboo_client.Extensions.compressFavorite
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadListAdapter
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber
import javax.inject.Inject

open class DownloadsFragmentImpl1_Content : DownloadsFragmentImpl0_View() {

    @Inject lateinit var kubooRemote: KubooRemote

    protected fun populateDownloads() {
        setNumberProgressBar()
        setStateLoading()
        contentRecyclerView.saveState()
        appDatabaseDao.getAllBookDownloadLiveData().observe(this, Observer {
            handleResult(it?.downloadListToBookList() ?: listOf())
        })
    }

    internal fun handleResult(result: List<Book>) {
        viewModel.setDownloadList(result)
        when (result.isEmpty()) {
            true -> onPopulateContentEmpty()
            false -> onPopulateContentSuccess(result)
        }
    }

    private fun onPopulateContentSuccess(result: List<Book>) {
        Timber.i("onPopulateDownloadsSuccess result: ${result.size}")
        setStateConnected()
        (contentRecyclerView.adapter as DownloadListAdapter).submitList(result.compressFavorite())
        contentRecyclerView.restoreState()
    }

    protected val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
            handleTab(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabSelected(tab: TabLayout.Tab) {
            handleTab(tab.position)
        }

        private fun handleTab(position: Int) {
            when (position) {
                0 -> kubooRemote.pauseAll()
                1 -> mainActivity.trackingService.startOneTimeTrackingService(viewModel.getActiveLogin())
            }
        }
    }

}
