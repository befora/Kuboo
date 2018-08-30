package com.sethchhim.kuboo_client.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sethchhim.kuboo_client.BaseApplication
import javax.inject.Inject

class OnClearFromRecentService : Service() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var notificationService: NotificationService

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int = Service.START_NOT_STICKY

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        notificationService.cancelProgress()
        stopSelf()
    }

}