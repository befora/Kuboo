package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import com.sethchhim.kuboo_client.R

class BrowserContentSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {

    init {
        setColorSchemeResources(R.color.lightColorAccent)
    }

}