package com.sethchhim.epublibdroid_kotlin

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.webkit.WebView
import com.sethchhim.epublibdroid_kotlin.custom.EpubReaderListener

open class EpubReaderViewImpl0_View @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr) {

    lateinit var epubReaderListener: EpubReaderListener

    fun setMargin(marginSize: Int) {
        Settings.MARGIN_SIZE = marginSize
    }

    fun setTextZoom(textZoom: Int) {
        Settings.TEXT_ZOOM = textZoom
    }

    fun setCustomBackgroundColor(backgroundColor: String) {
        Settings.BACKGROUND_COLOR = backgroundColor
    }

    fun setFontPath(fontPath: String) {
        Settings.FONT_PATH = fontPath
    }

    fun setFontColor(fontColor: String) {
        Settings.FONT_COLOR = fontColor
    }

    fun setLineHeight(lineHeight: Int) {
        Settings.LINE_HEIGHT = lineHeight
    }

    fun setScrollDuration(scrollDuration: Int) {
        Settings.SCROLL_DURATION = scrollDuration
    }

    protected fun setWebViewSettings() {
        settings.apply {
            javaScriptEnabled = true
            defaultTextEncodingName = "UTF-8"
        }
    }

    private fun alertDialog(title: String, Message: String) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(Message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()
    }
}