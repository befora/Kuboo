package com.sethchhim.kuboo_client.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sethchhim.kuboo_client.Constants.KEY_APP_THEME
import com.sethchhim.kuboo_client.Constants.KEY_BROWSER_IMMERSIVE
import com.sethchhim.kuboo_client.Constants.KEY_DOWNLOAD_SAVE_PATH
import com.sethchhim.kuboo_client.Constants.KEY_DOWNLOAD_TRACKING_INTERVAL
import com.sethchhim.kuboo_client.Constants.KEY_DOWNLOAD_TRACKING_LIMIT
import com.sethchhim.kuboo_client.Constants.KEY_DUAL_PANE
import com.sethchhim.kuboo_client.Constants.KEY_EPUB_MARGIN_SIZE
import com.sethchhim.kuboo_client.Constants.KEY_EPUB_TEXT_ZOOM
import com.sethchhim.kuboo_client.Constants.KEY_FAVORITE
import com.sethchhim.kuboo_client.Constants.KEY_FIRST_DOWNLOAD
import com.sethchhim.kuboo_client.Constants.KEY_HOME_LAYOUT
import com.sethchhim.kuboo_client.Constants.KEY_LOGIN_LIST
import com.sethchhim.kuboo_client.Constants.KEY_MARK_FINISHED
import com.sethchhim.kuboo_client.Constants.KEY_MAX_PAGE_WIDTH
import com.sethchhim.kuboo_client.Constants.KEY_PREVIEW
import com.sethchhim.kuboo_client.Constants.KEY_REVERSE_LAYOUT
import com.sethchhim.kuboo_client.Constants.KEY_RTL
import com.sethchhim.kuboo_client.Constants.KEY_SCALE_TYPE
import com.sethchhim.kuboo_client.Constants.KEY_SCREEN_ORIENTATION
import com.sethchhim.kuboo_client.Constants.KEY_VOLUME_PAGE_TURN
import com.sethchhim.kuboo_client.Constants.KEY_WIFI_ONLY
import com.sethchhim.kuboo_client.Settings.APP_THEME
import com.sethchhim.kuboo_client.Settings.DEFAULT_APP_THEME
import com.sethchhim.kuboo_client.Settings.DEFAULT_DOWNLOAD_TRACKING_INTERVAL
import com.sethchhim.kuboo_client.Settings.DEFAULT_DOWNLOAD_TRACKING_LIMIT
import com.sethchhim.kuboo_client.Settings.DEFAULT_EPUB_MARGIN_SIZE
import com.sethchhim.kuboo_client.Settings.DEFAULT_EPUB_TEXT_ZOOM
import com.sethchhim.kuboo_client.Settings.DEFAULT_HOME_LAYOUT
import com.sethchhim.kuboo_client.Settings.DEFAULT_MAX_PAGE_WIDTH
import com.sethchhim.kuboo_client.Settings.DEFAULT_SCALE_TYPE
import com.sethchhim.kuboo_client.Settings.DEFAULT_VOLUME_PAGE_TURN
import com.sethchhim.kuboo_client.Settings.DOWNLOAD_SAVE_PATH
import com.sethchhim.kuboo_client.Settings.DOWNLOAD_TRACKING_INTERVAL
import com.sethchhim.kuboo_client.Settings.DOWNLOAD_TRACKING_LIMIT
import com.sethchhim.kuboo_client.Settings.DUAL_PANE
import com.sethchhim.kuboo_client.Settings.EPUB_MARGIN_SIZE
import com.sethchhim.kuboo_client.Settings.EPUB_TEXT_ZOOM
import com.sethchhim.kuboo_client.Settings.FAVORITE
import com.sethchhim.kuboo_client.Settings.HOME_LAYOUT
import com.sethchhim.kuboo_client.Settings.IMMERSIVE_BROWSER
import com.sethchhim.kuboo_client.Settings.MARK_FINISHED
import com.sethchhim.kuboo_client.Settings.MAX_PAGE_WIDTH
import com.sethchhim.kuboo_client.Settings.PREVIEW
import com.sethchhim.kuboo_client.Settings.REVERSE_LAYOUT
import com.sethchhim.kuboo_client.Settings.RTL
import com.sethchhim.kuboo_client.Settings.SCALE_TYPE
import com.sethchhim.kuboo_client.Settings.SCREEN_ORIENTATION
import com.sethchhim.kuboo_client.Settings.VOLUME_PAGE_TURN
import com.sethchhim.kuboo_client.Settings.WIFI_ONLY
import com.sethchhim.kuboo_remote.model.Login
import org.jetbrains.anko.defaultSharedPreferences
import timber.log.Timber

