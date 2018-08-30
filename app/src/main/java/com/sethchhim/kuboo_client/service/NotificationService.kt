package com.sethchhim.kuboo_client.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.sethchhim.kuboo_client.Constants.ARG_REQUEST_DOWNLOAD_FRAGMENT
import com.sethchhim.kuboo_client.Extensions.guessFilename
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_remote.KubooRemote
import com.tonyodev.fetch2.Download

class NotificationService(val context: Context, val kubooRemote: KubooRemote) {

    private val NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL"
    private val NOTIFICATION_NAME = "KUBOO_NOTIFICATION"
    private val NOTIFICATION_PROGRESS_TAG = "NOTIFICATION_PROGRESS_TAG"
    private val NOTIFICATION_COMPLETED_TAG = "NOTIFICATION_COMPLETED_TAG"
    private val NOTIFICATION_PROGRESS_ID = 8675309
    private val NOTIFICATION_COMPLETED_ID = 8675310

    private var completedCount = 0

    internal fun startProgress(download: Download, downloadsCount: Int) {
        val notification = getStartNotification(download, downloadsCount)
        notificationManager.notify(NOTIFICATION_PROGRESS_TAG, NOTIFICATION_PROGRESS_ID, notification)
    }

    internal fun pauseProgress() {
        val notification = getPauseNotification()
        notificationManager.notify(NOTIFICATION_PROGRESS_TAG, NOTIFICATION_PROGRESS_ID, notification)
    }

    internal fun cancelProgress() = notificationManager.cancel(NOTIFICATION_PROGRESS_TAG, NOTIFICATION_PROGRESS_ID)

    internal fun cancelCompleted() {
        clearCompletedCount()
        notificationManager.cancel(NOTIFICATION_COMPLETED_TAG, NOTIFICATION_COMPLETED_ID)
    }

    internal fun startCompleted(download: Download) {
        val notification = getCompletedNotification(download)
        notificationManager.notify(NOTIFICATION_COMPLETED_TAG, NOTIFICATION_COMPLETED_ID, notification)
    }

    internal fun clearCompletedCount() {
        completedCount = 0
    }

    internal fun increaseCompletedCount() {
        completedCount += 1
    }

    private val progressNotificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL).apply {
        priority = NotificationCompat.PRIORITY_MAX
        setWhen(0)
        setContentIntent(getSelectionIntent())
        setOngoing(true)
    }

    private val completedNotificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL).apply {
        priority = NotificationCompat.PRIORITY_MAX
        setWhen(0)
        setContentIntent(getSelectionIntent())
        setOngoing(false)
    }
    private val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(getNotificationChannel())
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel() = NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW).apply {
        enableLights(true)
        lightColor = Color.GREEN
        enableVibration(false)
    }

    private fun getSelectionIntent(): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(ARG_REQUEST_DOWNLOAD_FRAGMENT, true)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun getCancelFetchIntent(): PendingIntent {
        val intent = Intent(context, IntentService::class.java)
        intent.action = IntentService.CANCEL_ACTION
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPauseFetchIntent(): PendingIntent {
        val intent = Intent(context, IntentService::class.java)
        intent.action = IntentService.PAUSE_ACTION
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getResumeFetchIntent(): PendingIntent {
        val intent = Intent(context, IntentService::class.java)
        intent.action = IntentService.RESUME_ACTION
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getResetCompletedCountIntent(): PendingIntent {
        val intent = Intent(context, IntentService::class.java)
        intent.action = IntentService.RESET_COMPLETED_COUNT_ACTION
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getDismissCompletedIntent(): PendingIntent {
        val intent = Intent(context, IntentService::class.java)
        intent.action = IntentService.DISMISS_COMPLETED_ACTION
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getStartNotification(download: Download, downloadsCount: Int): Notification {
        progressNotificationBuilder.apply {
            setContentTitle(download.request.url.guessFilename())
            setSmallIcon(android.R.drawable.stat_sys_download)
            setProgress(100, download.progress, false)

            if (downloadsCount > 0) {
                val downloadsMessage = context.resources.getString(R.string.notification_download_in_progress_remaining, (downloadsCount + 1).toString())
                setContentText(downloadsMessage)
            } else {
                setContentText(context.getString(R.string.notification_download_in_progress))
            }

            mActions.clear()
            addAction(R.drawable.ic_delete_white_24dp, context.getString(R.string.notification_cancel), getCancelFetchIntent())
            addAction(R.drawable.ic_pause_white_24dp, context.getString(R.string.notification_pause), getPauseFetchIntent())
        }
        return progressNotificationBuilder.build()
    }

    private fun getCompletedNotification(download: Download): Notification {
        completedNotificationBuilder.apply {
            val itemString = when (completedCount > 1) {
                true -> R.string.notification_items
                false -> R.string.notification_item
            }
            setContentTitle("${context.getString(R.string.notification_finished_download_of)} $completedCount ${context.getString(itemString)}!")
            setContentText(null)
            setSmallIcon(android.R.drawable.stat_sys_download_done)
            setProgress(0, 0, false)
            setDeleteIntent(getResetCompletedCountIntent())
            mActions.clear()
            addAction(R.drawable.ic_delete_white_24dp, context.getString(R.string.notification_browse), getSelectionIntent())
            addAction(R.drawable.ic_delete_white_24dp, context.getString(R.string.notification_dismiss), getDismissCompletedIntent())
        }
        return completedNotificationBuilder.build()
    }

    private fun getPauseNotification(): Notification {
        val pauseBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_play_arrow_white_24dp)
        progressNotificationBuilder.apply {
            setContentTitle(context.getString(R.string.app_label))
            setContentText(context.getString(R.string.notification_download_service_paused))
            setSmallIcon(R.drawable.ic_pause_white_24dp)
            setLargeIcon(pauseBitmap)
            setProgress(100, 0, false)

            mActions.clear()
            addAction(R.drawable.ic_delete_white_24dp, context.getString(R.string.notification_cancel), getCancelFetchIntent())
            addAction(R.drawable.ic_play_arrow_white_24dp, context.getString(R.string.notification_resume), getResumeFetchIntent())
        }
        return progressNotificationBuilder.build()
    }

}