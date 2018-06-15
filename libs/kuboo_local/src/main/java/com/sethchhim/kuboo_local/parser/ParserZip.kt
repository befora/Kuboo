package com.sethchhim.kuboo_local.parser

import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ParserZip : ParserBase(), Parser {

    private lateinit var mEntries: ArrayList<ZipEntry>
    private lateinit var mZipFile: ZipFile

    override var filePath = ""

    @Throws(IOException::class)
    override fun parse(file: File) {
        mZipFile = ZipFile(file.absolutePath)
        mEntries = ArrayList()

        val e = mZipFile.entries()
        while (e.hasMoreElements()) {
            val ze = e.nextElement()
            if (!ze.isDirectory && isImage(ze.name)) {
                mEntries.add(ze)
            }
        }

        mEntries.sortBy { it.name }
    }

    override fun numPages() = mEntries.size

    @Throws(IOException::class)
    override fun getPage(num: Int) = mZipFile.getInputStream(mEntries[num])

    @Throws(IOException::class)
    override fun destroy() = mZipFile.close()

}