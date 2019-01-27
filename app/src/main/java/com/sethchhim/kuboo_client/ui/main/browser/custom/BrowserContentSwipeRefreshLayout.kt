package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.AttributeSet
import com.sethchhim.kuboo_client.R

class BrowserContentSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : androidx.swiperefreshlayout.widget.SwipeRefreshLayout(context, attrs) {

    init {
        setColorSchemeResources(R.color.lightColorAccent)
    }

}