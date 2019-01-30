package com.sethchhim.kuboo_client.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.browser.*
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_remote.model.Book
import java.lang.Exception

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
        updateBrowserAdapter()
        disableSelectionMenuState()
    }

    internal fun enableSelectionMenuState() {
        when (viewModel.isSelectedListEmpty()) {
            true -> setSelectionMenuStateUnselected()
            false -> setSelectionMenuStateSelected()
        }
    }

    internal fun disableSelectionMenuState() {
        setSelectionMenuStateUnselected()
    }

    internal fun selectAll() {
        addAllFromCurrentViewHolderToSelectedList()
        updateBrowserAdapter()
        enableSelectionMenuState()
    }

    private fun updateBrowserAdapter() {
        try {
            (getCurrentFragment() as? BrowserBaseFragment)?.contentAdapter?.resetAllColorState()
        } catch (e: UninitializedPropertyAccessException) {
            //do nothing
        }
    }

    private fun addAllFromCurrentViewHolderToSelectedList() {
        try {
            (getCurrentFragment() as? BrowserBaseFragment)?.contentAdapter?.let {
                it.data.forEach { viewModel.addSelected(it.book) }
            }
        } catch (e: UninitializedPropertyAccessException) {
            //do nothing
        }
    }

    protected fun startSelectionDownload(): Boolean {
        val selectedList = viewModel.getSelectedList()
        dialogUtil.getDialogDownloadStart(this, selectedList).apply {
            setButton(AlertDialog.BUTTON_POSITIVE, "${context.getString(R.string.dialog_download)} (${selectedList.size})") { _, _ ->
                viewModel.startFetchDownloads(viewModel.getActiveLogin(), selectedList, savePath = Settings.DOWNLOAD_SAVE_PATH)
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

    private fun getBrowserContentType() = (getCurrentFragment() as? BrowserBaseFragment)?.contentRecyclerView?.contentType

    private fun setSelectionMenuStateSelected() {
        title = getSelectedBrowserTitle()
        hideMenuItemAbout()
        hideMenuItemBrowserLayout()
        hideMenuItemHttps()
        hideMenuItemSearch()

        showMenuItemDownload()
        showMenuItemSelectAll()
        showMenuItemMarkFinishedAdd()
        showMenuItemMarkFinishedDelete()
    }

    private fun setSelectionMenuStateUnselected() {
        title = getString(R.string.main_browse)
        hideMenuItemDownload()
        hideMenuItemMarkFinishedAdd()
        hideMenuItemMarkFinishedDelete()
        hideMenuItemSelectAll()

        showMenuItemAbout()
        showMenuItemSearch()

        getBrowserContentType()?.let { toggleMenuItemBrowserLayout(it) }
        toggleMenuItemHttps()
    }

}