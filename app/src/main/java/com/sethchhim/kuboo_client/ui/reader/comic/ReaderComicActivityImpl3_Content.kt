package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.enum.ScaleType
import com.sethchhim.kuboo_client.ui.base.custom.LoadingStage
import com.sethchhim.kuboo_client.ui.reader.comic.adapter.ReaderComicAdapter
import timber.log.Timber
import kotlin.math.roundToInt

@SuppressLint("Registered")
open class ReaderComicActivityImpl3_Content : ReaderComicActivityImpl2_Preview(), ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener {

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
        viewPager.adapter = ReaderComicAdapter(this@ReaderComicActivityImpl3_Content)
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
        setPipDimensions(position)
        setOverlayPosition(position)
        saveComicBookmark(position)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    protected fun toggleDualPaneMode() {
        Settings.DUAL_PANE = !Settings.DUAL_PANE
        sharedPrefsHelper.saveDualPane()
        isPreviewEnabled = false
        startActivity(intent)
        updateDualPaneMenuItemState()
    }

    protected fun toggleMangaMode() {
        Settings.RTL = !Settings.RTL
        sharedPrefsHelper.saveRtl()
        isPreviewEnabled = false
        startActivity(intent)
        updateMangaModeMenuItemState()
    }

    private fun startNextBook() {
        isPreviewEnabled = true

        viewModel.addFinish(currentBook)
        viewModel.clearReaderLists()
        if (isLocal) viewModel.cleanupParser()

        transitionUrl = nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)
        previewImageView.transitionName = transitionUrl

        showNewIntentTransition()
        startDownloadTracking(nextBook)
    }

    private fun finishBook() {
        viewModel.addFinish(currentBook)
        startDownloadTracking(currentBook)
        showExitTransition()
    }

    private fun startSinglePaneMode() {
        showLoadingDialog(loadingStage = LoadingStage.SINGLE)
        populateSinglePaneList()
        loadPreviewImage()
        onReaderListChanged()
    }

    private fun startDualPaneMode() {
        //populate single list
        populateSinglePaneList()
        loadPreviewImage()
        when (viewModel.isReaderDualPaneListEmpty()) {
            true -> {
                //populate dual list and observe
                showLoadingDialog(loadingStage = LoadingStage.DUAL)
                populateDualPaneList().observe(this, Observer { onReaderListChanged() })
            }
            false ->
                //or reuse existing dual list
                onReaderListChanged()
        }
    }

    private fun onReaderListChanged() {
        viewModel.setReaderListType()
        setOverlay(viewModel.getReaderListSize())
        viewPager.adapter = ReaderComicAdapter(this@ReaderComicActivityImpl3_Content)
        val position = viewModel.getReaderPositionByTrueIndex(currentBook.currentPage)
        viewPager.currentItem = position
        saveComicBookmark(position)
        hideLoadingDialog()
    }

    internal fun goToFirstPage() {
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

    protected fun hideSnackBarEnd() {
        snackBarEnd?.let {
            it.view.visibility = View.GONE
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

    protected fun hideSnackBarNext() {
        snackBarNext?.let {
            it.view.visibility = View.GONE
        }
    }

    protected fun onSnackBarNextAction() {
        startNextBook()
    }

    internal fun setPipDimensions(position: Int) {
        viewModel.getReaderDimensionAt(position)?.let {
            pipPosition = position
            pipWidth = it.width
            pipHeight = it.height

            val mediumPercentage = 62.58
            val smallPercentage = 74.77
            Timber.d("PipDimensions: position[$pipPosition] width[$pipWidth] widthMedium[${pipWidth.calculatePercentage(mediumPercentage)}] widthSmall[${pipWidth.calculatePercentage(smallPercentage)}] height[$pipHeight] heightMedium[${pipHeight.calculatePercentage(mediumPercentage)}] heightSmall[${pipHeight.calculatePercentage(smallPercentage)}]")
        }
    }

    protected fun setScaleType(menuItem: MenuItem, scaleType: Int) {
        menuItem.isChecked = true
        when (scaleType) {
            0 -> Settings.SCALE_TYPE = ScaleType.ASPECT_FILL.value
            1 -> Settings.SCALE_TYPE = ScaleType.ASPECT_FIT.value
            2 -> Settings.SCALE_TYPE = ScaleType.FIT_WIDTH.value
        }
        sharedPrefsHelper.saveScaleType()
        viewPager.adapter?.notifyDataSetChanged()
    }

}

private fun Int.calculatePercentage(percentage: Double): Int {
    return (this - (this * (percentage / 100))).roundToInt()
}