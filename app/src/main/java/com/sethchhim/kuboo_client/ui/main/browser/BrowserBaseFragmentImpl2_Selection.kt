package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.support.v7.app.AlertDialog
import android.widget.TextView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings

open class BrowserBaseFragmentImpl2_Selection : BrowserBaseFragmentImpl1_Content() {

    protected fun startSelectionDownload(): Boolean {
        val selectedList = viewModel.getSelectedList()
        dialogUtil.getDialogDownloadStart(mainActivity, selectedList).apply {
            setButton(AlertDialog.BUTTON_POSITIVE, "${context.getString(R.string.dialog_download)} (${selectedList.size})") { _, _ ->
                viewModel.startDownloads(selectedList, savePath = Settings.DOWNLOAD_SAVE_PATH)
                contentAdapter.disableSelectionMode()
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
            result?.let { if (it) contentAdapter.disableSelectionMode() }
        })

        if (!Settings.MARK_FINISHED) dialogUtil.getSnackBarMarkFinished(contentSwipeRefreshLayout).apply {
            setAction(R.string.main_settings) { mainActivity.showFragmentSettings() }
            show()
        }
        return true
    }

    protected fun startSelectionDeleteFinished(): Boolean {
        viewModel.removeFinishFromSelectedList().observe(this, Observer { result ->
            result?.let { if (it) contentAdapter.disableSelectionMode() }
        })
        return true
    }

}