package com.sethchhim.kuboo_client.ui.main.browser

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler

open class BrowserBaseFragment : BrowserBaseFragmentImpl3_Path() {

    protected var isCustomImplementation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.browser_layout_base, container, false)
        ButterKnife.bind(this, view)
        onButterKnifeBind(view)
        return view
    }

    override fun onPause() {
        super.onPause()
        disableSelection(isCustomImplementation)
    }

    override fun onResume() {
        super.onResume()
        enableSelection(isCustomImplementation)
        handleNeededAdapterUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideMenuItemSearch()
        mainActivity.hideMenuItemHttps()
        mainActivity.hideMenuItemBrowserLayout()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentSpanCount(newConfig.orientation, contentRecyclerView.contentType)
    }

    protected open fun onButterKnifeBind(view: View) {
        setPath()
        setPagination()
        paginationHandler = PaginationHandler(this, view)
    }

}