package com.sethchhim.kuboo_client.ui.main.login.browser

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.ui.main.login.adapter.LoginAdapter
import org.jetbrains.anko.sdk27.coroutines.onClick

class LoginBrowserFragment : LoginBrowserFragmentImpl1_Content() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        fab.onClick { onFabClicked() }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginAdapter = LoginAdapter(mainActivity, viewModel)
        loginRecyclerView.adapter = loginAdapter
        loginRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        populateLogin()
    }
}