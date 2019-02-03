package com.sethchhim.kuboo_client.ui.main.settings.advanced

import android.app.Dialog
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import org.jetbrains.anko.sdk27.coroutines.onClick

open class SettingsAdvancedFragmentImp1_Content : SettingsAdvancedFragmentImp0_View() {

    override fun onResume() {
        super.onResume()
        setRecentlyViewedHeightOffset()
        setReaderScrollOffset()
        setSystemForceDownsizing()
    }

    private fun setRecentlyViewedHeightOffset() = homeRecentlyViewedHeightOffset.apply {
        val minValue = -50
        val maxValue = 50
        summary = getRecentlyViewedHeightOffsetSummary()

        setOnPreferenceClickListener {
            dialogUtil.getDialogRecentlyViewedHeightOffset(mainActivity).apply {
                setOnDismissListener { sharedPrefsHelper.saveDownloadTrackingLimit() }
                show()

                val textView = findViewById<TextView>(R.id.dialog_layout_settings_recently_viewed_height_offset_textView0)!!
                val buttonDecrease = findViewById<TextView>(R.id.dialog_layout_settings_recently_viewed_height_offset_button0)!!
                val buttonIncrease = findViewById<TextView>(R.id.dialog_layout_settings_recently_viewed_height_offset_button1)!!
                getButton(Dialog.BUTTON_POSITIVE).onClick {
                    Settings.RECENTLY_VIEWED_HEIGHT_OFFSET = Settings.DEFAULT_RECENTLY_VIEWED_HEIGHT_OFFSET
                    sharedPrefsHelper.saveRecentlyViewedHeightOffset()
                    textView.text = getRecentlyViewedHeightOffsetSummary()
                    summary = getRecentlyViewedHeightOffsetSummary()
                }
                textView.text = getRecentlyViewedHeightOffsetSummary()
                buttonDecrease.setOnClickListener {
                    Settings.RECENTLY_VIEWED_HEIGHT_OFFSET -= 1
                    if (Settings.RECENTLY_VIEWED_HEIGHT_OFFSET < minValue) Settings.RECENTLY_VIEWED_HEIGHT_OFFSET = minValue
                    sharedPrefsHelper.saveRecentlyViewedHeightOffset()
                    textView.text = getRecentlyViewedHeightOffsetSummary()
                    summary = getRecentlyViewedHeightOffsetSummary()
                }
                buttonIncrease.setOnClickListener {
                    Settings.RECENTLY_VIEWED_HEIGHT_OFFSET += 1
                    if (Settings.RECENTLY_VIEWED_HEIGHT_OFFSET > maxValue) Settings.RECENTLY_VIEWED_HEIGHT_OFFSET = maxValue
                    sharedPrefsHelper.saveRecentlyViewedHeightOffset()
                    textView.text = getRecentlyViewedHeightOffsetSummary()
                    summary = getRecentlyViewedHeightOffsetSummary()
                }

                findViewById<TextView>(android.R.id.message)?.apply { textSize = 10F }
            }
            return@setOnPreferenceClickListener true
        }
    }

    private fun getRecentlyViewedHeightOffsetSummary(): String {
        val plus = when (Settings.RECENTLY_VIEWED_HEIGHT_OFFSET >= 0) {
            true -> "+"
            false -> ""
        }
        return "$plus${Settings.RECENTLY_VIEWED_HEIGHT_OFFSET}%"
    }

