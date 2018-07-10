package com.sethchhim.kuboo_local.parser

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.impl.FileVolumeManager
import com.github.junrar.rarfile.FileHeader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class ParserRar : ParserBase(), Parser {

    private val mHeaders = ArrayList<FileHeader>()

    private lateinit var mArchive: Archive
    private var mSolidFileExtracted = false

    override var filePath = ""

    @Throws(IOException::class)
    override fun parse(file: File) {
        try {
            mArchive = Archive(FileVolumeManager(file))
        } catch (e: RarException) {
            throw IOException("unable to open archive")
        }

        var header = mArchive.nextFileHeader()
        while (header != null) {
            if (!header.isDirectory) {
                val name = getName(header)
                if (isImage(name)) {
                    mHeaders.add(header)
                }
            }

            header = mArchive.nextFileHeader()
        }

        mHeaders.sortBy { getName(it) }
    }

    private fun getName(header: FileHeader): String {
        return if (header.isUnicode) header.fileNameW else header.fileNameString
    }

    override fun numPages() = mHeaders.size

    @Throws(Exception::class)
    override fun getPage(num: Int): InputStream {
        if (mArchive.mainHeader.isSolid) {
            // solid archives require special treatment
            synchronized(this) {
                if (!mSolidFileExtracted) {
                    for (h in mArchive.fileHeaders) {
                        if (!h.isDirectory && isImage(getName(h))) {
                            return mArchive.getInputStream(h)
                        }
                    }
                    mSolidFileExtracted = true
                }
            }
        }
        return mArchive.getInputStream(mHeaders[num])
    }


    @Throws(IOException::class)
    override fun destroy() {
        mArchive.close()
    }

}