@SuppressLint("ApplySharedPref")
class SharedPrefsHelper(val context: Context) {

    private val isDebugSharedPreferencesHelper = true
    private val sharedPreferences = context.defaultSharedPreferences

    fun restoreSettings() {
        APP_THEME = sharedPreferences.getInt(KEY_APP_THEME, DEFAULT_APP_THEME)
        DOWNLOAD_TRACKING_LIMIT = sharedPreferences.getInt(KEY_DOWNLOAD_TRACKING_LIMIT, DEFAULT_DOWNLOAD_TRACKING_LIMIT)
        DOWNLOAD_TRACKING_INTERVAL = sharedPreferences.getInt(KEY_DOWNLOAD_TRACKING_INTERVAL, DEFAULT_DOWNLOAD_TRACKING_INTERVAL)
        DOWNLOAD_SAVE_PATH = sharedPreferences.getString(KEY_DOWNLOAD_SAVE_PATH, context.getExternalFilesDir(null).path)
        DUAL_PANE = sharedPreferences.getBoolean(KEY_DUAL_PANE, false)
        EPUB_TEXT_ZOOM = sharedPreferences.getInt(KEY_EPUB_TEXT_ZOOM, DEFAULT_EPUB_TEXT_ZOOM)
        EPUB_MARGIN_SIZE = sharedPreferences.getInt(KEY_EPUB_MARGIN_SIZE, DEFAULT_EPUB_MARGIN_SIZE)
        FAVORITE = sharedPreferences.getBoolean(KEY_FAVORITE, true)
        HOME_LAYOUT = sharedPreferences.getInt(KEY_HOME_LAYOUT, DEFAULT_HOME_LAYOUT)
        IMMERSIVE_BROWSER = sharedPreferences.getBoolean(KEY_BROWSER_IMMERSIVE, false)
        MARK_FINISHED = sharedPreferences.getBoolean(KEY_MARK_FINISHED, true)
        MAX_PAGE_WIDTH = sharedPreferences.getInt(KEY_MAX_PAGE_WIDTH, DEFAULT_MAX_PAGE_WIDTH)
        PREVIEW = sharedPreferences.getBoolean(KEY_PREVIEW, true)
        RTL = sharedPreferences.getBoolean(KEY_RTL, false)
        REVERSE_LAYOUT = sharedPreferences.getBoolean(KEY_REVERSE_LAYOUT, false)
        SCALE_TYPE = sharedPreferences.getInt(KEY_SCALE_TYPE, DEFAULT_SCALE_TYPE)
        SCREEN_ORIENTATION = sharedPreferences.getInt(KEY_SCREEN_ORIENTATION, SCREEN_ORIENTATION)
        VOLUME_PAGE_TURN = sharedPreferences.getBoolean(KEY_VOLUME_PAGE_TURN, DEFAULT_VOLUME_PAGE_TURN)
        WIFI_ONLY = sharedPreferences.getBoolean(KEY_WIFI_ONLY, false)

        if (isDebugSharedPreferencesHelper) {
            Timber.i("Loading APP_THEME: $APP_THEME")
            Timber.i("Loading WIFI_ONLY: $WIFI_ONLY")
            Timber.i("Loading MARK_FINISHED: $MARK_FINISHED")
            Timber.i("Loading FAVORITE: $FAVORITE")
            Timber.i("Loading IMMERSIVE_BROWSER: $IMMERSIVE_BROWSER")
            Timber.i("Loading DUAL_PANE: $DUAL_PANE")
            Timber.i("Loading HOME_LAYOUT: $HOME_LAYOUT")
            Timber.i("Loading SCALE_TYPE: $SCALE_TYPE")
            Timber.i("Loading PREVIEW: $PREVIEW")
            Timber.i("Loading RTL: $RTL")
            Timber.i("Loading REVERSE_LAYOUT: $REVERSE_LAYOUT")
            Timber.i("Loading MAX_PAGE_WIDTH: $MAX_PAGE_WIDTH")
            Timber.i("Loading SCREEN_ORIENTATION: $SCREEN_ORIENTATION")
            Timber.i("Loading VOLUME_PAGE_TURN: $VOLUME_PAGE_TURN")
            Timber.i("Loading DOWNLOAD_SAVE_PATH: $DOWNLOAD_SAVE_PATH")
            Timber.i("Loading DOWNLOAD_TRACKING_INTERVAL: $DOWNLOAD_TRACKING_INTERVAL")
            Timber.i("Loading DOWNLOAD_TRACKING_LIMIT: $DOWNLOAD_TRACKING_LIMIT")
        }
    }

