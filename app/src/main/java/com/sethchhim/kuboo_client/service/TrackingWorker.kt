package com.sethchhim.kuboo_client.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_remote.model.Login
import javax.inject.Inject


class TrackingWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var trackingService: TrackingService

    override fun doWork(): ListenableWorker.Result {
        val login = Login().apply {
            nickname = inputData.getString(Constants.KEY_LOGIN_NICKNAME) ?: ""
            server = inputData.getString(Constants.KEY_LOGIN_SERVER) ?: ""
            username = inputData.getString(Constants.KEY_LOGIN_USERNAME) ?: ""
            password = inputData.getString(Constants.KEY_LOGIN_PASSWORD) ?: ""
        }
        trackingService.startTrackingServiceSingle(login)
        return Result.success()
    }
}