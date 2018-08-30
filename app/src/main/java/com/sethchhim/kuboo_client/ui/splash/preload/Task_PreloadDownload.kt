package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadDownload(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        viewModel.getDownloadListFavoriteCompressedFromDao().observeForever { result ->
            result?.let { viewModel.setDownloadList(it) }
            splashActivity.onPreloadTaskFinished(javaClass.simpleName, result?.size, getElapsedTime())
        }
    }

}
