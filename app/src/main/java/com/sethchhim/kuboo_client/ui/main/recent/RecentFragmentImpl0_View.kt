package com.sethchhim.kuboo_client.ui.main.recent

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.util.SystemUtil
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class RecentFragmentImpl0_View : DaggerFragment() {

    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    @BindView(R.id.home_layout_recent_textView2) lateinit var recentMoreTextView: TextView
    @BindView(R.id.home_layout_recent_textView3) lateinit var recentEmptyTextView: TextView
    @BindView(R.id.home_layout_recent_recyclerView) lateinit var recentRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_layout_recent, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    internal fun scrollToFirstRecent() = recentRecyclerView.layoutManager.scrollToPosition(0)

    protected fun onClickRecentMoreTextView() = mainActivity.showFragmentBrowserRecent()

    protected fun setStateConnected() {
        recentRecyclerView.fadeVisible()
        recentEmptyTextView.gone()
    }

    protected fun setStateDisconnected() {
        recentRecyclerView.gone()
        recentEmptyTextView.fadeVisible()
    }

    protected fun setStateEmpty() {
        recentRecyclerView.gone()
        recentEmptyTextView.fadeVisible()
    }

    protected fun setStateLoading() {
        recentRecyclerView.gone()
        recentEmptyTextView.gone()
    }

}




