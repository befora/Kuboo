package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class BrowserPathRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr) {

    init {
        itemAnimator = SlideInUpAnimator().apply {
            removeDuration = 0
            addDuration = 200
            moveDuration = 200
            changeDuration = 200
        }
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
    }

}