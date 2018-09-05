package com.sethchhim.kuboo_client.ui.reader.pdf

import android.os.Bundle
import org.jetbrains.anko.collections.forEachWithIndex
import timber.log.Timber
import java.text.DecimalFormat

class ReaderPdfActivity : ReaderPdfActivityImpl2_Content() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportStartPostponedEnterTransition()
        initContentUi()
        initListeners()
        populateContent()
    }

    override fun onVolumeDownLongPressed() {
        goToLastPage()
    }

    override fun onVolumeDownPressed() {
        goToNextPage()
    }

    override fun onVolumeUpLongPressed() {
        goToFirstPage()
    }

    override fun onVolumeUpPressed() {
        goToPreviousPage()
    }

    private fun printOutline() {
        flatOutline?.forEachWithIndex { i, item ->
            Timber.d("$i  ${item.title} currentPage[${item.currentPage}] total[${item.totalPages}]")
        } ?: Timber.e("Outline is null")
    }

    private fun formatDecimal(double: Double) = DecimalFormat("####0.000000000000000000").format(double)

}