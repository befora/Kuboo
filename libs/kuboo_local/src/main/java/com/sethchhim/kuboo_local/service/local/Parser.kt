package com.sethchhim.kuboo_local.service.local

import com.sethchhim.kuboo_local.model.ChapterInfo
import java.io.File
import java.io.IOException
import java.io.InputStream

interface Parser {

    var chapterInfo: ChapterInfo
    var filePath: String

    @Throws(IOException::class)
    fun parse(file: File)

    @Throws(IOException::class)
    fun destroy()

    @Throws(IOException::class)
    fun getPage(num: Int): InputStream

    fun numPages(): Int

}