package com.sethchhim.kuboo_client.ui.main.settings

import android.support.v7.app.AlertDialog
import android.widget.TextView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.util.DialogUtil

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
        setDownloadTrackingHideFinished()

        setHomeLayout()

        setReaderKeepScreenOn()
        setReaderVolumePageTurnPreference()

        setServerLoginPreference()

        setSystemThemePreference()
        setSystemOrientationPreference()
        setSystemWifiOnlyPreference()
        setSystemStartTab()

        setAdvancedPreference()
    }

    private fun setAdvancedPreference() = advancedPreference.apply {
        setOnPreferenceClickListener {
            mainActivity.showFragmentSettingsAdvanced()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setAboutVersionPreference() = aboutVersionPreference.apply {
        summary = systemUtil.getVersionName()
        setOnPreferenceClickListener {
            mainActivity.showChangeLog()
            return@setOnPreferenceClickListener true
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

    private fun setDownloadSavePath() {
        downloadSavePath.summary = Settings.DOWNLOAD_SAVE_PATH
        downloadSavePath.setOnPreferenceClickListener {
            val storageList = mainActivity.systemUtil.getStorageList()
            val storageListFormatted = mainActivity.systemUtil.getStorageListFormatted()
            var path = Settings.DOWNLOAD_SAVE_PATH
            val dialog = dialogUtil.getDialogDownloadSavePath(mainActivity, storageList, storageListFormatted, object : DialogUtil.OnDialogSelectSingleChoice {
                override fun onSelect(which: Int) {
                    path = storageList[which]
                }
            })
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.settings_apply)) { _, _ -> showDownloadSavePathConfirmDialog(path) }
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.settings_cancel)) { dialogInterface, _ -> dialogInterface.dismiss() }
            dialog.show()
            return@setOnPreferenceClickListener true
        }
    }

    private fun showDownloadSavePathConfirmDialog(path: String) {
        val confirmDialog = dialogUtil.getDialogDownloadSavePathConfirm(mainActivity)
        confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.settings_confirm)) { dialog, _ ->
            Settings.DOWNLOAD_SAVE_PATH = path
            sharedPrefsHelper.saveDownloadSavePath()
            downloadSavePath.summary = Settings.DOWNLOAD_SAVE_PATH
            mainActivity.trackingService.startTrackingServiceSingle(viewModel.getActiveLogin())
            dialog.dismiss()
        }
        confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.settings_cancel)) { dialog, _ -> dialog.dismiss() }
        confirmDialog.show()
    }

    private fun setDownloadTrackingLimit() = downloadTrackingLimit.apply {
        summary = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
        setOnPreferenceClickListener {
            dialogUtil.getDialogTrackingLimit(mainActivity).apply {
                setOnDismissListener { mainActivity.trackingService.startTrackingServiceSingle(viewModel.getActiveLogin()) }
                show()

                val textView = findViewById<TextView>(R.id.dialog_layout_settings_tracking_limit_textView0)!!
                val buttonDecrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_limit_button0)!!
                val buttonIncrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_limit_button1)!!

                textView.text = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                buttonDecrease.setOnClickListener {
                    Settings.DOWNLOAD_TRACKING_LIMIT -= 1
                    if (Settings.DOWNLOAD_TRACKING_LIMIT < 1) Settings.DOWNLOAD_TRACKING_LIMIT = 1
                    sharedPrefsHelper.saveDownloadTrackingLimit()
                    textView.text = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                    summary = "${Settings.DOWNLOAD_TRACKING_LIMIT}"
                }
                buttonIncrease.setOnClickListener {
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
                setOnDismissListener { mainActivity.trackingService.startTrackingServiceSingle(viewModel.getActiveLogin()) }
                show()
                val textView = findViewById<TextView>(R.id.dialog_layout_settings_tracking_interval_textView0)!!
                val buttonDecrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_interval_button0)!!
                val buttonIncrease = findViewById<TextView>(R.id.dialog_layout_settings_tracking_interval_button1)!!

                textView.text = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                buttonDecrease.setOnClickListener {
                    Settings.DOWNLOAD_TRACKING_INTERVAL -= 1
                    if (Settings.DOWNLOAD_TRACKING_INTERVAL < minHours) Settings.DOWNLOAD_TRACKING_INTERVAL = minHours
                    sharedPrefsHelper.saveDownloadTrackingInterval()
                    textView.text = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                    summary = "${Settings.DOWNLOAD_TRACKING_INTERVAL} ${getString(R.string.settings_hours)}"
                }
                buttonIncrease.setOnClickListener {
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

    private fun setDownloadTrackingHideFinished() = downloadTrackingHideFinished.apply {
        isChecked = Settings.DOWNLOAD_TRACKING_HIDE_FINISHED
        setOnPreferenceClickListener {
            Settings.DOWNLOAD_TRACKING_HIDE_FINISHED = !Settings.DOWNLOAD_TRACKING_HIDE_FINISHED
            sharedPrefsHelper.saveDownloadTrackingHideFinished()
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

    private fun setSystemStartTab() = systemStartTab.apply {
        summary = getStartTabSummary()

        setOnPreferenceClickListener {
            dialogUtil.getDialogStartTab(mainActivity, object : DialogUtil.OnDialogSelect2 {
                override fun onSelect0() = saveStartTab(0)
                override fun onSelect1() = saveStartTab(1)
                override fun onSelect2() = saveStartTab(2)

                private fun saveStartTab(startTab: Int) {
                    Settings.START_TAB = startTab
                    sharedPrefsHelper.saveStartTab()

                    summary = getStartTabSummary()
                }
            }).show()
            return@setOnPreferenceClickListener true
        }
    }

    private fun getStartTabSummary(): String {
        val stringArray = resources.getStringArray(R.array.settings_start_tab_entries)
        return when (Settings.START_TAB) {
            0 -> stringArray[0]
            1 -> stringArray[1]
            2 -> stringArray[2]
            else -> "ERROR"
        }
    }

}