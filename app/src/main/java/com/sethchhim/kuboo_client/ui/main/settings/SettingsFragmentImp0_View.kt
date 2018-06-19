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
import javax.inject.Inject

open class SettingsFragmentImp0_View : PreferenceFragmentCompat() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var viewModel: ViewModel

    protected lateinit var mainActivity: MainActivity

    protected lateinit var browserFavoritePreference: SwitchPreferenceCompat
    protected lateinit var browserMarkFinishedPreference: SwitchPreferenceCompat
    protected lateinit var browserPreviewPreference: SwitchPreferenceCompat
    protected lateinit var browserReverseLayoutPreference: SwitchPreferenceCompat
    protected lateinit var comicScaleTypePreference: Preference
    protected lateinit var comicDualPanePreference: SwitchPreferenceCompat
    protected lateinit var comicRtlPreference: SwitchPreferenceCompat
    protected lateinit var downloadSavePath: Preference
    //    protected lateinit var downloadSeriesLimit: Preference
    protected lateinit var epubTextZoomPreference: Preference
    protected lateinit var epubMarginPreference: Preference
    protected lateinit var serverLoginPreference: Preference
    protected lateinit var systemOrientationPreference: Preference
    protected lateinit var systemThemePreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        browserFavoritePreference = findPreference("settings_browser_favorite") as SwitchPreferenceCompat
        browserMarkFinishedPreference = findPreference("settings_browser_mark_finished") as SwitchPreferenceCompat
        browserPreviewPreference = findPreference("settings_browser_preview") as SwitchPreferenceCompat
        browserReverseLayoutPreference = findPreference("settings_browser_reverse_layout") as SwitchPreferenceCompat
        comicDualPanePreference = findPreference("settings_comic_dual_pane") as SwitchPreferenceCompat
        comicRtlPreference = findPreference("settings_comic_rtl") as SwitchPreferenceCompat
        comicScaleTypePreference = findPreference("settings_comic_scale_type")
        downloadSavePath = findPreference("settings_download_save_path")
//        downloadSeriesLimit = findPreference("settings_download_series_limit")
        epubTextZoomPreference = findPreference("settings_epub_text_zoom")
        epubMarginPreference = findPreference("settings_epub_margin")
        serverLoginPreference = findPreference("settings_server_login")
        systemOrientationPreference = findPreference("settings_system_orientation")
        systemThemePreference = findPreference("settings_system_theme")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    internal fun scrollToTop() = scrollToPreference(serverLoginPreference)

}
