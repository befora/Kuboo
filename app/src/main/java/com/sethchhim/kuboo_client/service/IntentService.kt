package com.sethchhim.kuboo_client.service

import android.app.IntentService
import android.content.Intent
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_remote.KubooRemote
import timber.log.Timber
import javax.inject.Inject

class IntentService : IntentService("KUBOO_NOTIFICATION") {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var notificationService: NotificationService

    override fun onHandleIntent(intent: Intent) {
        when (intent.action) {
            CANCEL_ACTION -> handleCancel()
            PAUSE_ACTION -> handlePause()
            RESUME_ACTION -> handleResume()
        }
    }

    private fun handleCancel() {
        Timber.i("CANCEL_ACTION")
        kubooRemote.cancelAll()
    }

    private fun handlePause() {
        Timber.i("PAUSE_ACTION")
        kubooRemote.pauseAll()
    }

    private fun handleResume() {
        Timber.i("RESUME_ACTION")
        kubooRemote.resumeAll()
    }

    companion object {
        val CANCEL_ACTION = "CANCEL_ACTION"
        val PAUSE_ACTION = "PAUSE_ACTION"
        val RESUME_ACTION = "RESUME_ACTION"
    }

}