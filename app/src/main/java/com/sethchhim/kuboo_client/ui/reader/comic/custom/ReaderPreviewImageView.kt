package com.sethchhim.kuboo_client.ui.reader.comic.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings

class ReaderPreviewImageView @JvmOverloads constructor(private val ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(ctx, attrs, defStyleAttr) {

    private val slideInLeftAnimation: Animation by lazy { AnimationUtils.loadAnimation(ctx, R.anim.slide_in_left) }
    private val slideInRightAnimation: Animation by lazy { AnimationUtils.loadAnimation(ctx, R.anim.slide_in_right) }
    private val slideOutLeftAnimation: Animation by lazy { AnimationUtils.loadAnimation(ctx, R.anim.slide_out_left) }
    private val slideOutRightAnimation: Animation by lazy { AnimationUtils.loadAnimation(ctx, R.anim.slide_out_right) }

    internal var isAnimatingTransition = true

    internal fun slideIn() = post {
        visibility = View.VISIBLE
        when (Settings.RTL) {
            true -> startAnimation(slideInLeftAnimation)
            false -> startAnimation(slideInRightAnimation)
        }
    }

    internal fun slideOut() = post {
        when (Settings.RTL) {
            true -> startAnimation(slideOutRightAnimation)
            false -> startAnimation(slideOutLeftAnimation)
        }
        visibility = View.GONE
    }
}