    fun getServerList(): MutableList<Login> {
        val json = sharedPreferences.getString(KEY_LOGIN_LIST, null)
        val type = object : TypeToken<MutableList<Login>>() {}.type
        val serverList = Gson().fromJson<MutableList<Login>>(json, type)
        return serverList ?: mutableListOf()
    }

    fun getLoginLastAccessed(): Login? {
        val serverList = getServerList()
        val sortedList = serverList.sortedWith(compareByDescending({ it.timeAccessed }))
        return when (sortedList.isNotEmpty()) {
            true -> sortedList[0]
            false -> null
        }
    }

    fun addLogin(login: Login) {
        val serverList = getServerList()
        val newList = mutableListOf<Login>()

        if (serverList.isEmpty()) {
            newList.add(login)
        } else {
            var isMatchFound = false
            serverList.forEach {
                val isMatch = it.server == login.server
                if (isMatch) {
                    newList.add(login)
                    isMatchFound = true
                } else {
                    newList.add(it)
                }
            }
            if (!isMatchFound) newList.add(login)
        }

        saveServerList(newList)
    }

    fun deleteLogin(login: Login) {
        val serverList = getServerList()
        val removeList = arrayListOf<Login>()
        serverList.forEach {
            val isMatch = it.nickname == login.nickname
                    && it.server == login.server
                    && it.username == login.username
                    && it.password == login.password
            if (isMatch) removeList.add(it)
        }
        serverList.removeAll(removeList)
        saveServerList(serverList)
    }

    fun isFirstDownload(): Boolean {
        val isFirstDownload = sharedPreferences.getBoolean(KEY_FIRST_DOWNLOAD, true)
        if (isDebugSharedPreferencesHelper) Timber.i("Loading IS_FIRST_DOWNLOAD: $isFirstDownload")
        if (isFirstDownload) {
            sharedPreferences.edit().putBoolean(KEY_FIRST_DOWNLOAD, false).apply()
            return true
        }
        return false
    }

