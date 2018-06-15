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

class ReaderComicFragmentImpl1_Single : ReaderComicFragment() {

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
        swipeRefreshLayout.setColorSchemeResources(R.color.lightColorAccent)

        when (isLocal) {
            true -> {
                imageView.loadImage(GlideLocal(book, getPage1ToInt()), getGlideCallback())
                swipeRefreshLayout.onRefresh { imageView.loadImage(GlideLocal(book, getPage1ToInt()), getGlideCallback()) }
            }
            false -> {
                imageView.loadImage(getPage1(), getGlideCallback())
                swipeRefreshLayout.onRefresh { imageView.loadImage(getPage1(), getGlideCallback()) }
            }
        }
    }

    private fun onLoadImageSuccess() {
        spinKitView.fadeGone()
        imageView.fadeVisible()

        failConstraintLayout.fadeGone()
        swipeRefreshLayout.dismissDelayed()
        swipeRefreshLayout.disable()
    }

    private fun onLoadImageFail(message: String?) {
        spinKitView.fadeGone()
        imageView.fadeGone()

        val swipeText = "Swipe down to refresh!"
        val reasonText = message?.let { it } ?: "Failed to load image!"
        failTextView.text = "$swipeText\n$reasonText"
        failConstraintLayout.fadeVisible()
        swipeRefreshLayout.dismissDelayed()
        swipeRefreshLayout.enable()
    }

    private fun getGlideCallback() = object : RequestListener<Bitmap> {
        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onLoadImageSuccess()
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
            onLoadImageFail(e?.localizedMessage)
            return false
        }
    }

    companion object {
        fun newInstance(book: Book, isLocal: Boolean, position: Int): ReaderComicFragmentImpl1_Single {
            return ReaderComicFragmentImpl1_Single().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_BOOK, book)
                    putBoolean(ARG_LOCAL, isLocal)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

}