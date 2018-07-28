package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.support.v4.view.ViewPager
import android.widget.SeekBar
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.base.custom.LoadingStage
import com.sethchhim.kuboo_client.ui.reader.comic.adapter.ReaderComicAdapter
import timber.log.Timber

@SuppressLint("Registered")
open class ReaderComicActivityImpl2_Content : ReaderComicActivityImpl1_Preview(), ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener {

    protected fun initListeners() {
        viewPager.clearOnPageChangeListeners()
        viewPager.addOnPageChangeListener(this)
        overlaySeekBar.setOnSeekBarChangeListener(this)
    }

    protected fun populateContent() {
        when (Settings.DUAL_PANE) {
            true -> startDualPaneMode()
            false -> startSinglePaneMode()
        }
    }

    protected fun refreshViewpager() {
        val currentItem = viewPager.currentItem
        viewPager.adapter = ReaderComicAdapter(this@ReaderComicActivityImpl2_Content)
        viewPager.currentItem = currentItem
    }

    internal fun onSwipeOutOfBoundsStart() = Timber.i("onSwipeOutOfBoundsStart")

    internal fun onSwipeOutOfBoundsEnd() {
        Timber.i("onSwipeOutOfBoundsEnd")
        val isBannedFromRecent = currentBook.isBannedFromRecent()
        val isLastInSeries = nextBook.isEmpty()
        when (isBannedFromRecent || isLastInSeries) {
            true -> showSnackBarEnd()
            false -> showSnackBarNext()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) = setOverlayPageNumberText(progress)

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        viewPager.currentItem = seekBar.progress
    }

    override fun onPageSelected(position: Int) {
        setOverlayPosition(position)
        saveComicBookmark(position)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    protected fun toggleDualPaneMode() {
        Settings.DUAL_PANE = !Settings.DUAL_PANE
        sharedPrefsHelper.saveDualPane()

        recreate()
    }

    protected fun toggleMangaMode() {
        Settings.RTL = !Settings.RTL
        sharedPrefsHelper.saveRtl()

        recreate()
    }

    private fun startNextBook() {
        viewModel.addFinish(currentBook)

        viewPager.adapter = null

        val nextPreviewUrl = nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)
        previewImageView.transitionName = nextPreviewUrl

        startReader(ReadData(book = nextBook, bookmarksEnabled = false, requestFinish = true, sharedElement = previewImageView, source = source))
        startDownloadTracking(nextBook)
    }

    private fun finishBook() {
        viewModel.addFinish(currentBook)
        finish()
        startDownloadTracking(currentBook)
    }

    private fun startSinglePaneMode() {
        showLoadingDialog(loadingStage = LoadingStage.SINGLE)
        populateSinglePaneList()
        loadPreviewImage()

        onReaderListChanged()
    }

    private fun startDualPaneMode() {
        //populate single list
        showLoadingDialog(loadingStage = LoadingStage.DUAL)
        populateSinglePaneList()
        loadPreviewImage()

        when (viewModel.isReaderDualPaneListEmpty()) {
            true ->
                //populate dual list and observe
                populateDualPaneList().observe(this, Observer { onReaderListChanged() })
            false ->
                //or reuse existing dual list
                onReaderListChanged()
        }
    }

    private fun onReaderListChanged() {
        viewModel.setReaderListType()
        setOverlay(viewModel.getReaderListSize())
        viewPager.adapter = ReaderComicAdapter(this@ReaderComicActivityImpl2_Content)
        viewPager.post {
            val position = viewModel.getReaderPositionByTrueIndex(currentBook.currentPage)
            viewPager.currentItem = position
            saveComicBookmark(position)
        }
        hideLoadingDialog()
    }

    internal fun goToFirstPage(){
        viewPager.currentItem = 0
    }

    internal fun goToLastPage() = viewPager.adapter?.let {
        viewPager.currentItem = it.count - 1
    }

    internal fun goToPreviousPage() = when (viewPager.currentItem == 0) {
        true -> onSwipeOutOfBoundsStart()
        false -> viewPager.currentItem = viewPager.currentItem - 1
    }

    internal fun goToNextPage() = viewPager.adapter?.let {
        when (viewPager.currentItem == it.count - 1) {
            true -> onSwipeOutOfBoundsEnd()
            false -> viewPager.currentItem = viewPager.currentItem + 1
        }
    }

    private fun showSnackBarEnd() {
        snackBarEnd = dialogUtil.getSnackBarFinishBookEnd(constraintLayout).apply {
            setAction(R.string.reader_menu) { onSnackBarEndAction() }
            show()
        }
    }

    protected fun onSnackBarEndAction() {
        finishBook()
    }

    private fun showSnackBarNext() {
        snackBarNext = dialogUtil.getSnackBarFinishBookNext(constraintLayout, nextBook).apply {
            setAction(R.string.reader_read) { onSnackBarNextAction() }
            show()
        }
    }

    protected fun onSnackBarNextAction() {
        startNextBook()
    }

}