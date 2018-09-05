package com.sethchhim.kuboo_client.ui.reader.pdf

import android.graphics.Bitmap
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.artifex.mupdf.fitz.Rect
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.github.ybq.android.spinkit.SpinKitView
import com.sethchhim.kuboo_client.Constants.ARG_BOOK
import com.sethchhim.kuboo_client.Constants.ARG_LOCAL
import com.sethchhim.kuboo_client.Constants.ARG_POSITION
import com.sethchhim.kuboo_client.Extensions.disable
import com.sethchhim.kuboo_client.Extensions.dismissDelayed
import com.sethchhim.kuboo_client.Extensions.enable
import com.sethchhim.kuboo_client.Extensions.fadeGone
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderPageImageView
import com.sethchhim.kuboo_remote.model.Book
import org.jetbrains.anko.support.v4.onRefresh
import timber.log.Timber

class ReaderPdfFragmentImpl1_Single : ReaderPdfFragment() {

    @BindView(R.id.reader_item_comic_single_panel1_readerPageImageView) lateinit var imageView: ReaderPageImageView
    @BindView(R.id.reader_item_comic_single_panel1_spinKitView) lateinit var spinKitView: SpinKitView
    @BindView(R.id.reader_item_fail_swipeRefreshLayout1) lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.reader_item_fail_constraintLayout1) lateinit var failConstraintLayout: ConstraintLayout
    @BindView(R.id.reader_item_fail_textView1) lateinit var failTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reader_layout_comic_single_content, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView.loadImage()
        swipeRefreshLayout.setColorSchemeResources(R.color.lightColorAccent)
        swipeRefreshLayout.onRefresh { imageView.loadImage() }
    }

    protected fun ImageView.loadImage() {
        appExecutors.diskIO.execute {
            val bitmap: Bitmap?
            val hits: Array<Rect>
            try {
                val page = readerPdfActivity.document.loadPage(position)
                val matrix = when (readerPdfActivity.fitPage) {
                    true -> AndroidDrawDevice.fitPage(page, readerPdfActivity.canvasW, readerPdfActivity.canvasH)
                    false -> AndroidDrawDevice.fitPageWidth(page, readerPdfActivity.canvasW)
                }
                bitmap = AndroidDrawDevice.drawPage(page, matrix)

                if (readerPdfActivity.searchNeedle != null) {
                    hits = page.search(readerPdfActivity.searchNeedle)
                    if (hits != null)
                        for (hit in hits)
                            hit.transform(matrix)
                }
                if (bitmap != null) {
                    appExecutors.mainThread.execute { onLoadImageSuccess(bitmap) }
                }
            } catch (e: Exception) {
                appExecutors.mainThread.execute { onLoadImageFail(e.message) }
            }
        }
    }

    private fun onLoadImageSuccess(bitmap: Bitmap) {
        Timber.d("onLoadImageSuccess")
        imageView.setImageBitmap(bitmap)
        spinKitView.fadeGone()
        imageView.fadeVisible()

        failConstraintLayout.fadeGone()
        swipeRefreshLayout.dismissDelayed()
        swipeRefreshLayout.disable()
    }

    private fun onLoadImageFail(message: String?) {
        Timber.d("onLoadImageFail $message")
        spinKitView.fadeGone()
        imageView.fadeGone()

        val swipeText = "Swipe down to refresh!"
        val reasonText = message?.let { it } ?: "Failed to load image!"
        failTextView.text = "$swipeText\n$reasonText"
        failConstraintLayout.fadeVisible()
        swipeRefreshLayout.dismissDelayed()
        swipeRefreshLayout.enable()
    }

    companion object {
        fun newInstance(book: Book, isLocal: Boolean, position: Int): ReaderPdfFragmentImpl1_Single {
            return ReaderPdfFragmentImpl1_Single().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_BOOK, book)
                    putBoolean(ARG_LOCAL, isLocal)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

}