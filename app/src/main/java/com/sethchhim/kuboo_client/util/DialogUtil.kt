package com.sethchhim.kuboo_client.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.sethchhim.kuboo_client.Extensions.guessFilename
import com.sethchhim.kuboo_client.Extensions.toReadable
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.Settings.APP_THEME
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Response
import org.jetbrains.anko.collections.forEachWithIndex
import java.io.File


class DialogUtil(val context: Context) {

    internal fun getSnackBarDeleteRecent(view: View, book: Book) = getSnackBar(view, "Removed: " + book.title, 5000, isAlert = true)

    internal fun getSnackBarDisconnected(view: View, reason: String) = getSnackBar(view, reason, 7000, isAlert = true)

    internal fun getSnackBarDownload(view: View) = getSnackBar(view, "Download Started", 7000, isAlert = false)

    internal fun getSnackBarFinishBookEnd(view: View) = getSnackBar(view, context.getString(R.string.reader_end_of_series), Snackbar.LENGTH_LONG, isAlert = true)

    internal fun getSnackBarFinishBookNext(view: View, nextBook: Book) = getSnackBar(view, context.getString(R.string.reader_next) + ": " + nextBook.title, Snackbar.LENGTH_LONG, isAlert = false)

    internal fun getSnackBarMarkFinished(view: View) = getSnackBar(view, "Mark Finished is currently disabled.", 7000, isAlert = false)

    internal fun getSnackBarResponse(view: View, response: Response) = getSnackBar(view, "${response.code} ${response.message}", 7000, isAlert = true)

    internal fun getSnackBarPermissionExternal(view: View): Snackbar? {
        val message = "Storage permissions required!"
        return getSnackBar(view, message, Snackbar.LENGTH_INDEFINITE, isAlert = true)
    }

    internal fun getDialogAppOrientation(context: Context, onDialogSelect2: OnDialogSelect2) = getAlertDialogBuilder(context).apply {
        setSingleChoiceItems(context.resources.getStringArray(R.array.settings_orientation_entries), Settings.SCREEN_ORIENTATION) { dialog, which ->
            when (which) {
                0 -> onDialogSelect2.onSelect0()
                1 -> onDialogSelect2.onSelect1()
                2 -> onDialogSelect2.onSelect2()
            }
            dialog.dismiss()
        }
    }.create()

    internal fun getDialogAppTheme(context: Context, onDialogSelect2: OnDialogSelect2) = getAlertDialogBuilder(context).apply {
        setSingleChoiceItems(context.resources.getStringArray(R.array.settings_theme_entries), Settings.APP_THEME) { dialog, which ->
            when (which) {
                0 -> onDialogSelect2.onSelect0()
                1 -> onDialogSelect2.onSelect1()
                2 -> onDialogSelect2.onSelect2()
            }
            dialog.dismiss()
        }
    }.create()

