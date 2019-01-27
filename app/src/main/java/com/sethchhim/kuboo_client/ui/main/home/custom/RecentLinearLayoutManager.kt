package com.sethchhim.kuboo_client.ui.main.home.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecentLinearLayoutManager(context: Context) : androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false) {

    private val EXTRA_LAYOUT_SPACE = 2000

    override fun getExtraLayoutSpace(state: androidx.recyclerview.widget.RecyclerView.State?): Int {
        return EXTRA_LAYOUT_SPACE
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

}