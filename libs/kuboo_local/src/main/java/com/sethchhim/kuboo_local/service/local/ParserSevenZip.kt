package com.sethchhim.kuboo_local.service.local

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class ParserSevenZip : ParserBase(), Parser {

    private lateinit var mEntries: MutableList<SevenZEntry>

    private inner class SevenZEntry(internal val entry: SevenZArchiveEntry, internal val bytes: ByteArray)

    @Throws(IOException::class)
    override fun parse(file: File) {
        mEntries = ArrayList()

        val sevenZFile = SevenZFile(file)
        var entry: SevenZArchiveEntry? = sevenZFile.nextEntry
        while (entry != null) {
            if (entry.isDirectory) {
                continue
            }
            val name = entry.name
            if (isImage(name)) {
                val content = ByteArray(entry.size.toInt())
                sevenZFile.read(content)
                mEntries.add(SevenZEntry(entry, content))
            }
            if (isComicInfo(name)) {
                val content = ByteArray(entry.size.toInt())
                sevenZFile.read(content)
                handleComicInfo(ByteArrayInputStream(SevenZEntry(entry, content).bytes))
            }
            entry = sevenZFile.nextEntry
        }

        mEntries.sortBy { it.entry.name }
    }

    override fun numPages() = mEntries.size

    @Throws(IOException::class)
    override fun getPage(num: Int): InputStream = ByteArrayInputStream(mEntries[num].bytes)

    @Throws(IOException::class)
    override fun destroy() {
    }

}
