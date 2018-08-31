package com.sethchhim.kuboo_client.ui.main.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.setConstraintBottomToTop
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadsAdapter

class DownloadsFragment : DownloadsFragmentImpl1_Content() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kubooRemote.addFetchListener(fetchListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        contentSwipeRefreshLayout.setConstraintBottomToTop(downloadsTabLayout.id)
        paginationLayout.gone()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomNavigation()
        downloadsAdapter = DownloadsAdapter(this, viewModel)
        contentRecyclerView.adapter = downloadsAdapter
        contentSwipeRefreshLayout.setOnRefreshListener {
            populateDownloads()
            setNumberProgressBar()
        }
    }

    override fun onResume() {
        super.onResume()
        populateDownloads()
        setNumberProgressBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        kubooRemote.removeFetchListener(fetchListener)
    }
}