package com.sethchhim.kuboo_client.ui.splash

import android.content.Intent
import android.os.Bundle
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.ui.splash.preload.*
import timber.log.Timber

class SplashActivity : BaseActivity() {

    private var preloadFinishCount = 0
    private var preloadTaskCount = 6
    private var preloadTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Task_PreloadDebugServers(this).doPreload()
        Task_PreloadDownload(this).doPreload()
        Task_PreloadFavorite(this).doPreload()
        Task_PreloadLog(this).doPreload()
        Task_PreloadRecentlyViewed(this).doPreload()
        Task_PreloadSharedPrefs(this).doPreload()
    }

    internal fun onPreloadTaskFinished(simpleName: String, size: Int?, elapsedTime: Long) {
        Timber.i("$simpleName preload finished: size[$size] [$elapsedTime ms]")
        preloadFinishCount += 1
        preloadTime += elapsedTime
        val isPreloadComplete = preloadTaskCount == preloadFinishCount
        if (isPreloadComplete) onPreloadDataComplete()
    }

    private fun onPreloadDataComplete() {
        Timber.i("Preload of application is finished: [$preloadTime ms]")

        val mainActivityIntent = Intent(this, MainActivity::class.java).apply { handleIntentRequest(intent) }
        startActivity(mainActivityIntent)
        finish()
    }

    private fun Intent.handleIntentRequest(intent: Intent) {
        //forward request to main activity
        when {
            intent.getBooleanExtra(Constants.ARG_REQUEST_DOWNLOAD_FRAGMENT, false) -> putExtra(Constants.ARG_REQUEST_DOWNLOAD_FRAGMENT, true)
            intent.getBooleanExtra(Constants.ARG_REQUEST_REMOTE_BROWSER_FRAGMENT, false) -> putExtra(Constants.ARG_REQUEST_REMOTE_BROWSER_FRAGMENT, true)
        }
    }

}