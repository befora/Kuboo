package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.splash.SplashActivity
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import javax.inject.Inject

open class Task_PreloadBase(val splashActivity: SplashActivity) {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var viewModel: ViewModel

    private val startTime = System.currentTimeMillis()

    internal open fun doPreload() {
        //override in children
    }

    private fun getElapsedTime() = System.currentTimeMillis() - startTime

    protected fun onFinished(className: String) {
        splashActivity.onPreloadTaskFinished(className, -1, getElapsedTime())
    }

}