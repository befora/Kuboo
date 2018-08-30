package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadFavorite(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        viewModel.getFavoriteListFromDao().observeForever { result ->
            result?.let { viewModel.setFavoriteList(it) }
            splashActivity.onPreloadTaskFinished(javaClass.simpleName, result?.size, getElapsedTime())
        }
    }

}