package com.sethchhim.kuboo_client.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.ui.base.custom.LoadingStage
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.GlideUtil
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.main_layout_base.*
import org.jetbrains.anko.toast
import javax.inject.Inject

@SuppressLint("Registered")
open class BaseActivityImpl0_View : DaggerAppCompatActivity() {

    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var glideUtil: GlideUtil
    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    internal lateinit var currentBook: Book
    protected lateinit var previousBook: Book
    protected lateinit var nextBook: Book
    protected lateinit var source: Source
    protected lateinit var transitionUrl: String

    protected lateinit var bookmarkDialog: AlertDialog
    protected lateinit var loadingDialog: AlertDialog

    internal var isLocal = false
    protected var isLoadingCancelled = false
    protected var isLoadingRequired = true

    internal fun forceOrientationSetting() {
        when (Settings.SCREEN_ORIENTATION) {
            0 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            1 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            2 -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        }
    }

    internal fun forceOrientationPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    internal fun forceOrientationLandscape() {
        val isLandscape = systemUtil.isOrientationLandscape()
        if (isLandscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    internal fun isHiDpi() = systemUtil.getSystemWidth() >= Constants.HI_DPI_VALUE || systemUtil.getSystemHeight() >= Constants.HI_DPI_VALUE && systemUtil.getDensity() <= 3

    internal fun isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    internal fun isStatusBarVisible(activity: Activity): Boolean {
        return activity.window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    internal fun setFullScreen() {
        //must be called before setContentView()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = uiOptions
    }

    internal fun setKeepScreenOn() = window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    internal fun showStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    internal fun hideStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    internal fun showToastError() = toast(R.string.login_something_went_wrong)

    internal fun showToastFailedToLoadImageAssets() = toast(R.string.main_failed_to_load_assets)

    internal fun showToastFileDoesNotExist() = toast(R.string.dialog_file_does_not_exist)

    internal fun showToastFileTypeNotSupported() = toast(R.string.main_file_type_not_supported)

    protected fun getAppTheme() = when (Settings.APP_THEME) {
        0 -> R.style.AppThemeLight
        1 -> R.style.AppThemeDark
        2 -> R.style.AppThemeOled
        else -> R.style.AppThemeLight
    }

    internal fun getAppThemeTextColor() = when (Settings.APP_THEME) {
        0 -> ContextCompat.getColor(this, R.color.primaryTextLight)
        else -> ContextCompat.getColor(this, R.color.primaryTextDark)
    }

    protected fun setTransitionDuration() = window.sharedElementEnterTransition?.apply {
        duration = Settings.SHARED_ELEMENT_TRANSITION_DURATION
        setDuration(Settings.SHARED_ELEMENT_TRANSITION_DURATION).interpolator = DecelerateInterpolator()
    }

    private fun hideBottomNavigation() = main_layout_base_bottomNavigationView.gone()

    private fun hideToolbar() = main_layout_base_toolBar.gone()

    private fun showBottomNavigation() = main_layout_base_bottomNavigationView.visible()

    private fun showToolbar() = main_layout_base_toolBar.visible()

    internal fun showChangeLog() = dialogUtil.getDialogChangeLog(this).apply {
        show()
        window.attributes = WindowManager.LayoutParams().apply {
            val systemWidth = systemUtil.getSystemWidth()
            val systemHeight = systemUtil.getSystemHeight()
            val newSize = (Math.min(systemWidth, systemHeight) * 0.9f).toInt()
            width = newSize
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    protected fun showLoadingDialog(loadingStage: LoadingStage) = loadingDialog.apply {
        isLoadingCancelled = false
        isLoadingRequired = true
        val isReader = loadingStage == LoadingStage.SINGLE || loadingStage == LoadingStage.DUAL

        setMessage(when (loadingStage) {
            LoadingStage.PING -> getString(R.string.dialog_connecting_to_server)
            LoadingStage.BOOKMARK -> getString(R.string.dialog_loading_bookmark)
            LoadingStage.ASSET -> getString(R.string.dialog_loading_assets)
            LoadingStage.SINGLE -> getString(R.string.dialog_loading_single_pane_mode)
            LoadingStage.DUAL -> getString(R.string.dialog_loading_dual_pane_mode)
        })

        if (!isShowing) {
            setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_cancel)) { _, _ -> onLoadingDialogCancel(isReader) }

            if (isReader) {
                setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.dialog_single_pane)) { _, _ -> onLoadingDialogSinglePane() }
            }

            if (isLoadingRequired) show()
        }
    }

    private fun Dialog.onLoadingDialogCancel(isReader: Boolean) {
        isLoadingCancelled = true
        if (isShowing) dismiss()
        if (isReader) finish()
    }

    private fun Dialog.onLoadingDialogSinglePane() {
        if (isShowing) dismiss()
        Settings.DUAL_PANE = false
        sharedPrefsHelper.saveDualPane()
        recreate()
    }

    protected fun hideLoadingDialog() = loadingDialog.apply {
        if (isShowing) dismiss()
    }

}
