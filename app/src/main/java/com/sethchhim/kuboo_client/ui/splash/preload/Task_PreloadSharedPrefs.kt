package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadSharedPrefs(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        sharedPrefsHelper.restoreSettings()
        onFinished(javaClass.simpleName)
    }

}
