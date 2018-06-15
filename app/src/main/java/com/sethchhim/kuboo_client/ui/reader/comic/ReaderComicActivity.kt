package com.sethchhim.kuboo_client.ui.reader.comic

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings

class ReaderComicActivity : ReaderComicActivityImpl3_Menu() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentUi()
        initListeners()
        populateContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isLocal) viewModel.cleanupParser()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reader_comic, menu)

        aspectFillMenuItem = menu.findItem(R.id.reader_aspect_fill)
        aspectFitMenuItem = menu.findItem(R.id.reader_aspect_fit)
        fitWidthMenuItem = menu.findItem(R.id.reader_aspect_fit_width)
        dualPaneMenuItem = menu.findItem(R.id.reader_dual_pane_mode)
        localMenuItem = menu.findItem(R.id.reader_local)
        mangaModeMenuItem = menu.findItem(R.id.reader_manga_mode)

        when (isLocal) {
            true -> localMenuItem.isVisible = true
            false -> localMenuItem.isVisible = false
        }

        when (Settings.SCALE_TYPE) {
            0 -> aspectFillMenuItem.isChecked = true
            1 -> aspectFitMenuItem.isChecked = true
            2 -> fitWidthMenuItem.isChecked = true
        }

        updateDualPaneMenuItemState()
        updateMangaModeMenuItemState()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reader_aspect_fill -> setScaleType(item, 0)
            R.id.reader_aspect_fit -> setScaleType(item, 1)
            R.id.reader_aspect_fit_width -> setScaleType(item, 2)
            R.id.reader_dual_pane_mode -> onSelectDualPane()
            R.id.reader_manga_mode -> onSelectMangaMode()
            R.id.reader_local -> showOfflineDialog(currentBook)
        }
        return super.onOptionsItemSelected(item)
    }


}