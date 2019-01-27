package com.sethchhim.kuboo_client.ui.main.login.browser

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sethchhim.kuboo_client.ui.main.login.adapter.LoginAdapter

class LoginBrowserFragment : LoginBrowserFragmentImpl1_Content() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        fab.setOnClickListener { onFabClicked() }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginAdapter = LoginAdapter(mainActivity, viewModel)
        loginRecyclerView.adapter = loginAdapter
        loginRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        populateLogin()
    }
}