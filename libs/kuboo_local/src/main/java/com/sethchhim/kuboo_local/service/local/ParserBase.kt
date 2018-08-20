package com.sethchhim.kuboo_local.service.local

import com.sethchhim.kuboo_local.model.ComicInfo
import com.sethchhim.kuboo_local.service.handler.HandlerComicInfo
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.InputStream
import java.io.StringReader
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.xml.parsers.SAXParserFactory
import kotlin.experimental.and
import kotlin.experimental.or

open class ParserBase {

    var filePath = ""
    var comicInfo = ComicInfo()

    fun isImage(filename: String) = filename.toLowerCase().matches(".*\\.(jpg|jpeg|bmp|gif|png|webp)$".toRegex())

    fun isComicInfo(filename: String) = filename.equals("comicinfo.xml", ignoreCase = true)

    fun handleComicInfo(inputStream: InputStream) {
        val comicInfoString = inputStream.bufferedReader().use { it.readText() }
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(InputSource(StringReader(comicInfoString)), HandlerComicInfo(comicInfo))
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun MD5(string: String): String {
        return try {
            val strBytes = string.toByteArray()
            val messageDigest = MessageDigest.getInstance("MD5")
            val digest = messageDigest.digest(strBytes)
            val sb = StringBuffer()
            for (i in digest.indices) {
                sb.append(Integer.toHexString((digest[i] and 0xFF.toByte() or 0x100.toByte()).toInt()).substring(1, 3))
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            string.replace("/", "")
        }
    }

}