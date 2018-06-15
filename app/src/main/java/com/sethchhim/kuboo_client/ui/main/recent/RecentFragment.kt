package com.sethchhim.kuboo_client.ui.main.recent

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.ui.main.recent.adapter.RecentAdapter
import com.sethchhim.kuboo_client.ui.main.recent.custom.RecentLinearLayoutManager
import org.jetbrains.anko.sdk25.coroutines.onClick

class RecentFragment : RecentFragmentImpl1_Content() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        recentAdapter = RecentAdapter(this, viewModel)
        recentRecyclerView.setHasFixedSize(true)
        recentRecyclerView.layoutManager = RecentLinearLayoutManager(activity!!)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recentMoreTextView.onClick { onClickRecentMoreTextView() }
        recentRecyclerView.adapter = recentAdapter
    }

    override fun onResume() {
        super.onResume()
        populateRecent()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val state = recentRecyclerView.layoutManager.onSaveInstanceState()
        recentAdapter = RecentAdapter(this, viewModel)
        recentRecyclerView.adapter = recentAdapter
        recentRecyclerView.layoutManager.onRestoreInstanceState(state)
    }

}