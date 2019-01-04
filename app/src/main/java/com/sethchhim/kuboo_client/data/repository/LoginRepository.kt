package com.sethchhim.kuboo_client.data.repository

import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.isMatch
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class LoginRepository(private val sharedPrefsHelper: SharedPrefsHelper) {

    private val activeLoginLiveData = MutableLiveData<Login>()

    private val loginList = sharedPrefsHelper.getServerList()

    internal fun getActiveLoginLiveData() = activeLoginLiveData

    internal fun getActiveLogin() = activeLoginLiveData.value ?: Login()

    internal fun getActiveServer() = getActiveLogin().server

    internal fun getLoginList() = loginList

    internal fun getLoginLastAccessed() = sharedPrefsHelper.getLoginLastAccessed()

    internal fun getLoginAt(position: Int): Login? {
        return try {
            loginList[position]
        } catch (e: IndexOutOfBoundsException) {
            Timber.e("Login item not found at position[$position]!")
            null
        }
    }

    internal fun setActiveLogin(login: Login?) {
        login?.updateTimeAccessed()
        activeLoginLiveData.value = login
        sharedPrefsHelper.saveServerList(loginList)
    }

    internal fun addLogin(login: Login) {
        login.setTimeAccessed()
        loginList.add(login)
        loginList.sortByDescending { it.timeAccessed }
        sharedPrefsHelper.saveServerList(loginList)
    }

    internal fun deleteLogin(login: Login) {
        loginList.removeMatch(login)
        sharedPrefsHelper.saveServerList(loginList)
    }

    internal fun isLoginListEmpty() = loginList.isEmpty()

    internal fun isActiveLogin(login: Login) = getActiveLogin().isMatch(login)

    internal fun isActiveLoginEmpty() = getActiveLogin().isEmpty()

    internal fun isActiveServerKuboo() = getActiveServer().contains("/opds/")

    internal fun isActiveServerUbooquity() = getActiveServer().contains("/opds-comics/")
            || getActiveServer().contains("/opds-books/")

    private fun MutableList<Login>.removeMatch(login: Login) {
        val removeList = arrayListOf<Login>()
        forEach { if (it.isMatch(login)) removeList.add(it) }
        removeAll(removeList)
    }

    private fun Login.updateTimeAccessed() {
        var containsMatch = false
        loginList.forEach {
            if (it.isMatch(this)) {
                it.setTimeAccessed()
                containsMatch = true
            }
        }
        if (!containsMatch) {
            setTimeAccessed()
            addLogin(this)
        }
    }

}



