package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.BuildConfig
import com.sethchhim.kuboo_client.ui.splash.SplashActivity
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber

class Task_PreloadDebugServers(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            if (viewModel.isLoginListEmpty()) {
                viewModel.addLogin(Login(nickname = "Books Server",
                        server = "https://192.168.1.100:2202/opds-books/",
                        username = "abcd",
                        password = "abcd"))

                viewModel.addLogin(Login(nickname = "Comics Server",
                        server = "https://192.168.1.100:2202/opds-comics/",
                        username = "abcd",
                        password = "abcd"))
            }
        }
        splashActivity.onPreloadTaskFinished(javaClass.simpleName, -1, getElapsedTime())

    }

}
