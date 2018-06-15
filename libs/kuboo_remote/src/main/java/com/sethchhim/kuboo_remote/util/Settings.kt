package com.sethchhim.kuboo_remote.util

object Settings {

    internal val isDebugOkHttp = true
    internal val isDebugOpdsHandler = false
    internal val isDebugPaginationHandler = false
    internal val isDebugItemCountHandler = false

    internal val CONNECTION_TIMEOUT = 15000
    internal val READ_TIMEOUT = 15000

    internal val CACHE_SIZE = 100 //megabytes

    val MAX_PAGE_WIDTH_DEFAULT = 500

}
