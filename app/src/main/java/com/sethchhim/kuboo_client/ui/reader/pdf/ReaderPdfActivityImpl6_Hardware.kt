package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint

@SuppressLint("Registered")
open class ReaderPdfActivityImpl6_Hardware : ReaderPdfActivityImpl5_Dialog() {

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

}