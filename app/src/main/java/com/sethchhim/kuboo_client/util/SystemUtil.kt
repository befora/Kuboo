package com.sethchhim.kuboo_client.util

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.IBinder
import androidx.core.content.ContextCompat
import android.view.WindowManager
import com.sethchhim.kuboo_client.BuildConfig
import com.sethchhim.kuboo_client.Extensions.toReadable
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File

class SystemUtil(private val context: Context) {

    private var isExitRequestConfirmed = false

    val robotoCondensedBold: Typeface by lazy { Typeface.createFromAsset(context.assets, "roboto_condensed_bold.ttf") }
    val robotoCondensedItalic: Typeface by lazy { Typeface.createFromAsset(context.assets, "roboto_condensed_italic.ttf") }
    val robotoCondensedRegular: Typeface by lazy { Typeface.createFromAsset(context.assets, "roboto_condensed_regular.ttf") }

    internal fun isFirstRunOfThisVersion(): Boolean {
        val sharedPreferences = context.defaultSharedPreferences
        val versionCode = getVersionCode()
        if (sharedPreferences.getBoolean(versionCode, true)) {
            sharedPreferences.edit().putBoolean(versionCode, false).apply()
            return true
        }
        return false
    }

    private fun getVersionCode(): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "ERROR"
    }

    internal fun getVersionName(): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "ERROR"
    }

    internal fun isDebugBuild() = BuildConfig.APPLICATION_ID.endsWith("debug", ignoreCase = true)

    internal fun collapseNotifications() = context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))

    internal fun isOrientationLandscape() = getOrientation() == Configuration.ORIENTATION_LANDSCAPE

    internal fun isOrientationPortrait() = getOrientation() == Configuration.ORIENTATION_PORTRAIT

    private fun getOrientation() = context.resources.configuration.orientation

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

    internal fun isNetworkAllowed() = !Settings.WIFI_ONLY || Settings.WIFI_ONLY && !isActiveNetworkMobile()

    private fun isActiveNetworkMobile(): Boolean {
        val connectivityManager = getConnectivityManager()
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        val isMobile = activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
        return isMobile && activeNetworkInfo.isConnected
    }

    private fun getConnectivityManager() = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun getWifiManager() = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    //==============================================================================================

    internal fun getDensity() = context.resources.displayMetrics.density

    internal fun getSystemWidth(): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size.x
    }

    internal fun getSystemHeight(): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size.y
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

    internal fun launchPlayStore() {
        val appPackageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val playStoreUrl = "https://play.google.com/store/apps/details?id="
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl + appPackageName))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            context.startActivity(intent)
        }
    }

    internal fun openLink(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(launchBrowser)
    }

    internal fun copyToClipboard(string: String) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
            primaryClip = ClipData.newPlainText("", string)
        }
        context.toast(context.getString(R.string.main_text_copied_to_clipboard))
    }

    //==============================================================================================

    private fun getCacheDir() = context.cacheDir

    private fun getCacheSize(): String {
        var size: Long = 0
        size += getDirSize(getCacheDir())
        return size.toReadable()
    }

    internal fun getStorageList(): Array<String> {
        try {
            val volumeArray = context.getExternalFilesDirs(null)
            val volumePathArray = Array(volumeArray.size) { i -> i.toString() }

            volumeArray.forEachIndexed { index, file -> volumePathArray[index] = file.path }
            return volumePathArray
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayOf()
    }

    internal fun getStorageListFormatted(): Array<String> {
        try {
            val volumeArray = context.getExternalFilesDirs(null)
            val volumePathArray = Array(volumeArray.size) { i -> i.toString() }

            var externalCount = 0
            volumeArray.forEachIndexed { index, file ->
                val freeSpace = file.freeSpace.toReadable()
                volumePathArray[index] = when (file.path.contains("/storage/emulated/0")) {
                    true -> "Internal Storage \n($freeSpace free)"
                    false -> {
                        externalCount += 1
                        "External Storage $externalCount \n($freeSpace free)"
                    }
                }
            }
            return volumePathArray
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayOf()
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
            Timber.i("[CACHE] Finished deleteDownload cache: oldSize[$oldCacheSize] newSize[$newCacheSize] [${elapsedTime}ms]")
        } catch (ignored: Exception) {
            Timber.e("[CACHE] Failed to deleteDownload cache!")
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

    internal fun requestExitApplication() {
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

    internal fun getPrimaryTextColor() = when (Settings.APP_THEME) {
        0 -> ContextCompat.getColor(context, R.color.primaryTextLight)
        else -> ContextCompat.getColor(context, R.color.primaryTextDark)
    }

}