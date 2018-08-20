package com.sethchhim.kuboo_local.service.local

import com.sethchhim.kuboo_local.model.ComicInfo
import java.io.File
import java.io.IOException
import java.io.InputStream

interface Parser {

    var comicInfo: ComicInfo
    var filePath: String

    @Throws(IOException::class)
    fun parse(file: File)

    @Throws(IOException::class)
    fun destroy()

    @Throws(IOException::class)
    fun getPage(num: Int): InputStream

    fun numPages(): Int

}