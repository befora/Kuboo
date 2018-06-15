package com.sethchhim.kuboo_client.util

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File
import java.text.DecimalFormat

class SystemUtil(private val context: Context) {

    private var isExitRequestConfirmed = false

    val robotoCondensedBold: Typeface by lazy { Typeface.createFromAsset(context.assets, "roboto_condensed_bold.ttf") }
    val robotoCondensedItalic: Typeface by lazy { Typeface.createFromAsset(context.assets, "roboto_condensed_italic.ttf") }
    val robotoCondensedRegular: Typeface by lazy { Typeface.createFromAsset(context.assets, "roboto_condensed_regular.ttf") }

    fun isFirstRun(): Boolean {
        val sharedPreferences = context.defaultSharedPreferences
        val versionCode = getVersionCode()
        if (sharedPreferences.getBoolean(versionCode, true)) {
            sharedPreferences.edit().putBoolean(versionCode, false).apply()
            return true
        }
        return false
    }

    fun getVersionCode(): String {
        val pInfo: PackageInfo
        try {
            pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "ERROR"
    }

    fun hideKeyboard() {
        context.inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun showKeyboard() {
        //TODO this doesn't work?
        context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    internal fun getOrientation() = context.resources.configuration.orientation

    internal fun isOrientationLandscape(): Boolean =
            getOrientation() == Configuration.ORIENTATION_LANDSCAPE

    internal fun isOrientationPortrait(): Boolean =
            getOrientation() == Configuration.ORIENTATION_PORTRAIT

    @SuppressLint("PrivateApi")
    private fun isSoftwareNavigation() = try {
        val serviceManager = Class.forName("android.os.ServiceManager")
        val serviceBinder = serviceManager.getMethod("getService", String::class.java).invoke(serviceManager, "window") as IBinder
        val stub = Class.forName("android.view.IWindowManager\$Stub")
        val windowManagerService = stub.getMethod("asInterface", IBinder::class.java).invoke(stub, serviceBinder)
        val hasNavigationBar = windowManagerService.javaClass.getMethod("hasNavigationBar")
        hasNavigationBar.invoke(windowManagerService) as Boolean
    } catch (e: Exception) {
        Timber.e("message[${e.message}]")
        false
    }

    internal fun isHardwareNavigation() = !isSoftwareNavigation()

    //==============================================================================================

    fun getSystemWidth(): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun getSystemHeight(): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }

    fun convertPixelsToDp(px: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val dp = px / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

    fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    //==============================================================================================

    internal fun launchExternalBrowser(filePath: String) {
        val position = filePath.lastIndexOf("/") + 1
        val formattedPath = filePath.substring(0, position)
        val selectedUri = Uri.parse(formattedPath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(selectedUri, "resource/folder")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (intent.resolveActivityInfo(context.packageManager, 0) != null) {
            context.startActivity(intent)
        } else {
            context.toast("Unable to find external browser!")
        }
    }

    fun launchPlayStore() {
        val appPackageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val URL_GOOGLE_PLAY_STORE = "http://play.google.com/store/apps/details?id="
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL_GOOGLE_PLAY_STORE + appPackageName))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            context.startActivity(intent)
        }
    }

    fun openLink(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(launchBrowser)
    }

    //==============================================================================================

    internal fun getCacheDir() = context.cacheDir

    private fun getCacheSize(): String {
        var size: Long = 0
        size += getDirSize(getCacheDir())
        return readableFileSize(size)
    }

    fun readableFileSize(long: Long): String {
        if (long <= 0) return "0 Bytes"
        val units = arrayOf("Bytes", "kB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(long.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.##").format(long / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    private fun getDirSize(directory: File): Long {
        var size: Long = 0
        try {
            for (file in directory.listFiles()!!) {
                if (file != null && file.isDirectory) {
                    size += getDirSize(file)
                } else if (file != null && file.isFile) {
                    size += file.length()
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return size
    }

    internal fun deleteCache() {
        try {
            val startTime = System.currentTimeMillis()
            val oldCacheSize = getCacheSize()
            deleteDir(getCacheDir())
            val newCacheSize = getCacheSize()
            val stopTime = System.currentTimeMillis()
            val elapsedTime = stopTime - startTime
            Timber.i("[CACHE] Finished delete cache: oldSize[$oldCacheSize] newSize[$newCacheSize] [${elapsedTime}ms]")
        } catch (ignored: Exception) {
            Timber.e("[CACHE] Failed to delete cache!")
        }
    }

    private fun deleteDir(directory: File): Boolean {
        if (directory.isDirectory) {
            val children = directory.list()
            children.forEach {
                Timber.i("[CACHE] Deleting file: $it")
                val success = deleteDir(File(directory, it))
                if (!success) {
                    return false
                }
            }
        }
        return directory.delete()
    }

    fun requestExitApplication() {
        if (isExitRequestConfirmed) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_HOME)
            context.startActivity(intent)
            return
        }
        isExitRequestConfirmed = true
        context.toast(R.string.main_press_back_again_to_exit)
        Handler().postDelayed({ isExitRequestConfirmed = false }, 2000)
    }

    fun getPrimaryTextColor() = when (Settings.APP_THEME) {
        0 -> ContextCompat.getColor(context, R.color.primaryTextLight)
        else -> ContextCompat.getColor(context, R.color.primaryTextDark)
    }

}