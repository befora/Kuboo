package com.sethchhim.kuboo_client.ui.reader.pdf

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.artifex.mupdf.mini.DocumentActivity
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import dagger.android.AndroidInjection
import org.jetbrains.anko.collections.forEachWithIndex
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

class ReaderPdfActivity : DocumentActivity() {

    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    private lateinit var currentBook: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        forceOrientationSetting()
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        currentBook = intent.getParcelableExtra(Constants.ARG_BOOK)
    }

    override fun onLoadPageSuccess() {
        super.onLoadPageSuccess()
        saveBookmark()
    }

    private fun saveBookmark() {
        currentBook.currentPage = currentPage
        currentBook.totalPages = pageCount
        intent.putExtra(Constants.ARG_BOOK, currentBook)
        viewModel.putRemoteUserApi(currentBook)
        viewModel.addRecent(currentBook)
    }

    private fun forceOrientationSetting() {
        when (Settings.SCREEN_ORIENTATION) {
            0 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            1 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            2 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        }
    }

    private fun printOutline() {
        flatOutline?.forEachWithIndex { i, item ->
            Timber.d("$i  ${item.title} page[${item.page}] total[${item.totalPages}]")
        } ?: Timber.e("Outline is null")
    }

    private fun formatDecimal(double: Double) = DecimalFormat("####0.000000000000000000").format(double)

}