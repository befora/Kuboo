package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import com.sethchhim.epublibdroid_kotlin.custom.EpubReaderListener
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.model.ReadData
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import java.io.File

@SuppressLint("Registered")
open class ReaderEpubActivityImpl3_Content : ReaderEpubActivityImpl2_Overlay(), EpubReaderListener {

    lateinit var file: File

    protected fun populateContent() {
        file = File(currentBook.filePath)
        when (file.exists()) {
            true -> onFileIsValid()
            false -> onFileIsNotValid()
        }
    }

    override fun onPositionLoading() = setStateLoading()

    override fun onLoadPositionSuccess() = setStateSuccess()

    override fun onTextSelectionModeChangeListener(mode: Boolean?) {}

    override fun onPageChangeListener(ChapterNumber: Int, PageNumber: Int, ProgressStart: Float, ProgressEnd: Float) {
        val progressMid = (ProgressStart + ProgressEnd) / 2
        saveEpubBookmark(ChapterNumber, progressMid)
    }

    override fun onLinkClicked(url: String) {}

    override fun onBookStartReached() {}

    override fun onBookEndReached() {
        val isBannedFromRecent = currentBook.isBannedFromRecent()
        val isLastInSeries = nextBook.isEmpty()
        when (isBannedFromRecent || isLastInSeries) {
            true -> showSnackBarEnd()
            false -> showSnackBarNext()
        }
    }

    override fun onClickEpubReaderView() = showOverlay()

    override fun goToPreviousPage() = epubReaderView.goToPreviousPage()

    override fun goToNextPage() = epubReaderView.goToNextPage()

    override fun onLongPressEpubReaderView() {}

    private fun onFileIsValid() {
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
        saveEpubBookmark(chapter, progress)
        setOverlay(-1, -1)
    }

    private fun onFileIsNotValid() {
        toast("File not found!")
        finish()
    }

    override fun startNextBook() {
        viewModel.addFinish(currentBook)
        deleteFinishedFetchDownload(currentBook)
        startDownloadTracking(nextBook)
        startReader(ReadData(book = nextBook, bookmarksEnabled = false, sharedElement = null, source = source))
        finish()
    }

    override fun finishBook() {
        viewModel.addFinish(currentBook)
        startDownloadTracking(currentBook)
        exitActivity()
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

}