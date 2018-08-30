package com.sethchhim.kuboo_client.ui.splash.preload

import com.sethchhim.kuboo_client.ui.splash.SplashActivity

class Task_PreloadLog(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        viewModel.getLogList().observeForever { result ->
            result?.let { if (it.isEmpty()) logUtil.addMockLogData() }
            splashActivity.onPreloadTaskFinished(javaClass.simpleName, result?.size, getElapsedTime())
        }
    }

}