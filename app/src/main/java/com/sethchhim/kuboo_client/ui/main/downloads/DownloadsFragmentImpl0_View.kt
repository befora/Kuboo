package com.sethchhim.kuboo_client.ui.main.downloads

import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import com.sethchhim.kuboo_client.Extensions.toReadable
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.BrowserBaseFragment
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadListAdapter
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import java.io.File

open class DownloadsFragmentImpl0_View : BrowserBaseFragment() {

    private var recyclerViewState: Parcelable? = null

    protected val fetchListener = object : FetchListener {
        override fun onAdded(download: Download) {}

        override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}

        override fun onCancelled(download: Download) {
            updatePosition(download)
        }

        override fun onCompleted(download: Download) {
            updatePosition(download)
        }

        override fun onDeleted(download: Download) {
            setNumberProgressBar()
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            updatePosition(download)
        }

        override fun onPaused(download: Download) {
            updatePosition(download)
        }

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            updatePosition(download)
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            updatePosition(download)
        }

        override fun onRemoved(download: Download) {
            updatePosition(download)
        }

        override fun onResumed(download: Download) {
            updatePosition(download)
        }

        override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}

        override fun onWaitingNetwork(download: Download) {}

        private fun updatePosition(download: Download) {
            setNumberProgressBar()
            (contentRecyclerView.adapter as DownloadListAdapter).apply {
                val list = viewModel.getDownloadListFavoriteCompressed()
                val dataFilteredByUrl = list.filter { it.server + it.linkAcquisition == download.url }
                when (dataFilteredByUrl.isEmpty()) {
                    true -> list.filter { it.getXmlId() == download.group }.forEach { updatePosition(it, download) }
                    false -> dataFilteredByUrl.forEach { updatePosition(it, download) }
                }
            }
        }
    }

    internal fun scrollToTop() = contentRecyclerView.scrollToPosition(0)

    protected fun setNumberProgressBar() = downloadsNumberProgressBar.apply {
        val saveDirectory = File(Settings.DOWNLOAD_SAVE_PATH)
        val freeSpace = saveDirectory.freeSpace
        val totalSpace = saveDirectory.totalSpace
        val usedSpace = totalSpace - freeSpace
        val usedPercentage = (usedSpace * 100f / totalSpace).toInt()

        max = 100
        progress = usedPercentage
        progressValueHidden = true
        suffix = "${freeSpace.toReadable()} ${getString(R.string.downloads_free)}"
        visible()
    }

    protected fun RecyclerView.saveState() {
        recyclerViewState = layoutManager?.onSaveInstanceState()
    }

    protected fun RecyclerView.restoreState() {
        recyclerViewState?.let { layoutManager?.onRestoreInstanceState(it) }
    }

}