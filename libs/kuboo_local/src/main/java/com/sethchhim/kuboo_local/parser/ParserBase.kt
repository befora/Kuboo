package com.sethchhim.kuboo_local.parser

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import kotlin.experimental.or

open class ParserBase {

    fun isImage(filename: String) = filename.toLowerCase().matches(".*\\.(jpg|jpeg|bmp|gif|png|webp)$".toRegex())

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