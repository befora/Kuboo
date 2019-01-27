package com.sethchhim.kuboo_client.ui.main.login.browser

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class LoginBrowserFragmentImpl0_View : DaggerFragment() {

    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var viewModel: ViewModel

    @BindView(R.id.state_empty_constraintLayout) lateinit var emptyLayout: ConstraintLayout
    @BindView(R.id.state_error_constraintLayout) lateinit var errorLayout: ConstraintLayout
    @BindView(R.id.login_layout_browser_floatingActionButton) lateinit var fab: FloatingActionButton
    @BindView(R.id.login_layout_browser_recyclerView) lateinit var loginRecyclerView: androidx.recyclerview.widget.RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_layout_browser, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    protected fun onFabClicked() = mainActivity.showFragmentLoginEdit(login = null)

    protected fun setStateConnected() {
        loginRecyclerView.fadeVisible()
        emptyLayout.invisible()
        errorLayout.invisible()
    }

    protected fun setStateDisconnected() {
        loginRecyclerView.invisible()
        emptyLayout.invisible()
        errorLayout.fadeVisible()
    }

    protected fun setStateEmpty() {
        loginRecyclerView.invisible()
        emptyLayout.fadeVisible()
        errorLayout.invisible()
    }

}