package com.sethchhim.kuboo_client.ui.main.home.custom

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class RecentRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        setHasFixedSize(true)
        layoutManager = RecentLinearLayoutManager(context)
    }

}
