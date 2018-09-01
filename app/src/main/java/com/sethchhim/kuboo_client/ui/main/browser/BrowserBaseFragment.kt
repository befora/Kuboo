package com.sethchhim.kuboo_client.ui.main.browser

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler

open class BrowserBaseFragment : BrowserBaseFragmentImpl1_Content() {

    protected var isPathEnabled = true
    protected var isPaginationEnabled = true

    private var isFirstInstance = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.browser_layout_base, container, false)
        ButterKnife.bind(this, view)
        onButterKnifeBind(view)
        return view
    }

    protected open fun onButterKnifeBind(view: View) {
        contentRecyclerView.setContent()
        pathRecyclerView.setPath()
        setPagination(isPaginationEnabled)
        paginationHandler = PaginationHandler(this, view)
    }

    override fun onPause() {
        super.onPause()
        mainActivity.disableSelectionMenuState()
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstInstance) {
//            contentAdapter.resetAllColorState()
            mainActivity.enableSelectionMenuState()
        }
        isFirstInstance = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideMenuItemBrowserLayout()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentSpanCount(newConfig.orientation, contentRecyclerView.contentType)
    }

    private fun RecyclerView.setPath() = when (isPathEnabled) {
        true -> {
            pathAdapter = BrowserPathAdapter(this@BrowserBaseFragment, viewModel)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = pathAdapter
        }
        false ->  gone()
    }

    private fun RecyclerView.setContent() {
//        contentAdapter = BrowserContentAdapter(this@BrowserBaseFragment, viewModel)
//        contentRecyclerView.adapter = contentAdapter
    }

}
