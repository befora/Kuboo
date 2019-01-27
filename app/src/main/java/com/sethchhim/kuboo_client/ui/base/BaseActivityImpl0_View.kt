package com.sethchhim.kuboo_client.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.core.content.ContextCompat
import android.view.View
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
import com.sethchhim.kuboo_client.service.TrackingService
import com.sethchhim.kuboo_client.util.*
import com.sethchhim.kuboo_remote.model.Book
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.main_layout_base.*
import javax.inject.Inject

@SuppressLint("Registered")
open class BaseActivityImpl0_View : DaggerAppCompatActivity() {

    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var logUtil: LogUtil
    @Inject lateinit var glideUtil: GlideUtil
    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var trackingService: TrackingService
    @Inject lateinit var viewModel: ViewModel

    internal lateinit var currentBook: Book
    protected lateinit var previousBook: Book
    protected lateinit var nextBook: Book
    protected lateinit var source: Source
    protected lateinit var transitionUrl: String

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

    internal fun setKeepScreenOn() {
        if (Settings.KEEP_SCREEN_ON) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

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

    private fun showBottomNavigation() = main_layout_base_bottomNavigationView.visible()

    private fun hideBottomNavigation() = main_layout_base_bottomNavigationView.gone()

    private fun showToolbar() = main_layout_base_toolBar.visible()

    private fun hideToolbar() = main_layout_base_toolBar.gone()

}
