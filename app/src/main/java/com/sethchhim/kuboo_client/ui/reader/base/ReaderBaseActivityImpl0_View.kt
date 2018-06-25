package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v7.widget.Toolbar
import android.util.Rational
import android.view.View
import android.widget.*
import butterknife.BindView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.model.Progress
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderPreviewImageView
import com.sethchhim.kuboo_remote.model.Book
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import timber.log.Timber
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
open class ReaderBaseActivityImpl0_View : BaseActivity() {

    @BindView(R.id.reader_layout_base_constraintLayout) lateinit var constraintLayout: ConstraintLayout
    @BindView(R.id.reader_layout_base_content_progressBar) lateinit var progressBar: ProgressBar
    @BindView(R.id.reader_layout_base_content_frameLayout) lateinit var contentFrameLayout: FrameLayout
    @BindView(R.id.reader_layout_base_overlay_constraintLayout) lateinit var overlayLayout: ConstraintLayout
    @BindView(R.id.reader_layout_base_overlay_guideLine1) lateinit var guidelineHorizontal: Guideline
    @BindView(R.id.reader_layout_comic_dual_content_guideLine) lateinit var guidelineVertical: Guideline
    @BindView(R.id.reader_layout_base_overlay_imageView) lateinit var overlayImageView: ImageView
    @BindView(R.id.reader_layout_base_overlay_linearLayout) lateinit var overlayTextLayout: LinearLayout
    @BindView(R.id.reader_layout_base_overlay_seekBar) lateinit var overlaySeekBar: SeekBar
    @BindView(R.id.reader_layout_base_overlay_textView1) lateinit var overlayTextView1: TextView
    @BindView(R.id.reader_layout_base_overlay_textView3) lateinit var overlayPageNumberTextView: TextView
    @BindView(R.id.reader_layout_base_overlay_textView5) lateinit var overlayTotalPagesTextView: TextView
    @BindView(R.id.reader_layout_base_preview_readerPreviewImageView) lateinit var previewImageView: ReaderPreviewImageView
    @BindView(R.id.reader_layout_base_toolBar) lateinit var toolbar: Toolbar

    protected fun forceOrientation() = when (Settings.DUAL_PANE) {
        true -> forceOrientationLandscape()
        false -> forceOrientationSetting()
    }

    protected fun handleProgress(progress: Progress) {
        if (loadingDialog.isShowing) {
            val textView = loadingDialog.findViewById<TextView>(android.R.id.message)
            textView?.let {
                val message = "${getString(R.string.dialog_loading_dual_pane_mode)} ${progress.position} of ${progress.total}"
                it.text = message
            }
        }
    }

    protected fun hideReaderToolbar() = supportActionBar?.hide()

    protected fun showExitTransition() {
        launch(UI) {
            try {
                showStatusBar()
                delay(300)
                previewImageView.slideIn()
                delay(800, TimeUnit.MILLISECONDS)
                super.onBackPressed()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    protected fun showOfflineDialog(book: Book) {
        dialogUtil.getDialogOffline(this, book).apply {
            //            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_browse)) { _, _ ->
//                systemUtil.launchExternalBrowser(book.filePath)
//            }
            show()
        }
    }

    protected fun showReaderToolBar() {
        supportActionBar?.show()
    }

    protected fun View.setLayoutDirection() {
        layoutDirection = when (Settings.RTL) {
            true -> SeekBar.LAYOUT_DIRECTION_RTL
            false -> SeekBar.LAYOUT_DIRECTION_LTR
        }
    }

    protected fun startPictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pipParams = PictureInPictureParams.Builder().apply {
                val aspectRatio = Rational(contentFrameLayout.width, contentFrameLayout.height)
                setAspectRatio(aspectRatio)
            }.build()
            enterPictureInPictureMode(pipParams)
        } else {
            toast(getString(R.string.reader_pip_requires_android_oreo_or_above))
        }
    }

}
