package com.sethchhim.kuboo_client.ui.reader.book

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import timber.log.Timber

@SuppressLint("Registered")
open class ReaderEpubActivityImpl1_Preview : ReaderEpubActivityImpl0_View() {

    protected fun loadPreviewImage() = when (isLocal) {
        true -> loadLocalPreviewImage()
        false -> loadRemotePreviewImage()
    }

//    private fun loadLocalPreviewImage() = viewModel.getLocalImageInputStream(0).observe(this, Observer { result ->
//        result?.let {
//            previewImageView.loadPreviewImage(GlideLocal(currentBook, 0))
//            overlayImageView.loadOverlayImage(GlideLocal(currentBook, 0))
//        }
//    })

    private fun loadLocalPreviewImage() = epubReaderView.getCoverImage()?.let {
        previewImageView.loadPreviewImage(it)
        overlayImageView.loadOverlayImage(it)
    } ?: onLoadPreviewFail()

    private fun loadRemotePreviewImage() {
        previewImageView.loadPreviewImage(transitionUrl)
        overlayImageView.loadOverlayImage(transitionUrl)
    }

    private fun ImageView.loadOverlayImage(any: Any) = Glide.with(this@ReaderEpubActivityImpl1_Preview)
            .load(any)
            .apply(RequestOptions()
                    .priority(Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .dontAnimate()
                    .dontTransform())
            .into(this)

    private fun ImageView.loadPreviewImage(any: Any) = Glide.with(this@ReaderEpubActivityImpl1_Preview)
            .load(any)
            .apply(RequestOptions()
                    .priority(Priority.IMMEDIATE)
                    .disallowHardwareConfig()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .dontAnimate()
                    .dontTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Timber.e("message[${e?.message}] url[$transitionUrl]")
                    onLoadPreviewFail()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    this@loadPreviewImage.viewTreeObserver.addOnPreDrawListener(
                            object : ViewTreeObserver.OnPreDrawListener {
                                override fun onPreDraw(): Boolean {
                                    this@loadPreviewImage.viewTreeObserver.removeOnPreDrawListener(this)
                                    onLoadPreviewSuccess()
                                    return true
                                }
                            }
                    )
                    return false
                }
            })
            .into(this)

}