package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.Progress
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

@SuppressLint("Registered")
open class ReaderBaseActivityImpl2_Dialog : ReaderBaseActivityImpl1_Preview() {

    protected var snackBarEnd: Snackbar? = null
    protected var snackBarNext: Snackbar? = null

    protected fun showDialogInfo(book: Book) {
        dialogUtil.getDialogInfo(this).apply {
            val title = when (book.isRemote()) {
                true -> context.getString(R.string.dialog_remote_file)
                false -> context.getString(R.string.dialog_local_file)
            }
            setTitle(title)

            val message = when (book.isRemote()) {
                true -> book.getAcquisitionUrl()
                false -> book.filePath
            }
            setMessage(message)

            this.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_copy_to_clipboard)) { _, _ ->
                systemUtil.copyToClipboard(message)
            }

            val isShowOpenSeries = book.server == viewModel.getActiveServer()
            if (isShowOpenSeries) this.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.dialog_open_series)) { dialog, _ ->
                dialog.dismiss()
                Intent(this@ReaderBaseActivityImpl2_Dialog, MainActivity::class.java).apply {
                    putExtra(Constants.ARG_REQUEST_REMOTE_BROWSER_FRAGMENT, true)
                    putExtra(Constants.ARG_REQUEST_REMOTE_BROWSER_FRAGMENT_PAYLOAD, currentBook)
                    this@ReaderBaseActivityImpl2_Dialog.startActivity(this)
                }
            }
            show()

            findViewById<TextView>(android.R.id.message)?.apply { textSize = 11F }
        }
    }

    protected fun showSnackBarEnd() {
        snackBarEnd = dialogUtil.getSnackBarFinishBookEnd(constraintLayout).apply {
            setAction(R.string.reader_menu) { onSnackBarEndAction() }
            show()
        }
    }

    protected fun hideSnackBarEnd() {
        snackBarEnd?.let {
            it.view.visibility = View.GONE
        }
    }

    protected fun showSnackBarNext() {
        snackBarNext = dialogUtil.getSnackBarFinishBookNext(constraintLayout, nextBook).apply {
            setAction(R.string.reader_read) { onSnackBarNextAction() }
            show()
        }
    }

    protected fun hideSnackBarNext() {
        snackBarNext?.let {
            it.view.visibility = View.GONE
        }
    }

    open fun onSnackBarEndAction() {
        //override in children
    }

    open fun onSnackBarNextAction() {
        //override in children
    }

    protected fun handleDualPaneLoadingProgress(progress: Progress) {
        Timber.d("progress ${progress.position} ${progress.total}")
        if (loadingDialog.isShowing) {
            val textView = loadingDialog.findViewById<TextView>(android.R.id.message)
            textView?.let {
                val message = "${getString(R.string.dialog_loading_dual_pane_mode)} ${progress.position} of ${progress.total}"
                it.text = message
            }
        }
    }

}