package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import com.github.ybq.android.spinkit.SpinKitView
import com.sethchhim.epublibdroid_kotlin.EpubReaderView
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.reader.base.ReaderBaseActivity
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
open class ReaderEpubActivityImpl0_View : ReaderBaseActivity() {

    lateinit var epubReaderView: EpubReaderView
    lateinit var spinKitView: SpinKitView

    protected fun initContentUi() {
        val contentView = layoutInflater.inflate(R.layout.reader_layout_epub_content, null, false)
        contentFrameLayout.addView(contentView)
        epubReaderView = findViewById(R.id.reader_epub_base_epubReaderView)
        spinKitView = findViewById(R.id.reader_epub_base_spinKitView)
    }

    protected fun setStateSuccess() {
        spinKitView.invisible()
        epubReaderView.fadeVisible()
    }

    protected fun setStateLoading() {
        spinKitView.visible()
        epubReaderView.invisible()
    }

    protected fun onLoadPreviewSuccess() {
        supportStartPostponedEnterTransition()
        showEnterTransition()
    }

    protected fun onLoadPreviewFail() {
        supportStartPostponedEnterTransition()
        previewImageView.gone()
    }

    private fun showEnterTransition() {
        launch(UI) {
            try {
                delay(1200, TimeUnit.MILLISECONDS)
                previewImageView.slideOut()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

    protected fun onFileNotFound() {
        toast("File not found!")
        finish()
    }

}