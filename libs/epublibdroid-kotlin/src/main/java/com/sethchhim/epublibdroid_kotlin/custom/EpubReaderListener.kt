package com.sethchhim.epublibdroid_kotlin.custom

interface EpubReaderListener {
    fun OnPageChangeListener(ChapterNumber: Int, PageNumber: Int, ProgressStart: Float, ProgressEnd: Float)

    fun OnTextSelectionModeChangeListener(mode: Boolean?)

    fun OnLinkClicked(url: String)

    fun OnBookStartReached()

    fun OnBookEndReached()

    fun OnPositionLoading()

    fun OnLoadPositionSuccess()

    fun onClickEpubReaderView()

    fun onLongPressEpubReaderView()
}