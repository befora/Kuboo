package com.sethchhim.kuboo_client.service

import android.app.IntentService
import android.content.Intent
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Constants.ARG_REQUEST_DOWNLOAD_FRAGMENT
import com.sethchhim.kuboo_client.ui.splash.SplashActivity
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import timber.log.Timber
import javax.inject.Inject

class IntentService : IntentService("KUBOO_NOTIFICATION") {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var notificationService: NotificationService
    @Inject lateinit var systemUtil: SystemUtil

    override fun onHandleIntent(intent: Intent) {
        when (intent.action) {
            CANCEL_ACTION -> handleCancel()
            PAUSE_ACTION -> handlePause()
            RESUME_ACTION -> handleResume()
            RESET_COMPLETED_COUNT_ACTION -> handleResetCompletedCount()
            DISMISS_COMPLETED_ACTION -> handleDismissCompleted()
            DOWNLOAD_FRAGMENT_ACTION -> handleDownloadFragment(cancelCompleted = false)
            DOWNLOAD_FRAGMENT_ACTION_AND_CANCEL_COMPLETED -> handleDownloadFragment(cancelCompleted = true)
        }
    }

    private fun handleDownloadFragment(cancelCompleted: Boolean) {
        val intent = Intent(applicationContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(ARG_REQUEST_DOWNLOAD_FRAGMENT, true)
        applicationContext.startActivity(intent)
        if (cancelCompleted) {
            notificationService.cancelCompleted()
            systemUtil.collapseNotifications()
        }
    }

    private fun handleCancel() {
        Timber.i(CANCEL_ACTION)
        kubooRemote.cancelAll()
    }

    private fun handlePause() {
        Timber.i(PAUSE_ACTION)
        kubooRemote.pauseAll()
    }

    private fun handleResume() {
        Timber.i(RESUME_ACTION)
        kubooRemote.resumeAll()
    }

    private fun handleResetCompletedCount() {
        Timber.i(RESET_COMPLETED_COUNT_ACTION)
        notificationService.clearCompletedCount()
    }

    private fun handleDismissCompleted() {
        Timber.i(DISMISS_COMPLETED_ACTION)
        notificationService.cancelCompleted()
    }

    companion object {
        const val CANCEL_ACTION = "CANCEL_ACTION"
        const val PAUSE_ACTION = "PAUSE_ACTION"
        const val RESUME_ACTION = "RESUME_ACTION"
        const val RESET_COMPLETED_COUNT_ACTION = "RESET_COMPLETED_COUNT_ACTION"
        const val DISMISS_COMPLETED_ACTION = "DISMISS_COMPLETED_ACTION"
        const val DOWNLOAD_FRAGMENT_ACTION = "DOWNLOAD_FRAGMENT_ACTION"
        const val DOWNLOAD_FRAGMENT_ACTION_AND_CANCEL_COMPLETED = "DOWNLOAD_FRAGMENT_ACTION_AND_CANCEL_COMPLETED"
    }

}