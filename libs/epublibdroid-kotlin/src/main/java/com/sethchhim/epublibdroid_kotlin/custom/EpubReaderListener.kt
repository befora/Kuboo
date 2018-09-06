package com.sethchhim.epublibdroid_kotlin.custom

interface EpubReaderListener {
    fun onPageChangeListener(ChapterNumber: Int, PageNumber: Int, ProgressStart: Float, ProgressEnd: Float)

    fun onTextSelectionModeChangeListener(mode: Boolean?)

    fun onLinkClicked(url: String)

    fun onBookStartReached()

    fun onBookEndReached()

    fun onPositionLoading()

    fun onLoadPositionSuccess()

    fun onClickEpubReaderView()

    fun onLongPressEpubReaderView()
}