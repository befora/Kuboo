package com.sethchhim.kuboo_local.service.local

import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ParserZip : ParserBase(), Parser {

    private lateinit var mEntries: ArrayList<ZipEntry>
    private lateinit var mZipFile: ZipFile

    @Throws(IOException::class)
    override fun parse(file: File) {
        mZipFile = ZipFile(file.absolutePath)
        mEntries = ArrayList()

        val e = mZipFile.entries()
        while (e.hasMoreElements()) {
            e.nextElement()?.apply {
                if (!isDirectory) {
                    if (isImage(name)) mEntries.add(this)
                    if (isComicInfo(name)) handleComicInfo(mZipFile.getInputStream(this))
                }
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