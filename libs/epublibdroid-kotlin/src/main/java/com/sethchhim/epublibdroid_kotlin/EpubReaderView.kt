package com.sethchhim.epublibdroid_kotlin

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.sethchhim.epublibdroid_kotlin.custom.GestureListener

@SuppressLint("SetJavaScriptEnabled")
class EpubReaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : EpubReaderViewImpl1_Content(context, attrs, defStyleAttr) {

    init {
        setWebViewSettings()
        setOnTouchListener(object : GestureListener(context) {
            override fun onClick() {
                super.onClick()
                epubReaderListener.onClickEpubReaderView()
            }

            override fun onLongPress() {
                super.onLongPress()
                epubReaderListener.onLongPressEpubReaderView()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                goToPreviousPage()
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                goToNextPage()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                goToNextPage()
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                goToPreviousPage()
            }
        })
    }

    fun restoreSettings(backgroundColor: String = Settings.DEFAULT_BACKGROUND_COLOR,
                        fontColor: String = Settings.DEFAULT_FONT_COLOR,
                        fontPath: String = Settings.DEFAULT_FONT_PATH,
                        lineHeight: Int = Settings.DEFAULT_LINE_HEIGHT,
                        marginSize: Int = Settings.DEFAULT_MARGIN_SIZE,
                        scrollDuration: Int = Settings.DEFAULT_SCROLL_DURATION,
                        textZoom: Int = Settings.DEFAULT_TEXT_ZOOM) {
        setCustomBackgroundColor(backgroundColor)
        setFontColor(fontColor)
        setFontPath(fontPath)
        setLineHeight(lineHeight)
        setMargin(marginSize)
        setScrollDuration(scrollDuration)
        setTextZoom(textZoom)

        applyTextZoom(Settings.TEXT_ZOOM)
    }

}