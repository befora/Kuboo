package com.sethchhim.kuboo_client.ui.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.view.WindowManager
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.ui.base.custom.LoadingStage
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

@SuppressLint("Registered")
open class BaseActivityImpl1_Dialog : BaseActivityImpl0_View() {

    internal lateinit var bookmarkDialog: AlertDialog
    internal lateinit var loadingDialog: AlertDialog

    protected var isLoadingCancelled = false
    protected var isLoadingRequired = true

    internal fun showChangeLog() = dialogUtil.getDialogChangeLog(this).apply {
        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_dismiss)) { dialogInterface, _ -> dialogInterface.dismiss() }
        setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.dialog_rate)) { _, _ -> systemUtil.launchPlayStore() }
        show()
        window?.attributes = WindowManager.LayoutParams().apply {
            val systemWidth = systemUtil.getSystemWidth()
            val systemHeight = systemUtil.getSystemHeight()
            val newSize = (Math.min(systemWidth, systemHeight) * 0.9f).toInt()
            width = newSize
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    protected fun showLoadingDialog(loadingStage: LoadingStage) = loadingDialog.apply {
        isLoadingCancelled = false
        isLoadingRequired = true
        val isReader = loadingStage == LoadingStage.SINGLE || loadingStage == LoadingStage.DUAL

        setMessage(when (loadingStage) {
            LoadingStage.PING -> getString(R.string.dialog_connecting_to_server)
            LoadingStage.BOOKMARK -> getString(R.string.dialog_loading_bookmark)
            LoadingStage.ASSET -> getString(R.string.dialog_loading_assets)
            LoadingStage.SINGLE -> getString(R.string.dialog_loading_single_pane_mode)
            LoadingStage.DUAL -> getString(R.string.dialog_loading_dual_pane_mode)
        })

        if (!isShowing) {
            setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_cancel)) { _, _ -> onLoadingDialogCancel(isReader) }

            if (isReader) {
                setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.dialog_single_pane)) { _, _ -> onLoadingDialogSinglePane() }
            }

            if (isLoadingRequired) show()
        }
    }

    private fun Dialog.onLoadingDialogCancel(isReader: Boolean) {
        isLoadingCancelled = true
        if (isShowing) dismiss()
        if (isReader) finish()
    }

    private fun Dialog.onLoadingDialogSinglePane() {
        if (isShowing) dismiss()
        Settings.DUAL_PANE = false
        sharedPrefsHelper.saveDualPane()
        recreate()
    }

    protected fun hideLoadingDialog() = loadingDialog.apply {
        if (isShowing) dismiss()
    }

    internal fun showToastDebug() {
        if (systemUtil.isDebugBuild()) {
            try {
                longToast("DEBUG")
            } catch (e: Exception) { //ignore
            }
        }
    }

    internal fun showToastError() = try {
        toast(R.string.login_something_went_wrong)
    } catch (e: Exception) { //ignore
    }

    internal fun showToastFailedToLoadImageAssets() = try {
        toast(R.string.main_failed_to_load_assets)
    } catch (e: Exception) { //ignore
    }

    internal fun showToastFileDoesNotExist() = try {
        toast(R.string.dialog_file_does_not_exist)
    } catch (e: Exception) { //ignore
    }

    internal fun showToastFileTypeNotSupported() = try {
        toast(R.string.main_file_type_not_supported)
    } catch (e: Exception) { //ignore
    }

}