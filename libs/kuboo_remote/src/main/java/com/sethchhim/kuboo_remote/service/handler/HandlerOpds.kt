package com.sethchhim.kuboo_remote.service.handler

import android.os.Build
import android.text.Html
import android.text.Html.fromHtml
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.OpdsEntity
import org.apache.commons.text.StringEscapeUtils
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class HandlerOpds(private val login: Login, private val saxList: MutableList<Book>, private val maxResults: Int = Int.MAX_VALUE) : DefaultHandler() {

    private val entity = OpdsEntity()

    private var entry = false
    private var id = false
    private var title = false
    private var author = false
    private var content = false
    private var linkAcquisition = false
    private var linkSubsection = false
    private var linkThumbnail = false
    private var linkXMLPath = false
    private var linkPrevious = false
    private var linkNext = false
    private var linkPse = false
    private var totalPages = false

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (qName.equals("ENTRY", ignoreCase = true)) entry = true
        if (qName.equals("ID", ignoreCase = true)) id = true
        if (qName.equals("NAME", ignoreCase = true)) author = true
        if (qName.equals("TITLE", ignoreCase = true)) title = true
        if (qName.equals("CONTENT", ignoreCase = true)) {
            if (attributes.isValueValid("type")) {
                if (attributes.getValue("type") == "html") content = true
            }
        }
        if (qName.equals("LINK", ignoreCase = true)) {
            if (attributes.isValueValid("rel")) {
                when {
                    attributes.getValue("rel").equals("http://opds-spec.org/acquisition", ignoreCase = true) -> {
                        entity.LinkAcquisition = attributes.get("href")
                        linkAcquisition = true
                    }
                    attributes.getValue("rel").equals("subsection", ignoreCase = true) -> {
                        entity.LinkSubsection = attributes.get("href")
                        linkSubsection = true
                    }
                    attributes.getValue("rel").equals("http://opds-spec.org/image/thumbnail", ignoreCase = true) -> {
                        entity.LinkThumbnail = attributes.get("href")
                        linkThumbnail = true
                    }
                    attributes.getValue("rel").equals("self", ignoreCase = true) -> {
                        entity.LinkXmlPath = attributes.getLinkNavigation("href")
                        linkXMLPath = true
                    }
                    attributes.getValue("rel").equals("previous", ignoreCase = true) -> {
                        entity.LinkPrevious = attributes.getLinkNavigation("href")
                        linkPrevious = true
                    }
                    attributes.getValue("rel").equals("next", ignoreCase = true) -> {
                        entity.LinkNext = attributes.getLinkNavigation("href")
                        linkNext = true
                    }
                    attributes.getValue("rel").equals("http://vaemendis.net/opds-pse/stream", ignoreCase = true) -> {
                        entity.LinkPse = attributes.getLinkPse()
                        entity.LinkPse = StringEscapeUtils.escapeXml10(entity.LinkPse)
                    }
                }
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (qName.equals("ENTRY", ignoreCase = true)) {
            saxList.add(entity.toBook())
            entry = false
            if (saxList.size > maxResults) throw SAXException("Results greater than maxResults: $maxResults")
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        when {
            id -> {
                entity.Id = String(ch, start, length)
                id = false
            }
            title -> {
                entity.Title = String(ch, start, length)
                entity.Title = stripHtml(entity.Title)
                title = false
            }
            author -> {
                entity.Author = String(ch, start, length)
                entity.Author = stripHtml(entity.Author)
                author = false
            }
            content -> {
                entity.Content = String(ch, start, length)
                entity.Content = stripHtml(entity.Content)
                content = false
            }
            linkAcquisition -> linkAcquisition = false
            linkSubsection -> linkSubsection = false
            linkThumbnail -> linkThumbnail = false
            linkXMLPath -> linkXMLPath = false
            linkPrevious -> linkPrevious = false
            linkNext -> linkNext = false
            linkPse -> linkPse = false
            totalPages -> totalPages = false
        }
    }

    private fun OpdsEntity.toBook(): Book {
        val isBook = LinkAcquisition.isNotEmpty()
                && LinkAcquisition.endsWith(".epub", ignoreCase = true)
                || LinkAcquisition.endsWith(".mobi", ignoreCase = true)
        val isComic = (LinkPse.isNotEmpty()) || (LinkSubsection.isNotEmpty())

        if (isBook or isComic) {
            when {
                Id.equals("allContentFlat", ignoreCase = true) -> Id = 0.toString()
                Id.equals("allContentFolder", ignoreCase = true) -> Id = 1.toString()
                Id.equals("latestContent", ignoreCase = true) -> Id = 2.toString()
            }
        }

        return Book().apply {
            id = try {
                Integer.parseInt(Id)
            } catch (e: NumberFormatException) {
                0
            }
            title = Title.unescape()
            author = Author.unescape()
            content = Content.unescape()
            content = getFormattedContent()
            linkAcquisition = LinkAcquisition.unescape()
            linkSubsection = LinkSubsection.unescape()
            linkThumbnail = LinkThumbnail.unescape()
            linkXmlPath = LinkXmlPath.unescape()
            linkPrevious = LinkPrevious.unescape()
            linkNext = LinkNext.unescape()
            linkPse = LinkPse.unescape()
            currentPage = 0
            totalPages = try {
                Integer.parseInt(TotalPages)
            } catch (e: NumberFormatException) {
                0
            }
            server = login.server
            bookMark = ""
            setTimeAccessed()
        }
    }

    private fun Attributes.isValueValid(value: String) = getValue(value) != null && getValue(value).isNotEmpty()

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

    private fun Attributes.getLinkNavigation(value: String): String {
        var string = getValue(value)
        when {
            string.contains("/opds-books/") -> string = try {
                val bits = string.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                bits[bits.size - 1]
            } catch (e: ArrayIndexOutOfBoundsException) {
                string.replace("/opds-books/", "")
            }
            string.contains("/opds-comics/") -> string = try {
                val bits = string.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                bits[bits.size - 1]
            } catch (e: ArrayIndexOutOfBoundsException) {
                string.replace("/opds-comics/", "")
            }
            string.contains("/opds/") -> string = try {
                val bits = string.split("/opds/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                bits[bits.size - 1]
            } catch (e: ArrayIndexOutOfBoundsException) {
                string.replace("/opds/", "")
            }
        }
        return string
    }

    private fun Attributes.getLinkPse(): String {
        var string = getValue("href")
        when {
            string.contains("/opds-books/") -> {
                val bits1 = string.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                string = bits1[bits1.size - 1]
                linkPse = true
                val stringTotalPages = getValue("pse:count")
                val bits2 = stringTotalPages.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                entity.TotalPages = bits2[bits2.size - 1]
                totalPages = true
            }
            string.contains("/opds-comics/") -> {
                val bits1 = string.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                string = bits1[bits1.size - 1]
                linkPse = true
                val stringTotalPages = getValue("pse:count")
                val bits2 = stringTotalPages.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                entity.TotalPages = bits2[bits2.size - 1]
                totalPages = true
            }
            //kuboo server linkpse
            string.contains("/cache/images/?bookId=") -> {
                linkPse = true
                val stringTotalPages = getValue("pse:count")
                entity.TotalPages = stringTotalPages
                totalPages = true
            }
        }
        return string
    }

    private fun stripHtml(html: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().replace("\n".toRegex(), "").trim { it <= ' ' }
    } else {
        fromHtml(html).toString().replace("\n".toRegex(), "").trim { it <= ' ' }
    }

    private fun String.unescape() = unescapeAmpersand().unescapeXml()

    private fun String.unescapeXml() = StringEscapeUtils.unescapeXml(this)

    private fun String.unescapeAmpersand() = replace("{ampersand}", "&").replace("&amp;", "&")

}