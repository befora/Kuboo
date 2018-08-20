package com.sethchhim.kuboo_local.service.handler

import com.sethchhim.kuboo_local.model.ComicInfo
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class HandlerComicInfo(private val comicInfo: ComicInfo) : DefaultHandler() {

    private var entry = false
    private var pages = false
    private var page = false

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (qName.equals("COMICINFO", ignoreCase = true)) {
            entry = true
        }
        if (qName.equals("PAGES", ignoreCase = true)) {
            pages = true
        }
        if (qName.equals("PAGE", ignoreCase = true)) {
            page = true
            val position = attributes.getValue("Image") ?: ""
            val bookmark = attributes.getValue("Bookmark") ?: ""
            if (position.isNotEmpty() && bookmark.isNotEmpty()) {
                val positionInt = try {
                    position.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
                comicInfo.bookmarks.add(Pair(positionInt, bookmark))
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (qName.equals("COMICINFO", ignoreCase = true)) {
            entry = false
        }
        if (qName.equals("PAGES", ignoreCase = true)) {
            pages = false
        }
        if (qName.equals("PAGE", ignoreCase = true)) {
            page = false
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (entry) {
            entry = false
        }
        if (pages) {
            pages = false
        }
        if (page) {
            page = false
        }
    }

}