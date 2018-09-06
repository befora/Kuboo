package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint

@SuppressLint("Registered")
open class ReaderEpubActivityImpl6_Hardware : ReaderEpubActivityImpl5_Menu() {

    override fun onVolumeDownLongPressed() {
        //do nothing
    }

    override fun onVolumeDownPressed() {
        goToNextPage()
    }

    override fun onVolumeUpLongPressed() {
        //do nothing
    }

    override fun onVolumeUpPressed() {
        goToPreviousPage()
    }

}