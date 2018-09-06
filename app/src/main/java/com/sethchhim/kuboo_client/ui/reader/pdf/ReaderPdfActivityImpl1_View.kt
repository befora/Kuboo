package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.OutlineItem
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderViewPagerImpl1_Edge
import java.util.*

@SuppressLint("Registered")
open class ReaderPdfActivityImpl1_View : ReaderPdfActivityImpl0_Hardware() {

    private val APP = "MuPDF"

    val NAVIGATE_REQUEST = 1

    protected var key: String? = null
    protected var path: String? = null
    protected var mimetype: String? = null

    protected var isReflowable: Boolean = false
    var fitPage: Boolean = true
    protected var title: String? = null
    protected var flatOutline: ArrayList<OutlineItem>? = null
    protected var layoutW: Float = 0.toFloat()
    protected var layoutH: Float = 0.toFloat()
    var canvasW: Int = 0
    var canvasH: Int = 0

    protected lateinit var viewPager: ReaderViewPagerImpl1_Edge

    protected var currentPage: Int = 0
    protected var searchHitPage: Int = 0
    var searchNeedle: String? = null
    protected var stopSearch: Boolean = false

    protected var isRequestRelay = false

    protected fun initContentUi() {
        previewImageView.isAnimatingTransition = false
        previewImageView.gone()
        canvasW = systemUtil.getSystemWidth()
        canvasH = systemUtil.getSystemHeight()

        val contentView = layoutInflater.inflate(R.layout.reader_layout_pdf_content, null, false)
        contentFrameLayout.removeAllViews()
        contentFrameLayout.addView(contentView)

        viewPager = findViewById(R.id.reader_layout_pdf_content_readerViewPagerImpl1_Edge)
    }

}