package com.sethchhim.kuboo_client.ui.main.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.home.adapter.LatestAdapter
import com.sethchhim.kuboo_client.ui.main.home.adapter.RecentAdapter
import com.sethchhim.kuboo_client.ui.main.home.custom.LatestLinearLayoutManager
import org.jetbrains.anko.sdk27.coroutines.onClick

class HomeFragment : HomeFragmentImpl1_Content() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        recentAdapter = RecentAdapter(this, viewModel)
        if (isHomeRequireLatest()) {
            view?.let {
                guideline = view.findViewById(R.id.home_layout_base1_guideline)
                guideline.setGuideLinePercent()
                constraintLayout = view.findViewById(R.id.home_layout_base1_constraintLayout)
                scrollView = view.findViewById(R.id.home_layout_base1_scrollView)
                latestRecyclerView = view.findViewById(R.id.home_layout_latest_recyclerView)
                latestMoreTextView = view.findViewById(R.id.home_layout_latest_textView2)
                latestEmptyTextView = view.findViewById(R.id.home_layout_latest_textView3)
                latestRecyclerView.layoutManager = LatestLinearLayoutManager(activity!!)
                latestRecyclerView.setHasFixedSize(true)
                latestAdapter = LatestAdapter(this, viewModel)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentMoreTextView.onClick { onClickRecentMoreTextView() }
        recentRecyclerView.adapter = recentAdapter
        if (isHomeRequireLatest()) {
            latestMoreTextView.onClick { onClickLatestMoreTextView() }
            latestRecyclerView.adapter = latestAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        populateRecent()
        if (isHomeRequireLatest()) populateLatest()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (isHomeRequireLatest()) guideline.setGuideLinePercent()
    }

    internal fun resetHome() {
        scrollToFirstRecent()
        populateRecent()
        if (isHomeRequireLatest()) {
            scrollToHomeTop()
            scrollToFirstLatest()
            populateLatest()
        }
    }

}
