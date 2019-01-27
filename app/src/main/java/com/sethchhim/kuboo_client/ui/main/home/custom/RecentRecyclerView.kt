package com.sethchhim.kuboo_client.ui.main.home.custom

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet

class RecentRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr) {

    init {
        setHasFixedSize(true)
        layoutManager = RecentLinearLayoutManager(context)
    }

}