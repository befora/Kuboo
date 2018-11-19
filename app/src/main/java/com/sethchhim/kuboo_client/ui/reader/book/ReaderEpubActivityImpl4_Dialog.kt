package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import android.widget.Button
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.ReadData

@SuppressLint("Registered")
open class ReaderEpubActivityImpl4_Dialog : ReaderEpubActivityImpl3_Content() {

    protected fun showSettingsDialog() {
        dialogUtil.getDialogBookSettings(this).apply {
            show()
            hideOverlay()
            findViewById<Button>(R.id.dialog_layout_book_settings_button0)?.setOnClickListener { decreaseTextZoom() }
            findViewById<Button>(R.id.dialog_layout_book_settings_button1)?.setOnClickListener { increaseTextZoom() }
            findViewById<Button>(R.id.dialog_layout_book_settings_button2)?.setOnClickListener { decreaseMargin() }
            findViewById<Button>(R.id.dialog_layout_book_settings_button3)?.setOnClickListener { increaseMargin() }
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
        super.startNextBook()
        finish()
        startReader(ReadData(book = nextBook, bookmarksEnabled = false, sharedElement = null, source = source))
    }

    override fun finishBook() {
        super.finishBook()
        startDownloadTracking(currentBook)
        exitActivity()
    }

}