package com.sethchhim.kuboo_client.ui.main.settings

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import javax.inject.Inject


open class SettingsFragmentImp0_View : PreferenceFragmentCompat() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    protected lateinit var mainActivity: MainActivity

    protected lateinit var aboutVersionPreference: Preference
    protected lateinit var browserFavoritePreference: SwitchPreferenceCompat
    protected lateinit var browserMarkFinishedPreference: SwitchPreferenceCompat
    protected lateinit var browserPreviewPreference: SwitchPreferenceCompat
    protected lateinit var browserReverseLayoutPreference: SwitchPreferenceCompat
    protected lateinit var downloadFinishedNotification: SwitchPreferenceCompat
    protected lateinit var downloadSavePath: Preference
    protected lateinit var downloadTrackingLimit: Preference
    protected lateinit var downloadTrackingInterval: Preference
    protected lateinit var downloadTrackingHideFinished: SwitchPreferenceCompat
    protected lateinit var homeLayoutPreference: Preference
    protected lateinit var serverLoginPreference: Preference
    protected lateinit var systemOrientationPreference: Preference
    protected lateinit var systemThemePreference: Preference
    protected lateinit var systemVolumePageTurnPreference: SwitchPreferenceCompat
    protected lateinit var systemWifiOnlyPreference: SwitchPreferenceCompat
    protected lateinit var systemKeepScreenOn: SwitchPreferenceCompat
    protected lateinit var systemStartTab: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        aboutVersionPreference = findPreference("settings_about_version")
        browserFavoritePreference = findPreference("settings_browser_favorite") as SwitchPreferenceCompat
        browserMarkFinishedPreference = findPreference("settings_browser_mark_finished") as SwitchPreferenceCompat
        browserPreviewPreference = findPreference("settings_browser_preview") as SwitchPreferenceCompat
        browserReverseLayoutPreference = findPreference("settings_browser_reverse_layout") as SwitchPreferenceCompat
        downloadFinishedNotification = findPreference("settings_download_show_finished_notification") as SwitchPreferenceCompat
        downloadSavePath = findPreference("settings_download_save_path")
        downloadTrackingLimit = findPreference("settings_download_tracking_limit")
        downloadTrackingInterval = findPreference("settings_download_tracking_interval")
        downloadTrackingHideFinished = findPreference("settings_download_tracking_hide_finished") as SwitchPreferenceCompat
        homeLayoutPreference = findPreference("settings_home_layout")
        serverLoginPreference = findPreference("settings_server_login")
        systemOrientationPreference = findPreference("settings_system_orientation")
        systemThemePreference = findPreference("settings_system_theme")
        systemKeepScreenOn = findPreference("settings_keep_screen_on") as SwitchPreferenceCompat
        systemVolumePageTurnPreference = findPreference("settings_volume_page_turn") as SwitchPreferenceCompat
        systemWifiOnlyPreference = findPreference("settings_wifi_only") as SwitchPreferenceCompat
        systemStartTab = findPreference("settings_start_tab")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    internal fun scrollToTop() = scrollToPreference(serverLoginPreference)

}