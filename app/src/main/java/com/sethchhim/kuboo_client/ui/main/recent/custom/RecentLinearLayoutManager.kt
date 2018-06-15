package com.sethchhim.kuboo_client.ui.main.recent.custom

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class RecentLinearLayoutManager(context: Context) : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

    private val EXTRA_LAYOUT_SPACE = 2000

    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return EXTRA_LAYOUT_SPACE
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

}