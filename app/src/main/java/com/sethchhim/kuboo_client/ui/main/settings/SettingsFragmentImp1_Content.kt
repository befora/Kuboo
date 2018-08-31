package com.sethchhim.kuboo_client.ui.main.settings

import android.widget.TextView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.util.DialogUtil
import org.jetbrains.anko.sdk25.coroutines.onClick

open class SettingsFragmentImp1_Content : SettingsFragmentImp0_View() {

    override fun onResume() {
        super.onResume()
        setAboutVersionPreference()

        setBrowserFavoritePreference()
        setBrowserMarkFinishedPreference()
        setBrowserPreviewPreference()
        setBrowserReverseLayoutPreference()

        setDownloadFinishedNotification()
        setDownloadSavePath()
        setDownloadTrackingLimit()
        setDownloadTrackingInterval()

        setHomeLayout()

        setReaderKeepScreenOn()
        setReaderVolumePageTurnPreference()

        setServerLoginPreference()

        setSystemThemePreference()
        setSystemOrientationPreference()
        setSystemWifiOnlyPreference()
    }

    private fun setAboutVersionPreference() = aboutVersionPreference.apply {
        summary = systemUtil.getVersionName()
        setOnPreferenceClickListener {
            mainActivity.showChangeLog()
            true
        }
    }

