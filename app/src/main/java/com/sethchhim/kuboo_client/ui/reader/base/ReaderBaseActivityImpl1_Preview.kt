package com.sethchhim.kuboo_client.ui.reader.base

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
open class ReaderBaseActivityImpl1_Preview : ReaderBaseActivityImpl0_View() {

    protected var isPreviewEnabled = true

    protected fun loadPreviewImage() = when (isLocal) {
        true -> loadLocalPreviewImage()
        false -> loadRemotePreviewImage()
    }

    open fun loadLocalPreviewImage() {
        //override in children
    }

    private fun loadRemotePreviewImage() {
        previewImageView.loadPreviewImage(transitionUrl)
    }

    protected fun ImageView.loadPreviewImage(any: Any) =
            Glide.with(this@ReaderBaseActivityImpl1_Preview)
                    .load(any)
                    .apply(RequestOptions()
                            .priority(Priority.IMMEDIATE)
                            .disallowHardwareConfig()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .dontTransform()
                            .dontAnimate())
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
                                            processResult()
                                            return true
                                        }
                                    }
                            )
                            return false
                        }

                        private fun processResult() {
                            when (isPreviewEnabled) {
                                true -> onLoadPreviewSuccessFirstRun()
                                false -> onLoadPreviewSuccessResume()
                            }
                        }
                    })
                    .into(this)

    open fun onLoadPreviewSuccessFirstRun() {
        //override in children
    }

    open fun onLoadPreviewSuccessResume() {
        //override in children
    }

    open fun onLoadPreviewFail() {
        //override in children
    }

}