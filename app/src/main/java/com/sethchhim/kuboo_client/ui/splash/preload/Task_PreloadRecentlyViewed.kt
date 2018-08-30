package com.sethchhim.kuboo_client.ui.splash.preload

import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadRecentlyViewed(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        viewModel.getRecentListFromDao().observe(splashActivity, Observer { result ->
            result?.let { viewModel.setRecentList(it) }
            splashActivity.onPreloadTaskFinished(javaClass.simpleName, result?.size, getElapsedTime())
        })
    }

}