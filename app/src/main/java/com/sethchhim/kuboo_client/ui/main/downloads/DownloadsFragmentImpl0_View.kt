package com.sethchhim.kuboo_client.ui.main.downloads

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.sethchhim.kuboo_client.Extensions.toReadable
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.BrowserBaseFragment
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadListAdapter
import com.tonyodev.fetch2.Download
import java.io.File

open class DownloadsFragmentImpl0_View : BrowserBaseFragment() {

    private var recyclerViewState: Parcelable? = null

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

    protected fun updateContent(download: Download) {
        setNumberProgressBar()
        (contentRecyclerView.adapter as DownloadListAdapter).apply {
            val dataFilteredByUrl = list.filter { it.server + it.linkAcquisition == download.url }
            when (dataFilteredByUrl.isEmpty()) {
                true -> list.filter { it.getXmlId() == download.group }.forEach { updatePosition(it, download) }
                false -> dataFilteredByUrl.forEach { updatePosition(it, download) }
            }
        }
    }

    protected fun androidx.recyclerview.widget.RecyclerView.saveState() {
        recyclerViewState = layoutManager?.onSaveInstanceState()
    }

    protected fun androidx.recyclerview.widget.RecyclerView.restoreState() {
        recyclerViewState?.let { layoutManager?.onRestoreInstanceState(it) }
    }

}