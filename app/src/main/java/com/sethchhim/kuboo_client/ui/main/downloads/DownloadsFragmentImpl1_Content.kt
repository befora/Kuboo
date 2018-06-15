package com.sethchhim.kuboo_client.ui.main.downloads

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.design.widget.TabLayout
import android.view.View
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadsAdapter
import com.sethchhim.kuboo_remote.KubooRemote
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

open class DownloadsFragmentImpl1_Content : DownloadsFragmentImpl0_View() {
    @Inject lateinit var kubooRemote: KubooRemote

    protected lateinit var downloadsAdapter: DownloadsAdapter

    protected val fetchListener = getFetchListener()

    protected fun populateDownloads() {
        setStateLoading()

        saveRecyclerViewState()
        viewModel.getDownloadListFromService(MutableLiveData<List<Download>>().apply {
            observe(this@DownloadsFragmentImpl1_Content, Observer { result ->
                handleResult(result)
            })
        })
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
                    1 -> kubooRemote.resumeAll()
                }
            }
        })
    }

    private fun getFetchListener() = object : FetchListener {
        override fun onCancelled(download: Download) = downloadsAdapter.update(download)

        override fun onCompleted(download: Download) = refreshDownloads()

        override fun onDeleted(download: Download) = refreshDownloads()

        override fun onError(download: Download) = downloadsAdapter.update(download)

        override fun onPaused(download: Download) = downloadsAdapter.update(download)

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) = downloadsAdapter.update(download)

        override fun onQueued(download: Download) = downloadsAdapter.update(download)

        override fun onRemoved(download: Download) = downloadsAdapter.update(download)

        override fun onResumed(download: Download) = downloadsAdapter.update(download)

        private fun refreshDownloads() = kubooRemote.getDownloadsList(MutableLiveData<List<Download>>().apply {
            observe(this@DownloadsFragmentImpl1_Content, Observer { result ->
                if (result != null) {
                    downloadsAdapter.update(result)
                }
            })
        })
    }

    private fun handleResult(result: List<Download>?) {
        when (result == null) {
            true -> onPopulateContentFail()
            false -> when (result!!.isEmpty()) {
                true -> onPopulateContentEmpty()
                false -> onPopulateContentSuccess(result)
            }
        }
    }

    internal fun onPopulateContentEmpty() {
        Timber.w("onPopulateDownloadsEmpty")
        setStateEmpty()
    }

    private fun onPopulateContentFail() {
        Timber.e("onPopulateDownloadsFail")
        setStateDisconnected()
    }

    private fun onPopulateContentSuccess(result: List<Download>) {
        Timber.i("onPopulateDownloadsSuccess result: ${result.size}")

        //remove download if it does not exist
        val filteredResult = mutableListOf<Download>()
        result.forEach {
            when (File(it.file).exists()) {
                true -> filteredResult.add(it)
                false -> kubooRemote.remove(it)
            }
        }

        setStateConnected()
        downloadsAdapter.update(filteredResult)
    }

}