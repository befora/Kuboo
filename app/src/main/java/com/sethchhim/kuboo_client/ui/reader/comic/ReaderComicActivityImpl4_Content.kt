package com.sethchhim.kuboo_client.ui.reader.comic

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.widget.SeekBar
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.enum.ScaleType
import com.sethchhim.kuboo_client.ui.base.custom.LoadingStage
import com.sethchhim.kuboo_client.ui.reader.comic.adapter.ReaderComicAdapter
import timber.log.Timber
import kotlin.math.roundToInt

@SuppressLint("Registered")
open class ReaderComicActivityImpl4_Content : ReaderComicActivityImpl3_Menu() {

    protected fun initListeners() {
        viewPager.clearOnPageChangeListeners()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                setPipDimensions(position)
                setOverlayPosition(position)
                saveComicBookmark(position)
            }
        })
        overlaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = setOverlayPageNumberText(progress)

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewPager.currentItem = seekBar.progress
            }
        })
    }

    protected fun populateContent() {
        when (Settings.DUAL_PANE) {
            true -> startDualPaneMode()
            false -> startSinglePaneMode()
        }
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
        viewPager.adapter = ReaderComicAdapter(this@ReaderComicActivityImpl4_Content)
        val position = viewModel.getReaderPositionByTrueIndex(currentBook.currentPage)
        viewPager.currentItem = position
        saveComicBookmark(position)
        setOverlay(viewPager.currentItem, viewModel.getReaderListSize())
        hideLoadingDialog()
    }

    protected fun refreshViewpager() {
        val currentItem = viewPager.currentItem
        viewPager.adapter = ReaderComicAdapter(this@ReaderComicActivityImpl4_Content)
        viewPager.currentItem = currentItem
    }

    override fun goToFirstPage() {
        viewPager.currentItem = 0
    }

    override fun goToLastPage() {
        viewPager.adapter?.let {
            viewPager.currentItem = it.count - 1
        }
    }

    override fun goToPreviousPage() {
        when (viewPager.currentItem == 0) {
            true -> onSwipeOutOfBoundsStart()
            false -> viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    override fun goToNextPage() {
        viewPager.adapter?.let {
            when (viewPager.currentItem == it.count - 1) {
                true -> onSwipeOutOfBoundsEnd()
                false -> viewPager.currentItem = viewPager.currentItem + 1
            }
        }
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

    private fun Int.calculatePercentage(percentage: Double) = (this - (this * (percentage / 100))).roundToInt()

}