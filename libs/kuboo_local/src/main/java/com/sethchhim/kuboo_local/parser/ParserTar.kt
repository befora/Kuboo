package com.sethchhim.kuboo_local.parser


import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.*
import java.util.*

class ParserTar : ParserBase(), Parser {

    private lateinit var mEntries: MutableList<TarEntry>

    override var filePath = ""

    private inner class TarEntry(internal val entry: TarArchiveEntry, internal val bytes: ByteArray)

    @Throws(IOException::class)
    override fun parse(file: File) {
        mEntries = ArrayList()

        val fis = BufferedInputStream(FileInputStream(file))
        val `is` = TarArchiveInputStream(fis)
        var entry: TarArchiveEntry? = `is`.nextTarEntry
        while (entry != null) {
            if (entry.isDirectory) {
                continue
            }
            if (isImage(entry.name)) {
                mEntries.add(TarEntry(entry, `is`.readBytes()))
            }
            entry = `is`.nextTarEntry
        }

        mEntries.sortBy { it.entry.name }
    }

    override fun numPages() = mEntries.size

    @Throws(IOException::class)
    override fun getPage(num: Int) = ByteArrayInputStream(mEntries[num].bytes)

    @Throws(IOException::class)
    override fun destroy() {
    }

}