    fun saveAppTheme() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving APP_THEME: $APP_THEME")
        sharedPreferences.edit().putInt(KEY_APP_THEME, APP_THEME).commit()
    }

    fun saveFavorite() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving FAVORITE: $FAVORITE")
        sharedPreferences.edit().putBoolean(KEY_FAVORITE, FAVORITE).apply()
    }

    fun saveWifiOnly() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving WIFI_ONLY: $WIFI_ONLY")
        sharedPreferences.edit().putBoolean(KEY_WIFI_ONLY, WIFI_ONLY).apply()
    }

    internal fun saveHomeLayout() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving HOME_LAYOUT: $HOME_LAYOUT")
        sharedPreferences.edit().putInt(KEY_HOME_LAYOUT, HOME_LAYOUT).apply()
    }

    internal fun saveMarkFinished() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving MARK_FINISHED: $MARK_FINISHED")
        sharedPreferences.edit().putBoolean(KEY_MARK_FINISHED, MARK_FINISHED).apply()
    }

    internal fun saveReverseLayout() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving REVERSE_LAYOUT: $REVERSE_LAYOUT")
        sharedPreferences.edit().putBoolean(KEY_REVERSE_LAYOUT, REVERSE_LAYOUT).apply()
    }

    internal fun savePreview() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving PREVIEW: $PREVIEW")
        sharedPreferences.edit().putBoolean(KEY_PREVIEW, PREVIEW).apply()
    }

    internal fun saveBrowserImmersive() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving IMMERSIVE_BROWSER: $IMMERSIVE_BROWSER")
        sharedPreferences.edit().putBoolean(KEY_BROWSER_IMMERSIVE, IMMERSIVE_BROWSER).apply()
    }

    fun saveDualPane() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving DUAL_PANE: $DUAL_PANE")
        sharedPreferences.edit().putBoolean(KEY_DUAL_PANE, DUAL_PANE).commit()
    }

    fun saveScaleType() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving SCALE_TYPE: $SCALE_TYPE")
        sharedPreferences.edit().putInt(KEY_SCALE_TYPE, SCALE_TYPE).apply()
    }

    fun saveRtl() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving RTL: $RTL")
        sharedPreferences.edit().putBoolean(KEY_RTL, RTL).commit()
    }

    fun saveMaxPageWidth() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving MAX_PAGE_WIDTH: $MAX_PAGE_WIDTH")
        sharedPreferences.edit().putInt(KEY_MAX_PAGE_WIDTH, MAX_PAGE_WIDTH).apply()
    }

    fun saveScreenOrientation() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving SCREEN_ORIENTATION: $SCREEN_ORIENTATION")
        sharedPreferences.edit().putInt(KEY_SCREEN_ORIENTATION, SCREEN_ORIENTATION).commit()
    }

    fun saveServerList(serverList: MutableList<Login>) {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving SERVER_LIST: size[${serverList.size}]")
        sharedPreferences.edit().putString(KEY_LOGIN_LIST, serverList.toJson()).apply()
    }

    fun saveDownloadSavePath() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving DOWNLOAD_SAVE_PATH: $DOWNLOAD_SAVE_PATH")
        sharedPreferences.edit().putString(KEY_DOWNLOAD_SAVE_PATH, DOWNLOAD_SAVE_PATH).apply()
    }

    fun saveDownloadTrackingInterval() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving DOWNLOAD_TRACKING_INTERVAL: $DOWNLOAD_TRACKING_INTERVAL")
        sharedPreferences.edit().putInt(KEY_DOWNLOAD_TRACKING_INTERVAL, DOWNLOAD_TRACKING_INTERVAL).apply()
    }

    fun saveDownloadTrackingLimit() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving DOWNLOAD_TRACKING_LIMIT: $DOWNLOAD_TRACKING_LIMIT")
        sharedPreferences.edit().putInt(KEY_DOWNLOAD_TRACKING_LIMIT, DOWNLOAD_TRACKING_LIMIT).apply()
    }

    fun saveEpubTextZoom() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving EPUB_TEXT_ZOOM: $EPUB_TEXT_ZOOM")
        sharedPreferences.edit().putInt(KEY_EPUB_TEXT_ZOOM, EPUB_TEXT_ZOOM).apply()
    }

    fun saveEpubMarginSize() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving EPUB_MARGIN_SIZE: $EPUB_MARGIN_SIZE")
        sharedPreferences.edit().putInt(KEY_EPUB_MARGIN_SIZE, EPUB_MARGIN_SIZE).apply()
    }

    fun saveVolumePageTurn() {
        if (isDebugSharedPreferencesHelper) Timber.i("Saving VOLUME_PAGE_TURN: $VOLUME_PAGE_TURN")
        sharedPreferences.edit().putBoolean(KEY_VOLUME_PAGE_TURN, VOLUME_PAGE_TURN).apply()
    }

    private fun Any.toJson() = Gson().toJson(this)

}
