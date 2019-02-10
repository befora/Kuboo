package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.BuildConfig
import com.sethchhim.kuboo_client.ui.splash.SplashActivity
import timber.log.Timber

class Task_InitTimber(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        onFinished(javaClass.simpleName)
    }

}
