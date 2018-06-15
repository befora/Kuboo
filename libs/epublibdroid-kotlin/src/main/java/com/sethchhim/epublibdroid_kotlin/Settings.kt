package com.sethchhim.epublibdroid_kotlin

object Settings {
    const val DEFAULT_BACKGROUND_COLOR = "#000000"
    const val DEFAULT_FONT_PATH = "file:///android_asset/lora_regular.ttf"
    const val DEFAULT_FONT_COLOR = "#E2E2E2"
    const val DEFAULT_MARGIN_SIZE = 24
    const val DEFAULT_TEXT_ZOOM = 120
    const val DEFAULT_LINE_HEIGHT = 24
    const val DEFAULT_SCROLL_DURATION = 300

    lateinit var BACKGROUND_COLOR: String
    lateinit var FONT_PATH: String
    lateinit var FONT_COLOR: String
    var LINE_HEIGHT = -1
    var MARGIN_SIZE = -1
    var SCROLL_DURATION = -1
    var TEXT_ZOOM = -1
}