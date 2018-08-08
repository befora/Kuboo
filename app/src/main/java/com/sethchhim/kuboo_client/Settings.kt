package com.sethchhim.kuboo_client

object Settings {

    val UBOOQUITY_VERSION = "2.1.1"

    //-----------------------------------------DEFAULT----------------------------------------------
    // These settings are applied on a fresh install of the app.
    const val DEFAULT_APP_THEME = 1
    const val DEFAULT_MAX_PAGE_WIDTH = 1500
    const val DEFAULT_SCALE_TYPE = 1
    const val DEFAULT_EPUB_MARGIN_SIZE = 24
    const val DEFAULT_EPUB_TEXT_ZOOM = 120

    const val DEFAULT_DOWNLOAD_TRACKING_LIMIT = 3
    const val DEFAULT_DOWNLOAD_TRACKING_INTERVAL = 180 //minutes

    const val DEFAULT_HOME_LAYOUT = 0

    const val DEFAULT_KEEP_SCREEN_ON = false
    const val DEFAULT_VOLUME_PAGE_TURN = false

    //----------------------------------------APP_THEME---------------------------------------------

    //0 = LIGHT, 1 = DARK, 2 = OLED
    var APP_THEME = 2

    val ERROR_DRAWABLE = R.mipmap.ic_launcher

    //--------------------------------------UI_PREFERENCES------------------------------------------
    //0 = AUTO_ROTATE, 1 = LOCK_PORTRAIT, 2 = LOCK_LANDSCAPE
    var SCREEN_ORIENTATION = 0

    //Screen rotation delay before animation.
    const val SHARED_ELEMENT_TRANSITION_DURATION = 350L

    // HOME_LAYOUT 0 = RECENTLY VIEWED ONLY
    // HOME_LAYOUT 1 = RECENTLY VIEWED + LATEST
    var HOME_LAYOUT = DEFAULT_HOME_LAYOUT

    var IMMERSIVE_BROWSER = false
    var MARK_FINISHED = false
    var REVERSE_LAYOUT = false
    var FAVORITE = true
    var PREVIEW = true

    val CONFIGURATION_CHANGE_DELAY: Long = 400
    const val RECYCLERVIEW_DELAY: Long = 600

    //-----------------------------------CONNECTION_PREFERENCES-------------------------------------
    var HTTPS_ENABLED = true
    var WIFI_ONLY = false
    val BUFFER_SIZE: Long = 4096

    //-----------------------------------DOWNLOAD_PREFERENCES-------------------------------------
    var DOWNLOAD_SAVE_PATH = ""
    var DOWNLOAD_TRACKING_LIMIT = DEFAULT_DOWNLOAD_TRACKING_LIMIT
    var DOWNLOAD_TRACKING_INTERVAL = DEFAULT_DOWNLOAD_TRACKING_INTERVAL

    //-------------------------------------READER_PREFERENCES---------------------------------------
    var SCALE_TYPE = 1
    var DUAL_PANE = false
    var RTL = false
    var PIP_MODE = false
    var KEEP_SCREEN_ON = DEFAULT_KEEP_SCREEN_ON
    var VOLUME_PAGE_TURN = DEFAULT_VOLUME_PAGE_TURN

    var MAX_PAGE_WIDTH = 1500
    var MAX_PAGE_HEIGHT = 2000

    var THUMBNAIL_SIZE_RECENT = 500 //auto generated in recentAdapter


    var EPUB_TEXT_ZOOM = 120
    val EPUB_LINE_HEIGHT = 24
    var EPUB_MARGIN_SIZE = 24
    val EPUB_FONT_PATH = "file:///android_asset/lora_regular.ttf"
    val EPUB_FONT_COLOR = "#E2E2E2"
    val EPUB_BACKGROUND_COLOR = "#000000"

}