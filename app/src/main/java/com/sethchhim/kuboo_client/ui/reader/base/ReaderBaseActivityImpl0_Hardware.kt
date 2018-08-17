package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.view.KeyEvent
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.base.BaseActivity

@SuppressLint("Registered")
open class ReaderBaseActivityImpl0_Hardware : BaseActivity() {

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (Settings.VOLUME_PAGE_TURN) {
            if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (event.isLongPress)
                    onVolumeDownLongPressed()
                else if (event.action == KeyEvent.ACTION_UP)
                    onVolumeDownPressed()
                return true
            }
            if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                if (event.isLongPress)
                    onVolumeUpLongPressed()
                else if (event.action == KeyEvent.ACTION_UP)
                    onVolumeUpPressed()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    open fun onVolumeDownLongPressed() {
        //override in children activity
    }

    open fun onVolumeDownPressed() {
        //override in children activity
    }

    open fun onVolumeUpLongPressed() {
        //override in children activity
    }

    open fun onVolumeUpPressed() {
        //override in children activity
    }

}