package com.sethchhim.kuboo_client.ui.reader.comic

import android.graphics.Bitmap
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Constants.ARG_BOOK
import com.sethchhim.kuboo_client.Constants.ARG_LOCAL
import com.sethchhim.kuboo_client.Constants.ARG_POSITION
import com.sethchhim.kuboo_client.Extensions.disable
import com.sethchhim.kuboo_client.Extensions.dismissDelayed
import com.sethchhim.kuboo_client.Extensions.enable
import com.sethchhim.kuboo_client.Extensions.fadeGone
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.GlideLocal
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderPageImageView
import com.sethchhim.kuboo_remote.model.Book
import org.jetbrains.anko.support.v4.onRefresh

class ReaderComicFragmentImpl2_Dual : ReaderComicFragment() {

    @BindView(R.id.reader_item_comic_single_panel1_readerPageImageView) lateinit var imageView1: ReaderPageImageView
    @BindView(R.id.reader_item_comic_single_panel1_spinKitView) lateinit var spinKitView1: SpinKitView
    @BindView(R.id.reader_item_fail_swipeRefreshLayout1) lateinit var swipeRefreshLayout1: SwipeRefreshLayout
    @BindView(R.id.reader_item_fail_constraintLayout1) lateinit var failConstraintLayout1: ConstraintLayout
    @BindView(R.id.reader_item_fail_textView1) lateinit var failTextView1: TextView

    @BindView(R.id.reader_item_comic_dual_panel2_readerPageImageView) lateinit var imageView2: ReaderPageImageView
    @BindView(R.id.reader_item_comic_dual_panel2_spinKitView) lateinit var spinKitView2: SpinKitView
    @BindView(R.id.reader_item_fail_swipeRefreshLayout2) lateinit var swipeRefreshLayout2: SwipeRefreshLayout
    @BindView(R.id.reader_item_fail_constraintLayout2) lateinit var failConstraintLayout2: ConstraintLayout
    @BindView(R.id.reader_item_fail_textView2) lateinit var failTextView2: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reader_layout_comic_dual_content, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout1.setColorSchemeResources(R.color.lightColorAccent)
        swipeRefreshLayout2.setColorSchemeResources(R.color.lightColorAccent)

        val page1 = getPage1()
        val page2 = getPage2()
        when (isLocal) {
            true -> {
                if (page1 != Constants.KEY_SINGLE) {
                    imageView1.loadImage(GlideLocal(book, getPage1ToInt()), getRequestListener1())
                    swipeRefreshLayout1.onRefresh { imageView1.loadImage(GlideLocal(book, getPage1ToInt()), getRequestListener1()) }
                }
                if (page2 != Constants.KEY_SINGLE) {
                    imageView2.loadImage(GlideLocal(book, getPage2ToInt()), getRequestListener2())
                    swipeRefreshLayout2.onRefresh { imageView2.loadImage(GlideLocal(book, getPage2ToInt()), getRequestListener2()) }
                }
            }
            false -> {
                if (page1 != Constants.KEY_SINGLE) {
                    imageView1.loadImage(page1, getRequestListener1())
                    swipeRefreshLayout1.onRefresh { imageView1.loadImage(page1, getRequestListener1()) }
                }

                if (page2 != Constants.KEY_SINGLE) {
                    imageView2.loadImage(page2, getRequestListener2())
                    swipeRefreshLayout2.onRefresh { imageView2.loadImage(page2, getRequestListener2()) }
                }
            }
        }
    }

    private fun onLoadImage1Success() {
        spinKitView1.fadeGone()
        imageView1.fadeVisible()

        swipeRefreshLayout1.dismissDelayed()
        swipeRefreshLayout1.disable()
        failConstraintLayout2.fadeGone()
    }

    private fun onLoadImage1Fail(message: String?) {
        spinKitView1.fadeGone()
        imageView1.fadeVisible()

        val swipeText = "Swipe down to refresh!"
        val reasonText = message?.let { it } ?: "Failed to load image!"
        failTextView1.text = "$swipeText\n$reasonText"
        failConstraintLayout1.fadeVisible()
        swipeRefreshLayout1.dismissDelayed()
        swipeRefreshLayout1.enable()
    }

    private fun onLoadImage2Success() {
        spinKitView2.fadeGone()
        imageView2.fadeVisible()

        swipeRefreshLayout2.dismissDelayed()
        swipeRefreshLayout2.disable()
        failConstraintLayout2.fadeGone()
    }

    private fun onLoadImage2Fail(message: String?) {
        spinKitView2.fadeGone()
        imageView2.fadeVisible()

        val swipeText = "Swipe down to refresh!"
        val reasonText = message?.let { it } ?: "Failed to load image!"
        failTextView2.text = "$swipeText\n$reasonText"
        failConstraintLayout2.fadeVisible()
        swipeRefreshLayout2.dismissDelayed()
        swipeRefreshLayout2.enable()
    }

    private fun getRequestListener1() = object : RequestListener<Bitmap> {
        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onLoadImage1Success()
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
            onLoadImage1Fail(e?.message)
            return false
        }
    }

    private fun getRequestListener2() = object : RequestListener<Bitmap> {
        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onLoadImage2Success()
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
            onLoadImage2Fail(e?.message)
            return false
        }
    }

    companion object {
        fun newInstance(book: Book, isLocal: Boolean, position: Int): ReaderComicFragmentImpl2_Dual {
            return ReaderComicFragmentImpl2_Dual().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_BOOK, book)
                    putBoolean(ARG_LOCAL, isLocal)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

}