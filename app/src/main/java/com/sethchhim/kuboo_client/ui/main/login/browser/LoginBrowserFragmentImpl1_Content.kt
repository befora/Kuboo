package com.sethchhim.kuboo_client.ui.main.login.browser

import com.sethchhim.kuboo_client.ui.main.login.adapter.LoginAdapter
import com.sethchhim.kuboo_remote.model.Login

open class LoginBrowserFragmentImpl1_Content : LoginBrowserFragmentImpl0_View() {

    protected lateinit var loginAdapter: LoginAdapter

    protected fun populateLogin() {
        val result = viewModel.getLoginList()
        handleResult(result)
    }

    private fun handleResult(result: List<Login>?) {
        when (result == null) {
            true -> onPopulateFail()
            false -> when (result!!.isEmpty()) {
                true -> onPopulateEmpty()
                false -> onPopulateSuccess()
            }
        }
    }

    private fun onPopulateEmpty() = setStateEmpty()

    private fun onPopulateFail() = setStateDisconnected()

    private fun onPopulateSuccess() {
        setStateConnected()
        loginAdapter.notifyDataSetChanged()
    }

}