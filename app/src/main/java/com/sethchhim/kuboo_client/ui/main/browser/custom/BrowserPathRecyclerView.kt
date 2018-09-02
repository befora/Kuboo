package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class BrowserPathRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        itemAnimator = SlideInUpAnimator().apply {
            removeDuration = 0
            addDuration = 200
            moveDuration = 200
            changeDuration = 200
        }
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

}