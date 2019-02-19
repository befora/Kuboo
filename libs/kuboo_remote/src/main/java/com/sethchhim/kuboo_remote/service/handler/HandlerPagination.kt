package com.sethchhim.kuboo_remote.service.handler

import android.os.Build
import android.text.Html
import com.sethchhim.kuboo_remote.model.OpdsEntity
import com.sethchhim.kuboo_remote.model.Pagination
import com.sethchhim.kuboo_remote.util.Settings.isDebugPaginationHandler
import org.apache.commons.text.StringEscapeUtils
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import timber.log.Timber

class HandlerPagination(private val pagination: Pagination) : DefaultHandler() {

    private val entity = OpdsEntity()
    private var linkPrevious = false
    private var linkSelf = false
    private var linkNext = false

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: org.xml.sax.Attributes) {
        if (isDebugPaginationHandler) Timber.d("Start Element: $qName")
        if (qName.equals("LINK", ignoreCase = true)) {
            if (attributes.getValue("rel") != null && !attributes.getValue("rel").isEmpty()) {
                when {
                    attributes.getValue("rel").equals("previous", ignoreCase = true) -> {
                        entity.LinkPrevious = attributes.get("href")
                        linkPrevious = true
                    }
                    attributes.getValue("rel").equals("self", ignoreCase = true) -> {
                        entity.LinkSelf = attributes.get("href")
                        linkSelf = true
                    }
                    attributes.getValue("rel").equals("next", ignoreCase = true) -> {
                        entity.LinkNext = attributes.get("href")
                        linkNext = true
                    }
                }
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (isDebugPaginationHandler) Timber.d("End Element: $qName")
        if (qName.equals("LINK", ignoreCase = true)) {
            if (linkPrevious) {
                pagination.previous = stripHtml(entity.LinkPrevious).unescape()
                linkPrevious = false
            }
            if (linkSelf) {
                pagination.self = stripHtml(entity.LinkSelf).unescape()
                linkSelf = false
            }
            if (linkNext) {
                pagination.next = stripHtml(entity.LinkNext).unescape()
                linkNext = false
                throw SAXException("End of pagination items.")
            }
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (linkPrevious) if (isDebugPaginationHandler) Timber.d("Found link previous: ${entity.LinkPrevious}")
        if (linkSelf) if (isDebugPaginationHandler) Timber.d("Found link self: ${entity.LinkSelf}")
        if (linkNext) if (isDebugPaginationHandler) Timber.d("Found link next: ${entity.LinkNext}")
    }

    private fun Attributes.get(value: String): String {
        var string = getValue(value)
        when {
            string.contains("/opds-books/") -> {
                val bits = string.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                string = bits[bits.size - 1]
            }
            string.contains("/opds-comics/") -> {
                val bits = string.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                string = bits[bits.size - 1]
            }
            string.contains("/opds/") -> {
                val bits = string.split("/opds/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                string = bits[bits.size - 1]
            }
        }
        return string
    }

    private fun stripHtml(html: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().replace("\n".toRegex(), "").trim { it <= ' ' }
    } else {
        Html.fromHtml(html).toString().replace("\n".toRegex(), "").trim { it <= ' ' }
    }

    private fun String.unescape() = unescapeAmpersand().unescapeXml()

    private fun String.unescapeXml() = StringEscapeUtils.unescapeXml(this)

    private fun String.unescapeAmpersand() = replace("{ampersand}", "&").replace("&amp;", "&")

}