package com.sethchhim.kuboo_client.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.base.custom.OnPreloadCallback
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

class GlideUtil {

    internal fun preload(lifecycleOwner: LifecycleOwner, book: Book): MutableLiveData<Boolean> {
        val preloadLiveData = MutableLiveData<Boolean>()
        var finishedCount = 0

        val preloadCallback = object : OnPreloadCallback {
            override fun onSuccess() {
                finishedCount += 1
                if (finishedCount == 2) preloadLiveData.value = true
            }

            override fun onFailure() {
                preloadLiveData.value = false
                preloadLiveData.removeAllObservers(lifecycleOwner)
            }
        }

        preload(context = lifecycleOwner as Context,
                stringUrl = book.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT),
                onPreloadCallback = preloadCallback)

        preload(context = lifecycleOwner as Context,
                stringUrl = book.server + book.getPse(Settings.MAX_PAGE_WIDTH, book.currentPage),
                onPreloadCallback = preloadCallback)

        return preloadLiveData
    }

    internal fun preload(context: Context, stringUrl: String, onPreloadCallback: OnPreloadCallback? = null) = Glide.with(context)
            .load(stringUrl)
            .apply(RequestOptions()
                    .priority(Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Timber.e("message[${e.toString()}] url[$stringUrl]")
                    onPreloadCallback?.onFailure()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    onPreloadCallback?.onSuccess()
                    return false
                }
            })
            .preload()

}