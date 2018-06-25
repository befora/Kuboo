package com.sethchhim.kuboo_client.ui.main

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import com.sethchhim.kuboo_client.Extensions.millisecondsToSeconds
import com.sethchhim.kuboo_client.Extensions.minutesToMilliseconds
import com.sethchhim.kuboo_client.Settings
import timber.log.Timber
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.absoluteValue

@SuppressLint("Registered")
open class MainActivityImpl3_Service : MainActivityImpl2_Selection() {

    private var downloadTrackingTimer: Timer? = null
    private var nextUpdateTimer: Timer? = null

    val timeUntilLiveData = MutableLiveData<Long>()
    private var currentExecutionTime = -1L
    private var nextExecutionTime = -1L


    internal fun setDownloadTrackingService() {
        startSeriesDownloadService()
        setDownloadTrackingTimer()
    }

    private fun setDownloadTrackingTimer() {
        if (downloadTrackingTimer != null) downloadTrackingTimer!!.cancel()
        setNextUpdateTimer()
        downloadTrackingTimer = timer(startAt = Date(), period = Settings.DOWNLOAD_TRACKING_INTERVAL.minutesToMilliseconds(), action = {
            startSeriesDownloadService()
            setNextUpdateTimer()
            Timber.i("Download tracking service started.")
        })
    }

    private fun setNextUpdateTimer() {
        if (nextUpdateTimer != null) nextUpdateTimer!!.cancel()
        currentExecutionTime = System.currentTimeMillis()
        nextExecutionTime = currentExecutionTime + Settings.DOWNLOAD_TRACKING_INTERVAL.minutesToMilliseconds()
        nextUpdateTimer = timer(startAt = Date(), period = 1000, action = {
            timeUntilLiveData.postValue((System.currentTimeMillis() - nextExecutionTime).absoluteValue)
            Timber.d("Time until next download tracking update: ${timeUntilLiveData.value?.millisecondsToSeconds()} seconds")
        })
    }

}