    internal fun getDialogBookmark(context: Context) = getAlertDialogBuilder(context).apply {
        setTitle(context.getString(R.string.dialog_bookmark_found))
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_bookmark, null))
    }.create()

    internal fun getDialogBookSettings(context: Context) = getAlertDialogBuilder(context).apply {
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_book_settings, null))
    }.create()

    internal fun getDialogChangeLog(context: Context) = getAlertDialogBuilder(context, appTheme = 1).apply {
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_changelog, null))
    }.create()

    internal fun getDialogDownloadStart(context: Context, downloadList: List<Book>) = getAlertDialogBuilder(context).apply {
        setTitle(context.getString(R.string.dialog_download))

        var stringMessage = ""
        downloadList.forEachWithIndex { index, book -> stringMessage = "$stringMessage \n${index + 1}. ${book.title}" }
        setMessage(stringMessage)
    }.create()

    internal fun getDialogDownloadItemSettings(context: Context, book: Book, onDialogSelect0: OnDialogSelect0) = getAlertDialogBuilder(context).apply {
        val fileName = book.getAcquisitionUrl().guessFilename()
        val filePath = book.filePath
        val fileSize = File(filePath).length().toReadable()
        val message = "File Size: $fileSize\nLocation: $filePath\n"
        setTitle(fileName)
        setMessage(message)
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_download_item_settings, null))
        setNeutralButton(context.getString(R.string.dialog_delete)) { dialog, which -> onDialogSelect0.onSelect0() }
    }.create()

    internal fun getDialogDownloadSavePath(context: Context, storageList: Array<String>, storageListFormatted: Array<String>, onDialogSelectSingleChoice: OnDialogSelectSingleChoice) = getAlertDialogBuilder(context).apply {
        val checkedItem = storageList.indexOf(Settings.DOWNLOAD_SAVE_PATH)
        setSingleChoiceItems(storageListFormatted, checkedItem) { dialog, which ->
            onDialogSelectSingleChoice.onSelect(which)
        }
    }.create()

    internal fun getDialogDownloadSavePathConfirm(context: Context) = getAlertDialogBuilder(context).apply {
        setTitle("WARNING")
        setMessage("Changing save path will reset all download tracking items!")
    }.create()

    internal fun getDialogForceDownsizing(context: Context) = getAlertDialogBuilder(context).apply {
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_reader_force_downsizing, null))
        setPositiveButton(context.getString(R.string.dialog_default)) { dialog, which ->  }
    }.create()

    internal fun getDialogHomeLayout(context: Context, onDialogSelect2: OnDialogSelect2) = getAlertDialogBuilder(context).apply {
        setSingleChoiceItems(context.resources.getStringArray(R.array.settings_layout_entries), Settings.HOME_LAYOUT) { dialog, which ->
            when (which) {
                0 -> onDialogSelect2.onSelect0()
                1 -> onDialogSelect2.onSelect1()
                2 -> onDialogSelect2.onSelect2()
            }
            dialog.dismiss()
        }
    }.create()

    internal fun getDialogHttps(context: Context, tlsCipherSuite: String) = getAlertDialogBuilder(context).apply {
        setTitle(context.getString(R.string.dialog_connection_is_encrypted))
        setMessage(tlsCipherSuite)
    }.create()

    internal fun getDialogLicense(context: Context, license: String) = getAlertDialogBuilder(context).apply {
        setMessage(license)
    }.create()

    internal fun getDialogLoading(context: Context) = getAlertDialogBuilder(context).apply {
        setTitle(context.getString(R.string.dialog_please_wait))
        setCancelable(false)
    }.create()

    internal fun getDialogInfo(context: Context) = getAlertDialogBuilder(context).apply {
    }.create()

    internal fun getDialogRecentRemove(context: Context, book: Book) = getAlertDialogBuilder(context).apply {
        setTitle(context.getString(R.string.dialog_remove_recently_viewed))
        setMessage(book.title)
    }.create()

    internal fun getDialogRecentlyViewedHeightOffset(context: Context) = getAlertDialogBuilder(context).apply {
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_settings_recently_viewed_height_offset, null))
        setPositiveButton(context.getString(R.string.dialog_default)) { dialog, which ->  }
        setMessage(context.getString(R.string.settings_adjust_height_offset_of_the_recently_viewed_row))

    }.create()

    internal fun getDialogRequestRestart(context: Context, onDialogSelect0: OnDialogSelect0) = getAlertDialogBuilder(context).apply {
        setTitle(context.getString(R.string.dialog_restart_required))
        setMessage(context.getString(R.string.dialog_please_restart_to_apply_changes))
        setPositiveButton(context.getString(R.string.dialog_restart)) { _, _ -> onDialogSelect0.onSelect0() }
        setNegativeButton(context.getString(R.string.dialog_cancel)) { dialog, _ -> dialog.dismiss() }
    }.create()

    internal fun getDialogStartTab(context: Context, onDialogSelect2: OnDialogSelect2) = getAlertDialogBuilder(context).apply {
        setSingleChoiceItems(context.resources.getStringArray(R.array.settings_start_tab_entries), Settings.START_TAB) { dialog, which ->
            when (which) {
                0 -> onDialogSelect2.onSelect0()
                1 -> onDialogSelect2.onSelect1()
                2 -> onDialogSelect2.onSelect2()
            }
            dialog.dismiss()
        }
    }.create()

    internal fun getDialogTrackingInterval(context: Context) = getAlertDialogBuilder(context).apply {
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_settings_tracking_interval, null))
    }.create()

    internal fun getDialogTrackingLimit(context: Context) = getAlertDialogBuilder(context).apply {
        setView(LayoutInflater.from(context).inflate(R.layout.dialog_layout_settings_tracking_limit, null))
    }.create()

    private fun getAlertDialogBuilder(context: Context, appTheme: Int = APP_THEME): AlertDialog.Builder {
        when (appTheme) {
            0 -> return AlertDialog.Builder(context, R.style.DialogThemeLight)
            1 -> return AlertDialog.Builder(context, R.style.DialogThemeDark)
            2 -> return AlertDialog.Builder(context, R.style.DialogThemeOled)
        }
        return AlertDialog.Builder(context)
    }

    private fun getSnackBar(view: View, string: String, delay: Int, isAlert: Boolean) = Snackbar.make(view, string, delay).apply {
        val snackView = getView()
        val snackBarActionTextId = R.id.snackbar_action
        val textViewAction: TextView = snackView.findViewById(snackBarActionTextId)!!
        textViewAction.textSize = 12F
        val snackBarTextId = R.id.snackbar_text
        textViewAction.setTypeface(textViewAction.typeface, Typeface.BOLD)
        val textView: TextView = snackView.findViewById(snackBarTextId)!!
        textView.textSize = 12F
        textView.setTextColor(Color.WHITE)
        if (isAlert) {
            snackView.setBackgroundColor(Color.RED)
            setActionTextColor(Color.WHITE)
        } else {
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightColorPrimaryDark))
            setActionTextColor(ContextCompat.getColor(context, R.color.lightColorAccent))
        }
    }

    interface OnDialogSelect0 {
        fun onSelect0()
    }

    interface OnDialogSelect1 {
        fun onSelect0()
        fun onSelect1()
    }

    interface OnDialogSelect2 {
        fun onSelect0()
        fun onSelect1()
        fun onSelect2()
    }

    interface OnDialogSelectSingleChoice {
        fun onSelect(which: Int)
    }

}


