package com.sethchhim.kuboo_client

import com.sethchhim.kuboo_client.data.enum.AppTheme
import com.sethchhim.kuboo_client.data.enum.HomeLayout
import com.sethchhim.kuboo_client.data.enum.ScaleType
import com.sethchhim.kuboo_client.data.enum.ScreenOrientation

object Settings {

    const val UBOOQUITY_VERSION = "2.1.1"

    //-----------------------------------------DEFAULT----------------------------------------------
    // These settings are applied on a fresh install of the app.
    val DEFAULT_APP_THEME = AppTheme.DARK.value
    val DEFAULT_HOME_LAYOUT = HomeLayout.RECENTLY_VIEWED_ONLY.value
    val DEFAULT_SCREEN_ORIENTATION = ScreenOrientation.AUTO.value
    val DEFAULT_SCALE_TYPE = ScaleType.ASPECT_FIT.value

    const val DEFAULT_EPUB_MARGIN_SIZE = 24
    const val DEFAULT_EPUB_TEXT_ZOOM = 120
    const val DEFAULT_MAX_PAGE_WIDTH = 1500

    const val DEFAULT_DOWNLOAD_TRACKING_LIMIT = 3
    const val DEFAULT_DOWNLOAD_TRACKING_INTERVAL = 12 //hours

    const val DEFAULT_KEEP_SCREEN_ON = false
    const val DEFAULT_VOLUME_PAGE_TURN = false

    //----------------------------------------APP_THEME---------------------------------------------
    var APP_THEME = DEFAULT_APP_THEME
    val ERROR_DRAWABLE = R.mipmap.ic_launcher

    //----------------------------------DATABASE_PREFERENCES----------------------------------------
    const val LOG_LIMIT = 1000

    //--------------------------------------UI_PREFERENCES------------------------------------------
    var HOME_LAYOUT = DEFAULT_HOME_LAYOUT
    var SCREEN_ORIENTATION = DEFAULT_SCREEN_ORIENTATION

    var FAVORITE = true
    var IMMERSIVE_BROWSER = false
    var MARK_FINISHED = false
    var PREVIEW = true
    var REVERSE_LAYOUT = false
    var BROWSER_MEDIA_FORCE_LIST = true

    const val SHARED_ELEMENT_TRANSITION_DURATION = 350L //Screen rotation delay before animation.
    const val RECYCLER_VIEW_DELAY = 600L

    //-----------------------------------CONNECTION_PREFERENCES-------------------------------------
    var WIFI_ONLY = false

    //-----------------------------------DOWNLOAD_PREFERENCES-------------------------------------
    var DOWNLOAD_SAVE_PATH = ""
    var DOWNLOAD_TRACKING_LIMIT = DEFAULT_DOWNLOAD_TRACKING_LIMIT
    var DOWNLOAD_TRACKING_INTERVAL = DEFAULT_DOWNLOAD_TRACKING_INTERVAL

    //-------------------------------------READER_PREFERENCES---------------------------------------
    var DUAL_PANE = false
    var KEEP_SCREEN_ON = DEFAULT_KEEP_SCREEN_ON
    var MAX_PAGE_WIDTH = 1500
    var MAX_PAGE_HEIGHT = 2000
    var PIP_MODE = false
    var RTL = false
    var SCALE_TYPE = DEFAULT_SCALE_TYPE
    var THUMBNAIL_SIZE_RECENT = 500 //auto generated in recentAdapter
    var VOLUME_PAGE_TURN = DEFAULT_VOLUME_PAGE_TURN

    var EPUB_TEXT_ZOOM = 120
    val EPUB_LINE_HEIGHT = 24
    var EPUB_MARGIN_SIZE = 24
    val EPUB_FONT_PATH = "file:///android_asset/lora_regular.ttf"
    val EPUB_FONT_COLOR = "#E2E2E2"
    val EPUB_BACKGROUND_COLOR = "#000000"

}