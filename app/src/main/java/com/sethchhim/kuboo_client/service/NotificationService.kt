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
import com.sethchhim.kuboo_client.ui.main.MainActivityImpl0_View
import com.sethchhim.kuboo_remote.KubooRemote
import com.tonyodev.fetch2.Download

class NotificationService(val context: Context, val kubooRemote: KubooRemote) {

    private val NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL"
    private val NOTIFICATION_NAME = "KUBOO_NOTIFICATION"
    private val NOTIFICATION_ID = 8675309
    private val NOTIFICATION_TAG = context.getString(R.string.app_label)

    private val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL).apply {
        priority = NotificationCompat.PRIORITY_MAX
        setWhen(0)
        setAutoCancel(true)
        setContentIntent(getSelectionIntent())
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
        val intent = Intent(context, MainActivityImpl0_View::class.java)
        intent.putExtra(ARG_REQUEST_DOWNLOAD_FRAGMENT, true)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun getCancelFetchIntent(): PendingIntent {
        val cancelIntent = Intent(context, IntentService::class.java)
        cancelIntent.action = IntentService.CANCEL_ACTION
        return PendingIntent.getService(context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPauseFetchIntent(): PendingIntent {
        val pauseIntent = Intent(context, IntentService::class.java)
        pauseIntent.action = IntentService.PAUSE_ACTION
        return PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getResumeFetchIntent(): PendingIntent {
        val resumeIntent = Intent(context, IntentService::class.java)
        resumeIntent.action = IntentService.RESUME_ACTION
        return PendingIntent.getService(context, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getStartNotification(download: Download, downloadsCount: Int): Notification {
        notificationBuilder.apply {
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
        return notificationBuilder.build()
    }

    private fun getPauseNotification(): Notification {
        val pauseBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_play_arrow_white_24dp)
        notificationBuilder.apply {
            setContentTitle(context.getString(R.string.app_label))
            setContentText(context.getString(R.string.notification_download_service_paused))
            setSmallIcon(R.drawable.ic_pause_white_24dp)
            setLargeIcon(pauseBitmap)
            setProgress(100, 0, false)

            mActions.clear()
            addAction(R.drawable.ic_delete_white_24dp, context.getString(R.string.notification_cancel), getCancelFetchIntent())
            addAction(R.drawable.ic_play_arrow_white_24dp, context.getString(R.string.notification_resume), getResumeFetchIntent())
        }
        return notificationBuilder.build()
    }

    internal fun startNotification(download: Download, downloadsCount: Int) {
        val notification = getStartNotification(download, downloadsCount)
        notification.flags = NotificationCompat.FLAG_NO_CLEAR and NotificationCompat.FLAG_LOCAL_ONLY
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
    }

    internal fun pauseNotification() {
        val notification = getPauseNotification()
        notification.flags = NotificationCompat.FLAG_NO_CLEAR and NotificationCompat.FLAG_LOCAL_ONLY
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
    }

    internal fun stopNotification() = notificationManager.cancelAll()

}