package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.OutlineItem
import com.sethchhim.kuboo_client.ui.reader.base.ReaderBaseActivity
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderViewPagerImpl1_Edge
import timber.log.Timber
import java.util.*

@SuppressLint("Registered")
open class ReaderPdfActivityImpl0_View : ReaderBaseActivity() {

    protected var isReflowable: Boolean = false
    protected var flatOutline: ArrayList<OutlineItem>? = null
    protected var layoutW: Float = 0.toFloat()
    protected var layoutH: Float = 0.toFloat()

    protected lateinit var viewPager: ReaderViewPagerImpl1_Edge

    protected var currentPage: Int = 0

    protected var isRequestRelay = false

    protected fun initUi() {
        previewImageView.isAnimatingTransition = false
        previewImageView.gone()

        val contentView = layoutInflater.inflate(R.layout.reader_layout_pdf_content, null, false)
        contentFrameLayout.removeAllViews()
        contentFrameLayout.addView(contentView)

        viewPager = findViewById(R.id.reader_layout_pdf_content_readerViewPagerImpl1_Edge)
    }

    override fun onSwipeOutOfBoundsStart() = Timber.i("onSwipeOutOfBoundsStart")

    override fun onSwipeOutOfBoundsEnd() {
        Timber.i("onSwipeOutOfBoundsEnd")
        val isBannedFromRecent = currentBook.isBannedFromRecent()
        val isLastInSeries = nextBook.isEmpty()
        when (isBannedFromRecent || isLastInSeries) {
            true -> showSnackBarEnd()
            false -> showSnackBarNext()
        }
    }

}