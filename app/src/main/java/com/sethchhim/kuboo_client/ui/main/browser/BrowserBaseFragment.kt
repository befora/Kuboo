package com.sethchhim.kuboo_client.ui.main.browser

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler

open class BrowserBaseFragment : BrowserBaseFragmentImpl2_Selection() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentAdapter = BrowserContentAdapter(this, viewModel)
        contentSwipeRefreshLayout.setColorSchemeResources(R.color.lightColorAccent)
        paginationHandler = PaginationHandler(this, view)
        pathAdapter = BrowserPathAdapter(this, view, viewModel)
        pathRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contentRecyclerView.adapter = contentAdapter
        pathRecyclerView.adapter = pathAdapter
    }

    override fun onResume() {
        super.onResume()
        resetAllColorState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentSpanCount(newConfig.orientation, contentRecyclerView.contentType)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_action_download -> startSelectionDownload()
            R.id.main_action_mark_finished_add -> startSelectionAddFinished()
            R.id.main_action_mark_finish_delete -> startSelectionDeleteFinished()
        }
        return super.onOptionsItemSelected(item)
    }

}