    private fun setBrowserFavoritePreference() = browserFavoritePreference.apply {
        isChecked = Settings.FAVORITE
        setOnPreferenceClickListener {
            Settings.FAVORITE = !Settings.FAVORITE
            sharedPrefsHelper.saveFavorite()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setBrowserMarkFinishedPreference() = browserMarkFinishedPreference.apply {
        isChecked = Settings.MARK_FINISHED
        setOnPreferenceClickListener {
            Settings.MARK_FINISHED = !Settings.MARK_FINISHED
            sharedPrefsHelper.saveMarkFinished()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setBrowserPreviewPreference() = browserPreviewPreference.apply {
        isChecked = Settings.PREVIEW
        setOnPreferenceClickListener {
            Settings.PREVIEW = !Settings.PREVIEW
            sharedPrefsHelper.savePreview()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setBrowserReverseLayoutPreference() = browserReverseLayoutPreference.apply {
        isChecked = Settings.REVERSE_LAYOUT
        setOnPreferenceClickListener {
            Settings.REVERSE_LAYOUT = !Settings.REVERSE_LAYOUT
            sharedPrefsHelper.saveReverseLayout()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setDownloadFinishedNotification() = downloadFinishedNotification.apply {
        isChecked = Settings.DOWNLOAD_FINISHED_NOTIFICATION
        setOnPreferenceClickListener {
            Settings.DOWNLOAD_FINISHED_NOTIFICATION = !Settings.DOWNLOAD_FINISHED_NOTIFICATION
            sharedPrefsHelper.saveFinishedNotification()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setDownloadSavePath() = downloadSavePath.apply {
        summary = Settings.DOWNLOAD_SAVE_PATH
        setOnPreferenceClickListener {
            val storageList = mainActivity.systemUtil.getStorageList()
            val storageListFormatted = mainActivity.systemUtil.getStorageListFormatted()
            dialogUtil.getDialogDownloadSavePath(mainActivity, storageList, storageListFormatted, object : DialogUtil.OnDialogSelectSingleChoice {
                override fun onSelect(which: Int) {
                    val path = storageList[which]
                    Settings.DOWNLOAD_SAVE_PATH = path
                    sharedPrefsHelper.saveDownloadSavePath()
                    summary = Settings.DOWNLOAD_SAVE_PATH
                }
            }).show()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setDownloadTrackingLimit() = downloadTrackingLimit.apply {
        summary = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
        setOnPreferenceClickListener {
            dialogUtil.getDialogTrackingLimit(mainActivity).apply {
                setOnDismissListener { mainActivity.trackingService.startOneTimeTrackingService(viewModel.getActiveLogin()) }
                show()

                val textView = findViewById<TextView>(R.id.dialog_layout_settings_tracking_limit_textView0)!!
                val buttonDecrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_limit_button0)!!
                val buttonIncrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_limit_button1)!!

                textView.text = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                buttonDecrease.onClick {
                    Settings.DOWNLOAD_TRACKING_LIMIT -= 1
                    if (Settings.DOWNLOAD_TRACKING_LIMIT < 1) Settings.DOWNLOAD_TRACKING_LIMIT = 1
                    sharedPrefsHelper.saveDownloadTrackingLimit()
                    textView.text = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                    summary = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                }
                buttonIncrease.onClick {
                    Settings.DOWNLOAD_TRACKING_LIMIT += 1
                    sharedPrefsHelper.saveDownloadTrackingLimit()
                    textView.text = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                    summary = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                }
            }
            return@setOnPreferenceClickListener true
        }
    }

    private fun setDownloadTrackingInterval() = downloadTrackingInterval.apply {
        val minHours = 6
        val maxHours = 24
        //previous version used minutes so we need to reset values to reflect hours
        if (Settings.DOWNLOAD_TRACKING_INTERVAL < minHours || Settings.DOWNLOAD_TRACKING_INTERVAL >= maxHours) {
            Settings.DOWNLOAD_TRACKING_INTERVAL = Settings.DEFAULT_DOWNLOAD_TRACKING_INTERVAL
            sharedPrefsHelper.saveDownloadTrackingInterval()
        }
        summary = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
        setOnPreferenceClickListener {
            dialogUtil.getDialogTrackingInterval(mainActivity).apply {
                setOnDismissListener { mainActivity.trackingService.startOneTimeTrackingService(viewModel.getActiveLogin()) }
                show()
                val textView = findViewById<TextView>(R.id.dialog_layout_settings_tracking_interval_textView0)!!
                val buttonDecrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_interval_button0)!!
                val buttonIncrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_interval_button1)!!

                textView.text = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                buttonDecrease.onClick {
                    Settings.DOWNLOAD_TRACKING_INTERVAL -= 1
                    if (Settings.DOWNLOAD_TRACKING_INTERVAL < minHours) Settings.DOWNLOAD_TRACKING_INTERVAL = minHours
                    sharedPrefsHelper.saveDownloadTrackingInterval()
                    textView.text = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                    summary = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                }
                buttonIncrease.onClick {
                    Settings.DOWNLOAD_TRACKING_INTERVAL += 1
                    if (Settings.DOWNLOAD_TRACKING_INTERVAL < minHours) Settings.DOWNLOAD_TRACKING_INTERVAL = minHours
                    else if (Settings.DOWNLOAD_TRACKING_INTERVAL >= maxHours) Settings.DOWNLOAD_TRACKING_INTERVAL = maxHours
                    sharedPrefsHelper.saveDownloadTrackingInterval()
                    textView.text = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                    summary = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                }
            }
            return@setOnPreferenceClickListener true
        }
    }

    private fun setHomeLayout() = homeLayoutPreference.apply {
        val stringArray = resources.getStringArray(R.array.settings_layout_entries)
        summary = when (Settings.HOME_LAYOUT) {
            0 -> stringArray[0]
            1 -> stringArray[1]
            2 -> stringArray[2]
            else -> "ERROR"
        }

        setOnPreferenceClickListener {
            dialogUtil.getDialogHomeLayout(mainActivity, object : DialogUtil.OnDialogSelect2 {
                override fun onSelect0() = saveHomeLayout(0)
                override fun onSelect1() = saveHomeLayout(1)
                override fun onSelect2() = saveHomeLayout(2)

                private fun saveHomeLayout(layout: Int) {
                    Settings.HOME_LAYOUT = layout
                    sharedPrefsHelper.saveHomeLayout()
                    summary = stringArray[layout]
                }
            }).show()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setReaderKeepScreenOn() = systemKeepScreenOn.apply {
        isChecked = Settings.KEEP_SCREEN_ON
        setOnPreferenceClickListener {
            Settings.KEEP_SCREEN_ON = !Settings.KEEP_SCREEN_ON
            sharedPrefsHelper.saveKeepScreenOn()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setReaderVolumePageTurnPreference() = systemVolumePageTurnPreference.apply {
        isChecked = Settings.VOLUME_PAGE_TURN
        setOnPreferenceClickListener {
            Settings.VOLUME_PAGE_TURN = !Settings.VOLUME_PAGE_TURN
            sharedPrefsHelper.saveVolumePageTurn()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setServerLoginPreference() = serverLoginPreference.apply {
        setOnPreferenceClickListener {
            mainActivity.showFragmentLoginBrowser()
            return@setOnPreferenceClickListener true
        }

        val activeLogin = viewModel.getActiveLogin()
        when (activeLogin.isEmpty()) {
            true -> {
                R.layout.settings_item_preference_normal
                layoutResource = R.layout.settings_item_preference_error
                summary = getString(R.string.settings_none)
            }
            false -> {
                layoutResource = R.layout.settings_item_preference_normal
                summary = when (activeLogin.nickname.isEmpty()) {
                    true -> activeLogin.server
                    false -> activeLogin.nickname
                }
            }
        }
    }

    private fun setSystemThemePreference() = systemThemePreference.apply {
        val stringArray = resources.getStringArray(R.array.settings_theme_entries)
        summary = when (Settings.APP_THEME) {
            0 -> stringArray[0]
            1 -> stringArray[1]
            2 -> stringArray[2]
            else -> "ERROR"
        }

        setOnPreferenceClickListener {
            dialogUtil.getDialogAppTheme(mainActivity, object : DialogUtil.OnDialogSelect2 {
                override fun onSelect0() = saveTheme(0)
                override fun onSelect1() = saveTheme(1)
                override fun onSelect2() = saveTheme(2)

                private fun saveTheme(appTheme: Int) {
                    //show dialog first before applying theme
                    dialogUtil.getDialogRequestRestart(mainActivity, object : DialogUtil.OnDialogSelect0 {
                        override fun onSelect0() {
                            mainActivity.recreate()
                        }
                    }).show()

                    Settings.APP_THEME = appTheme
                    sharedPrefsHelper.saveAppTheme()
                }
            }).show()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setSystemOrientationPreference() = systemOrientationPreference.apply {
        summary = getScreenOrientationSummary()

        setOnPreferenceClickListener {
            dialogUtil.getDialogAppOrientation(mainActivity, object : DialogUtil.OnDialogSelect2 {
                override fun onSelect0() = saveOrientation(0)
                override fun onSelect1() = saveOrientation(1)
                override fun onSelect2() = saveOrientation(2)

                private fun saveOrientation(appOrientation: Int) {
                    Settings.SCREEN_ORIENTATION = appOrientation
                    sharedPrefsHelper.saveScreenOrientation()

                    summary = getScreenOrientationSummary()

                    mainActivity.forceOrientationSetting()
                }
            }).show()
            return@setOnPreferenceClickListener true
        }
    }

    private fun getScreenOrientationSummary(): String {
        val stringArray = resources.getStringArray(R.array.settings_orientation_entries)
        return when (Settings.SCREEN_ORIENTATION) {
            0 -> stringArray[0]
            1 -> stringArray[1]
            2 -> stringArray[2]
            else -> "ERROR"
        }
    }

    private fun setSystemWifiOnlyPreference() = systemWifiOnlyPreference.apply {
        isChecked = Settings.WIFI_ONLY
        setOnPreferenceClickListener {
            Settings.WIFI_ONLY = !Settings.WIFI_ONLY
            sharedPrefsHelper.saveWifiOnly()
            return@setOnPreferenceClickListener true
        }
    }

}