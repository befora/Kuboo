package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BrowserContentGridLayoutManager constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) : androidx.recyclerview.widget.GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    override fun onLayoutChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler?, state: androidx.recyclerview.widget.RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

}
