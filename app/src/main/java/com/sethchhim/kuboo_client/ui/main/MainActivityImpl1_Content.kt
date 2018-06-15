package com.sethchhim.kuboo_client.ui.main

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Response
import timber.log.Timber

@SuppressLint("Registered")
open class MainActivityImpl1_Content : MainActivityImpl0_View() {

    protected var isRequestDownloadFragment = false
    protected var loginLiveData = MutableLiveData<Login>()

    protected fun onActiveLoginChanged(it: Login?) {
        viewModel.clearPathList()
        if (isRequestDownloadFragment) {
            Timber.i("Download fragment is requested from intent.")
            showFragmentDownloads()
        } else {
            when (it == null || it.isEmpty()) {
                true -> onActiveLoginInvalid()
                false -> onActiveLoginValid()
            }
        }
    }

    protected fun pingShowHomeFragment() = viewModel.pingActiveServer().observe(this, Observer { result ->
        when (result?.isSuccessful ?: false) {
            true -> {
                setStateConnected()
                showFragmentHome()
            }
            false -> if (isBottomNavHomeSelected()) setStateDisconnected(result)
        }
    })

    protected fun pingShowBrowserRemoteFragment() = viewModel.pingActiveServer().observe(this, Observer { result ->
        when (result?.isSuccessful ?: false) {
            true -> {
                setStateConnected()
                showFragmentBrowserRemote()
            }
            false -> if (isBottomNavBrowseSelected()) setStateDisconnected(result)
        }
    })

    protected fun setActiveLogin() {
        val lastAccessedLogin = viewModel.getLoginLastAccessed()
        Timber.d("Found last accessed login ${lastAccessedLogin?.server}")
        viewModel.setActiveLogin(lastAccessedLogin)
    }

    private fun onActiveLoginInvalid() {
        when (viewModel.isLoginListEmpty()) {
            true -> setStateDisconnected(Response(code = 0, message = getString(R.string.main_no_server_detected), isSuccessful = false))
            false -> showFragmentLoginBrowser()
        }
    }

    private fun onActiveLoginValid() {
        Timber.i("onActiveLoginValid")
        setStateLoading()
        bottomNav.selectedItemId = R.id.navigation_home
//        pingShowHomeFragment()
    }

}
