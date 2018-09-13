package com.sethchhim.kuboo_client.ui.main.downloads

import android.arch.lifecycle.Observer
import android.support.design.widget.TabLayout
import com.sethchhim.kuboo_client.Extensions.compressFavorite
import com.sethchhim.kuboo_client.Extensions.downloadListToBookList
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadListAdapter
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import timber.log.Timber
import javax.inject.Inject

open class DownloadsFragmentImpl1_Content : DownloadsFragmentImpl0_View() {

    @Inject lateinit var kubooRemote: KubooRemote

    protected fun populateDownloads() {
        setNumberProgressBar()
        setStateLoading()
        resetDownloads()
        contentRecyclerView.saveState()
        viewModel.getDownloadListLiveData().observe(this, Observer {
            it?.let { handleResult(it.downloadListToBookList()) }
        })
    }

    private fun handleResult(result: List<Book>) {
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

    protected val fetchListener = object : FetchListener {
        override fun onAdded(download: Download) {}

        override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}

        override fun onCancelled(download: Download) {
            updateContent(download)
        }

        override fun onCompleted(download: Download) {
            updateContent(download)
        }

        override fun onDeleted(download: Download) {
            updateContent(download)
            setNumberProgressBar()
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            updateContent(download)
        }

        override fun onPaused(download: Download) {
            updateContent(download)
        }

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            updateContent(download)
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            updateContent(download)
        }

        override fun onRemoved(download: Download) {
            updateContent(download)
        }

        override fun onResumed(download: Download) {
            updateContent(download)
        }

        override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}

        override fun onWaitingNetwork(download: Download) {}
    }

    private fun resetDownloads() {
        (contentRecyclerView.adapter as DownloadListAdapter).submitList(mutableListOf())
    }

}
