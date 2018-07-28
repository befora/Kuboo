package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import org.jetbrains.anko.sdk25.coroutines.onClick

@SuppressLint("Registered")
open class ReaderBaseActivity : ReaderBaseActivityImpl5_Tracking() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportPostponeEnterTransition()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader_layout_base)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        hideReaderToolbar()
        hideStatusBar()

        title = currentBook.title
        previewImageView.transitionName = transitionUrl

        overlaySeekBar.setLayoutDirection()
        overlayLayout.onClick { hideOverlay() }
        overlayTextView1.onClick { hideOverlay() }
        restoreOverlay()

        viewModel.clearReaderLists()
        populateNeighbors()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reader_info -> showDialogInfo(currentBook)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = when (systemUtil.isHardwareNavigation()) {
        true -> hideOverlayHardwareNavigation()
        false -> hideOverlaySoftwareNavigation()
    }

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

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Settings.PIP_MODE) startPictureInPictureMode()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) hideOverlay()
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