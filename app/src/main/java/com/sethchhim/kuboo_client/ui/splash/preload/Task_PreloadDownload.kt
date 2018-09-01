package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadDownload(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        viewModel.getDownloadList(favoriteCompressed = true).observeForever { result ->
            result?.let {  }
            splashActivity.onPreloadTaskFinished(javaClass.simpleName, result?.size, getElapsedTime())
        }
    }

}
