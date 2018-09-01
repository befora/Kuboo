package com.sethchhim.kuboo_client.ui.main.browser

import android.support.v7.widget.LinearLayoutManager
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter

open class BrowserBaseFragmentImpl3_Path: BrowserBaseFragmentImpl2_Content() {

    protected var isPathEnabled = true

    protected fun setPath() {
        when (isPathEnabled) {
            true -> {
                pathAdapter = BrowserPathAdapter(this, viewModel)
                pathRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                pathRecyclerView.adapter = pathAdapter
            }
            false -> pathRecyclerView.gone()
        }
    }
}