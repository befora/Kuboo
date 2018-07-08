package com.sethchhim.kuboo_client.ui.main.downloads

import android.support.design.widget.TabLayout
import android.view.View
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadsAdapter
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import timber.log.Timber
import javax.inject.Inject

open class DownloadsFragmentImpl1_Content : DownloadsFragmentImpl0_View() {

    @Inject lateinit var kubooRemote: KubooRemote

    protected lateinit var downloadsAdapter: DownloadsAdapter

    protected val fetchListener = object : FetchListener {

        override fun onCancelled(download: Download) {
            updatePosition(download)
        }

        override fun onCompleted(download: Download) {
            updatePosition(download)
        }

        override fun onDeleted(download: Download) {
        }

        override fun onError(download: Download) {
            updatePosition(download)
        }

        override fun onPaused(download: Download) {
            updatePosition(download)
        }

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            updatePosition(download)
        }

//        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
//            updatePosition(download)
//        }

        override fun onQueued(download: Download) {
            updatePosition(download)
        }

        override fun onRemoved(download: Download) {
            updatePosition(download)
        }

        override fun onResumed(download: Download) {
            updatePosition(download)
        }

        private fun updatePosition(download: Download) {
            val dataFilteredByUrl = downloadsAdapter.data.filter { it.server + it.linkAcquisition == download.url }
            when (dataFilteredByUrl.isEmpty()) {
                true -> downloadsAdapter.data.filter { it.getXmlId() == download.group }.forEach { downloadsAdapter.updatePosition(it, download) }
                false -> dataFilteredByUrl.forEach { downloadsAdapter.updatePosition(it, download) }
            }
        }
    }

    protected fun populateDownloads() {
        setStateLoading()

        saveRecyclerViewState()
        handleResult(viewModel.getDownloadListFavoriteCompressed())
    }

    protected fun setBottomNavigation() = downloadsTabLayout.apply {
        addTab(newTab(), 0)
        addTab(newTab(), 1)
        getTabAt(0)?.text = getString(R.string.downloads_pause_all)
        getTabAt(1)?.text = getString(R.string.downloads_resume_all)
        getTabAt(0)?.setIcon(R.drawable.ic_pause_white_24dp)
        getTabAt(1)?.setIcon(R.drawable.ic_play_arrow_white_24dp)
        visibility = View.VISIBLE

        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
                    1 -> {
                        mainActivity.startSeriesDownloadService()
                        kubooRemote.resumeAll()
                    }
                }
            }
        })
    }

    internal fun handleResult(result: List<Book>?) {
        when (result == null) {
            true -> onPopulateContentFail()
            false -> when (result!!.isEmpty()) {
                true -> onPopulateContentEmpty()
                false -> onPopulateContentSuccess(result)
            }
        }
    }

    private fun onPopulateContentEmpty() {
        Timber.w("onPopulateDownloadsEmpty")
        setStateEmpty()
    }

    private fun onPopulateContentFail() {
        Timber.e("onPopulateDownloadsFail")
        setStateDisconnected()
    }

    private fun onPopulateContentSuccess(result: List<Book>) {
        Timber.i("onPopulateDownloadsSuccess result: ${result.size}")

        setStateConnected()
        downloadsAdapter.updateData(result)
    }

}