    private fun setReaderScrollOffset() = readerScrollOffset.apply {
        val minValue = -50
        val maxValue = 50
        summary = getReaderScrollOffsetSummary()

        setOnPreferenceClickListener {
            dialogUtil.getDialogReaderScrollOffset(mainActivity).apply {
                show()
                val textView = findViewById<TextView>(R.id.dialog_layout_settings_reader_scroll_offset_textView0)!!
                val buttonDecrease = findViewById<TextView>(R.id.dialog_layout_settings_reader_scroll_offset_button0)!!
                val buttonIncrease = findViewById<TextView>(R.id.dialog_layout_settings_reader_scroll_offset_button1)!!
                getButton(Dialog.BUTTON_POSITIVE).onClick {
                    Settings.READER_SCROLL_OFFSET = Settings.DEFAULT_READER_SCROLL_OFFSET
                    sharedPrefsHelper.saveReaderScrollOffset()
                    textView.text = getReaderScrollOffsetSummary()
                    summary = getReaderScrollOffsetSummary()
                }
                textView.text = getReaderScrollOffsetSummary()
                buttonDecrease.setOnClickListener {
                    Settings.READER_SCROLL_OFFSET -= 1
                    if (Settings.READER_SCROLL_OFFSET < minValue) Settings.READER_SCROLL_OFFSET = minValue
                    sharedPrefsHelper.saveReaderScrollOffset()
                    textView.text = getReaderScrollOffsetSummary()
                    summary = getReaderScrollOffsetSummary()
                }
                buttonIncrease.setOnClickListener {
                    Settings.READER_SCROLL_OFFSET += 1
                    if (Settings.READER_SCROLL_OFFSET > maxValue) Settings.READER_SCROLL_OFFSET = maxValue
                    sharedPrefsHelper.saveReaderScrollOffset()
                    textView.text = getReaderScrollOffsetSummary()
                    summary = getReaderScrollOffsetSummary()
                }

                findViewById<TextView>(android.R.id.message)?.apply { textSize = 10F }
            }
            return@setOnPreferenceClickListener true
        }
    }

    private fun getReaderScrollOffsetSummary(): String {
        val plus = when (Settings.READER_SCROLL_OFFSET >= 0) {
            true -> "+"
            false -> ""
        }
        return "$plus${Settings.READER_SCROLL_OFFSET}%"
    }

    private fun setSystemForceDownsizing() = systemForceDownsizing.apply {
        val maxValue = 9999
        setOnPreferenceClickListener {
            dialogUtil.getDialogForceDownsizing(mainActivity).apply {
                show()
                val seekBar = findViewById<SeekBar>(R.id.dialog_layout_force_downsizing_seekbar)!!
                val textView = findViewById<TextView>(R.id.dialog_layout_force_downsizing_textView)!!
                val button0 = findViewById<Button>(R.id.dialog_layout_force_downsizing_button0)!!
                val button1 = findViewById<Button>(R.id.dialog_layout_force_downsizing_button1)!!
                textView.text = "Max Width: ${Settings.MAX_PAGE_WIDTH}"
                seekBar.max = maxValue
                seekBar.progress = Settings.MAX_PAGE_WIDTH
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        textView.text = "Max Width: $progress"
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        Settings.MAX_PAGE_WIDTH = seekBar.progress
                        sharedPrefsHelper.saveMaxPageWidth()
                        textView.text = "Max Width: ${Settings.MAX_PAGE_WIDTH}"
                    }
                })
                getButton(Dialog.BUTTON_POSITIVE).onClick {
                    Settings.MAX_PAGE_WIDTH = Settings.DEFAULT_MAX_PAGE_WIDTH
                    sharedPrefsHelper.saveMaxPageWidth()
                    seekBar.progress = Settings.MAX_PAGE_WIDTH
                    textView.text = "Max Width: ${Settings.MAX_PAGE_WIDTH}"
                }
                button0.onClick {
                    if (Settings.MAX_PAGE_WIDTH > 0) {
                        Settings.MAX_PAGE_WIDTH -= 1
                        sharedPrefsHelper.saveMaxPageWidth()
                        textView.text = "Max Width: ${Settings.MAX_PAGE_WIDTH}"
                    }
                }
                button1.onClick {
                    if (Settings.MAX_PAGE_WIDTH < maxValue) {
                        Settings.MAX_PAGE_WIDTH += 1
                        sharedPrefsHelper.saveMaxPageWidth()
                        textView.text = "Max Width: ${Settings.MAX_PAGE_WIDTH}"
                    }
                }
            }
            return@setOnPreferenceClickListener true
        }
    }

}