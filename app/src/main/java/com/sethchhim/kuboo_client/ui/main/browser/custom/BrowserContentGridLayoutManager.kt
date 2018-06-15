package com.sethchhim.kuboo_client.ui.main.browser.custom

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

class BrowserContentGridLayoutManager constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

}
