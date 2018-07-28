package com.sethchhim.kuboo_client.ui.reader.book

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.sethchhim.kuboo_client.R
import org.jetbrains.anko.sdk25.coroutines.onClick

class ReaderEpubActivity : ReaderEpubActivityImpl3_Menu() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentUi()
        populateContent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reader_book, menu)
        settingsMenuItem = menu.findItem(R.id.reader_settings)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reader_settings -> showSettingsDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        resetPosition()
    }

    override fun onVolumeDownLongPressed() {
        //do nothing
    }

    override fun onVolumeDownPressed() {
        epubReaderView.goToNextPage()
    }

    override fun onVolumeUpLongPressed() {
        //do nothing
    }

    override fun onVolumeUpPressed() {
        epubReaderView.goToPreviousPage()
    }

    private fun showSettingsDialog() = dialogUtil.getDialogBookSettings(this).apply {
        show()
        hideOverlay()
        findViewById<Button>(R.id.dialog_layout_book_settings_button0)?.onClick { decreaseTextZoom() }
        findViewById<Button>(R.id.dialog_layout_book_settings_button1)?.onClick { increaseTextZoom() }
        findViewById<Button>(R.id.dialog_layout_book_settings_button2)?.onClick { decreaseMargin() }
        findViewById<Button>(R.id.dialog_layout_book_settings_button3)?.onClick { increaseMargin() }
        setOnDismissListener { showOverlay() }
    }

}


