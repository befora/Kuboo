package com.sethchhim.kuboo_client.ui.main.home

import android.content.res.Configuration
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.ui.main.home.custom.RecentRecyclerView
import com.sethchhim.kuboo_client.util.SystemUtil
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class HomeFragmentImpl0_View : DaggerFragment() {

    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    @BindView(R.id.home_layout_recent_textView2) lateinit var recentMoreTextView: TextView
    @BindView(R.id.home_layout_recent_textView3) lateinit var recentEmptyTextView: TextView
    @BindView(R.id.home_layout_recent_recyclerView) lateinit var recentRecyclerView: RecentRecyclerView

    lateinit var constraintLayout: ConstraintLayout
    lateinit var guideline: Guideline
    lateinit var scrollView: ScrollView
    lateinit var latestMoreTextView: TextView
    lateinit var latestEmptyTextView: TextView
    lateinit var latestRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = when (Settings.HOME_LAYOUT) {
            1 -> R.layout.home_layout_base1
            else -> R.layout.home_layout_base0
        }
        val view = inflater.inflate(layout, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    internal fun scrollToFirstRecent() {
        recentRecyclerView.stopScroll()
        recentRecyclerView.layoutManager?.scrollToPosition(0)
    }

    protected fun scrollToFirstLatest() {
        latestRecyclerView.stopScroll()
        latestRecyclerView.layoutManager?.scrollToPosition(0)
    }

    protected fun scrollToHomeTop() {
        scrollView.smoothScrollTo(0, 0)
    }

    protected fun onClickRecentMoreTextView() = mainActivity.showFragmentBrowserRecent()

    protected fun onClickLatestMoreTextView() = mainActivity.showFragmentBrowserLatest()

    protected fun setRecentStateConnected() {
        recentRecyclerView.fadeVisible()
        recentEmptyTextView.gone()
    }

    protected fun setRecentStateDisconnected() {
        recentRecyclerView.gone()
        recentEmptyTextView.fadeVisible()
    }

    protected fun setRecentStateEmpty() {
        recentRecyclerView.gone()
        recentEmptyTextView.fadeVisible()
    }

    protected fun setRecentStateLoading() {
        recentEmptyTextView.gone()
    }

    protected fun setLatestStateConnected() {
        latestRecyclerView.fadeVisible()
        latestEmptyTextView.gone()
    }

    protected fun setLatestStateDisconnected() {
        latestRecyclerView.gone()
        latestEmptyTextView.fadeVisible()
    }

    protected fun setLatestStateEmpty() {
        latestRecyclerView.gone()
        latestEmptyTextView.fadeVisible()
    }

    protected fun setLatestStateLoading() {
        latestEmptyTextView.gone()
    }

    protected fun Guideline.setGuideLinePercent() {
        val orientation = resources.configuration.orientation
        val offset = Settings.RECENTLY_VIEWED_HEIGHT_OFFSET * 0.01f
        setGuidelinePercent(when (mainActivity.isHiDpi()) {
            true -> when (orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 0.7F + offset
                else -> 0.6F + offset
            }
            false -> when (orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 0.6F + offset
                else -> 0.55F + offset
            }
        })
    }

}