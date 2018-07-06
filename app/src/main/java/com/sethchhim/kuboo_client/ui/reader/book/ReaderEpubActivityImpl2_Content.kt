package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import android.util.Log
import com.sethchhim.epublibdroid_kotlin.custom.EpubReaderListener
import com.sethchhim.kuboo_client.Settings
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.io.File

@SuppressLint("Registered")
open class ReaderEpubActivityImpl2_Content : ReaderEpubActivityImpl1_Preview(), EpubReaderListener {

    lateinit var file: File

    protected fun populateContent() {
        file = File(currentBook.filePath)
        when (file.exists()) {
            true -> loadBook()
            false -> onFileNotFound()
        }
    }

    override fun OnPositionLoading() {
        Log.d("EpubReader", "OnPositionLoading")
        setStateLoading()
    }

    override fun OnLoadPositionSuccess() {
        Log.d("EpubReader", "OnLoadPositionSuccess")
        setStateSuccess()
    }

    override fun OnTextSelectionModeChangeListener(mode: Boolean?) {
        Log.d("EpubReader", "TextSelectionMode$mode ")
    }

    override fun OnPageChangeListener(ChapterNumber: Int, PageNumber: Int, ProgressStart: Float, ProgressEnd: Float) {
        Log.d("EpubReader", "PageChange: Chapter:$ChapterNumber PageNumber:$PageNumber ProgressStart:$ProgressStart ProgressEnd:$ProgressEnd TotalContentHeight[${epubReaderView.getTotalContentHeight()}]")
        val progressMid = (ProgressStart + ProgressEnd) / 2
        saveEpubBookmark(ChapterNumber, progressMid)
    }

    override fun OnLinkClicked(url: String) {
        Log.d("EpubReader", "LinkClicked:$url ")
    }

    override fun OnBookStartReached() {
        Log.d("EpubReader", "StartReached")
    }

    override fun OnBookEndReached() {
        Log.d("EpubReader", "EndReached")
    }

    override fun onClickEpubReaderView() {
        Timber.d("onClickEpubReaderView")
        showOverlay()
    }

    override fun onLongPressEpubReaderView() {
        Timber.d("onLongPressEpubReaderView")
    }

    protected fun decreaseTextZoom() {
        Settings.EPUB_TEXT_ZOOM -= 5
        sharedPrefsHelper.saveEpubTextZoom()
        epubReaderView.applyTextZoom(Settings.EPUB_TEXT_ZOOM)
    }

    protected fun increaseTextZoom() {
        Settings.EPUB_TEXT_ZOOM += 5
        sharedPrefsHelper.saveEpubTextZoom()
        epubReaderView.applyTextZoom(Settings.EPUB_TEXT_ZOOM)
    }

    protected fun decreaseMargin() {
        Settings.EPUB_MARGIN_SIZE -= 4
        sharedPrefsHelper.saveEpubMarginSize()
        epubReaderView.setMargin(Settings.EPUB_MARGIN_SIZE)
        epubReaderView.applyMargin(Settings.EPUB_MARGIN_SIZE)
    }

    protected fun increaseMargin() {
        Settings.EPUB_MARGIN_SIZE += 5
        sharedPrefsHelper.saveEpubMarginSize()
        epubReaderView.setMargin(Settings.EPUB_MARGIN_SIZE)
        epubReaderView.applyMargin(Settings.EPUB_MARGIN_SIZE)
    }

    protected fun resetPosition() {
        launch(UI) {
            setStateLoading()
            delay(500)
            epubReaderView.scrollToCurrentPosition()
            setStateSuccess()
        }
    }

    private fun loadBook() {
        epubReaderView.openEpubFile(file.path)
        loadPreviewImage()
        epubReaderView.restoreSettings(
                backgroundColor = Settings.EPUB_BACKGROUND_COLOR,
                fontColor = Settings.EPUB_FONT_COLOR,
                fontPath = Settings.EPUB_FONT_PATH,
                lineHeight = Settings.EPUB_LINE_HEIGHT,
                marginSize = Settings.EPUB_MARGIN_SIZE,
                textZoom = Settings.EPUB_TEXT_ZOOM)
        epubReaderView.epubReaderListener = this

        val chapter = getChapterFromEpubBookmark(currentBook.bookMark)
        val progress = getProgressFromEpubBookmark(currentBook.bookMark)
        epubReaderView.loadPosition(chapter, progress)
    }

}