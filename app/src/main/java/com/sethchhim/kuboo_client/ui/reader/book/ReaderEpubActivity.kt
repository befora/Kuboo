package com.sethchhim.kuboo_client.ui.reader.book

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sethchhim.kuboo_client.R

open class ReaderEpubActivity : ReaderEpubActivityImpl6_Hardware() {

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        resetPosition()
    }

}


