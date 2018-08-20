package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.main.MainActivity
import org.jetbrains.anko.sdk25.coroutines.onClick


@SuppressLint("Registered")
open class ReaderBaseActivity : ReaderBaseActivityImpl6_Tracking() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportPostponeEnterTransition()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader_layout_base)
        setKeepScreenOn()
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        hideReaderToolbar()
        hideStatusBar()

        pipWidth = systemUtil.getSystemWidth()
        pipHeight = systemUtil.getSystemHeight()

        title = currentBook.title
        previewImageView.transitionName = transitionUrl

        overlayLayout.onClick { hideOverlay() }
        overlayTextView1.onClick { hideOverlay() }
        restoreOverlay()

        populateNeighbors()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        hideOverlay(isFadeEnabled = false)
        populateNeighbors()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearReaderLists()
    }

    override fun finish() {
        if (isBackStackLost) {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }
        super.finish()
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

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Settings.PIP_MODE) startPictureInPictureMode()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            isInPipMode = true
            hideOverlay(isFadeEnabled = false)
        } else {
            isInPipMode = false
            isBackStackLost = true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        setOverlayGuideLines()
    }

}