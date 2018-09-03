package com.sethchhim.kuboo_remote

import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import java.net.URL

object Extensions {

    fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

}