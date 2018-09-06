package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint

@SuppressLint("Registered")
open class ReaderComicActivityImpl6_Hardware : ReaderComicActivityImpl5_Dialog() {

    override fun onVolumeDownLongPressed() {
        goToLastPage()
    }

    override fun onVolumeDownPressed() {
        when {
            snackBarEnd?.isShownOrQueued ?: false -> onSnackBarEndAction()
            snackBarNext?.isShownOrQueued ?: false -> onSnackBarNextAction()
            else -> goToNextPage()
        }
    }

    override fun onVolumeUpLongPressed() {
        goToFirstPage()
    }

    override fun onVolumeUpPressed() {
        goToPreviousPage()
    }

}