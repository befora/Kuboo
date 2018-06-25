package com.sethchhim.kuboo_client.ui.main

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.support.v7.app.AlertDialog
import android.widget.TextView
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.BrowserBaseFragment
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_remote.model.Book

@SuppressLint("Registered")
open class MainActivityImpl2_Selection : MainActivityImpl1_Content() {

    internal fun enableSelectionMode(holder: BrowserContentAdapter.BrowserHolder, book: Book) {
        when (viewModel.isSelected(book)) {
            true -> {
                viewModel.removeSelected(book)
                enableSelectionMenuState()
                loadColorState(holder, book)
            }
            false -> {
                viewModel.addSelected(book)
                enableSelectionMenuState()
                loadColorState(holder, book)
            }
        }
    }

    internal fun disableSelectionMode() {
        viewModel.clearSelected()
        enableSelectionMenuState()
        resetAllColorState()
    }

    internal fun enableSelectionMenuState() {
        when (viewModel.isSelectedListEmpty()) {
            true -> setSelectionMenuStateUnselected()
            false -> setSelectionMenuStateSelected()
        }
    }

    internal fun disableSelectionMenuState() {
        downloadMenuItem.gone()
        markFinishedDeleteMenuItem.gone()
        markFinishedAddMenuItem.gone()
    }

    protected fun startSelectionDownload(): Boolean {
        val selectedList = viewModel.getSelectedList()
        dialogUtil.getDialogDownloadStart(this, selectedList).apply {
            setButton(AlertDialog.BUTTON_POSITIVE, "${context.getString(R.string.dialog_download)} (${selectedList.size})") { _, _ ->
                viewModel.startDownloads(selectedList, savePath = Settings.DOWNLOAD_SAVE_PATH)
                disableSelectionMode()
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel)) { dialog, _ -> dialog.dismiss() }
            show()

            findViewById<TextView>(android.R.id.message)?.apply {
                textSize = 11F
            }
        }
        return true
    }

    protected fun startSelectionAddFinished(): Boolean {
        viewModel.addFinishFromSelectedList().observe(this, Observer { result ->
            result?.let { if (it) disableSelectionMode() }
        })

        if (!Settings.MARK_FINISHED) dialogUtil.getSnackBarMarkFinished(frameLayout).apply {
            setAction(R.string.main_settings) { showFragmentSettings() }
            show()
        }
        return true
    }

    protected fun startSelectionDeleteFinished(): Boolean {
        viewModel.removeFinishFromSelectedList().observe(this, Observer { result ->
            result?.let { if (it) disableSelectionMode() }
        })
        return true
    }

    private fun loadColorState(holder: BrowserContentAdapter.BrowserHolder, book: Book) = (getCurrentFragment() as? BrowserBaseFragment)?.contentAdapter?.loadColorState(holder, book)

    private fun resetAllColorState() = (getCurrentFragment() as? BrowserBaseFragment)?.contentAdapter?.resetAllColorState()

    private fun setSelectionMenuStateSelected() {
        title = getSelectedBrowserTitle()
        downloadMenuItem.visible()
        searchMenuItem.gone()
        markFinishedDeleteMenuItem.visible()
        markFinishedAddMenuItem.visible()

        hideMenuItemHttps()
    }

    private fun setSelectionMenuStateUnselected() {
        title = getString(R.string.main_browse)
        searchMenuItem.visible()
        downloadMenuItem.gone()
        markFinishedDeleteMenuItem.gone()
        markFinishedAddMenuItem.gone()

        toggleMenuItemHttps()
    }

}