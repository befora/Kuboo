package com.sethchhim.kuboo_client.ui.main.home.custom

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class RecentRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    private val slideInUpAnimator = SlideInUpAnimator().apply {
        removeDuration = 0
        addDuration = 275
        moveDuration = 275
        changeDuration = 275
    }

    init {
        setHasFixedSize(true)
        layoutManager = RecentLinearLayoutManager(context)
        itemAnimator = slideInUpAnimator
    }

}
