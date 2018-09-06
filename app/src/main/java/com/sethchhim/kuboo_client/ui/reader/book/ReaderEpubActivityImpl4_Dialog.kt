package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import android.widget.Button
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.ReadData
import org.jetbrains.anko.sdk25.coroutines.onClick

@SuppressLint("Registered")
open class ReaderEpubActivityImpl4_Dialog : ReaderEpubActivityImpl3_Content() {

    protected fun showSettingsDialog() {
        dialogUtil.getDialogBookSettings(this).apply {
            show()
            hideOverlay()
            findViewById<Button>(R.id.dialog_layout_book_settings_button0)?.onClick { decreaseTextZoom() }
            findViewById<Button>(R.id.dialog_layout_book_settings_button1)?.onClick { increaseTextZoom() }
            findViewById<Button>(R.id.dialog_layout_book_settings_button2)?.onClick { decreaseMargin() }
            findViewById<Button>(R.id.dialog_layout_book_settings_button3)?.onClick { increaseMargin() }
            setOnDismissListener { showOverlay() }
        }
    }

    override fun onSnackBarEndAction() {
        finishBook()
    }

    override fun onSnackBarNextAction() {
        startNextBook()
    }

    override fun startNextBook() {
        viewModel.addFinish(currentBook)
        startDownloadTracking(nextBook)
        finish()
        startReader(ReadData(book = nextBook, bookmarksEnabled = false, sharedElement = null, source = source))
    }

    override fun finishBook() {
        viewModel.addFinish(currentBook)
        startDownloadTracking(currentBook)
        exitActivity()
    }

}