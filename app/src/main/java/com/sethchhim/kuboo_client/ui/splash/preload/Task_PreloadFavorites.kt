package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadFavorites(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        viewModel.getFavoriteListFromDao().observeForever {
            it?.let {
                viewModel.setFavoriteList(it)
            }
            splashActivity.onPreloadTaskFinished(javaClass.simpleName, -1, getElapsedTime())
        }
    }

}
