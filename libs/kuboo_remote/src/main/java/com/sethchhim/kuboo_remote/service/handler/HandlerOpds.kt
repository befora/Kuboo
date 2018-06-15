package com.sethchhim.kuboo_remote.service.handler

import android.text.Html.fromHtml
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.OpdsEntity
import com.sethchhim.kuboo_remote.util.Settings.isDebugOpdsHandler
import org.apache.commons.lang3.StringEscapeUtils
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import timber.log.Timber

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
        if (isDebugOpdsHandler) Timber.d("Start Element: $qName")
        if (qName.equals("ENTRY", ignoreCase = true)) {
            entry = true
        }
        if (qName.equals("ID", ignoreCase = true)) {
            id = true
        }
        if (qName.equals("NAME", ignoreCase = true)) {
            author = true
        }
        if (qName.equals("TITLE", ignoreCase = true)) {
            title = true
        }
        if (qName.equals("CONTENT", ignoreCase = true)) {
            if (attributes.getValue("type") != null && !attributes.getValue("type").isEmpty()) {
                if (attributes.getValue("type") == "html") {
                    content = true
                }
            }
        }
        if (qName.equals("LINK", ignoreCase = true)) {
            if (attributes.getValue("rel") != null && !attributes.getValue("rel").isEmpty()) {
                if (attributes.getValue("rel").equals("http://opds-spec.org/acquisition", ignoreCase = true)) {
                    entity.LinkAcquisition = attributes.getValue("href")
                    if (entity.LinkAcquisition.contains("/opds-books/")) {
                        val bits = entity.LinkAcquisition.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkAcquisition = bits[bits.size - 1]
                    } else if (entity.LinkAcquisition.contains("/opds-comics/")) {
                        val bits = entity.LinkAcquisition.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkAcquisition = bits[bits.size - 1]
                    }
//                    if (entity.LinkAcquisition.contains("{ampersand}")) entity.LinkAcquisition = entity.LinkAcquisition.replace("{ampersand}", "&")
//                    entity.LinkAcquisition = StringEscapeUtils.unescapeXml(entity.LinkAcquisition)
                    linkAcquisition = true
                } else if (attributes.getValue("rel").equals("subsection", ignoreCase = true)) {
                    entity.LinkSubsection = attributes.getValue("href")
                    if (entity.LinkSubsection.contains("/opds-books/")) {
                        val bits = entity.LinkSubsection.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkSubsection = bits[bits.size - 1]
                    } else if (entity.LinkSubsection.contains("/opds-comics/")) {
                        val bits = entity.LinkSubsection.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkSubsection = bits[bits.size - 1]
                    }
//                    if (entity.LinkSubsection.contains("{ampersand}")) entity.LinkSubsection = entity.LinkSubsection.replace("{ampersand}", "&")
//                    entity.LinkSubsection = StringEscapeUtils.unescapeXml(entity.LinkSubsection)
                    linkSubsection = true
                } else if (attributes.getValue("rel").equals("http://opds-spec.org/image/thumbnail", ignoreCase = true)) {
                    entity.LinkThumbnail = attributes.getValue("href")
                    if (entity.LinkThumbnail.contains("/opds-books/")) {
                        val bits = entity.LinkThumbnail.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkThumbnail = bits[bits.size - 1]
                    } else if (entity.LinkThumbnail.contains("/opds-comics/")) {
                        val bits = entity.LinkThumbnail.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkThumbnail = bits[bits.size - 1]
                    }
//                    if (entity.LinkThumbnail.contains("{ampersand}")) entity.LinkThumbnail = entity.LinkThumbnail.replace("{ampersand}", "&")
//                    entity.LinkThumbnail = StringEscapeUtils.unescapeXml(entity.LinkThumbnail)
                    linkThumbnail = true
                } else if (attributes.getValue("rel").equals("self", ignoreCase = true)) {
                    entity.LinkXmlPath = attributes.getValue("href")
                    if (entity.LinkXmlPath.contains("/opds-books/")) {
                        try {
                            val bits = entity.LinkXmlPath.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            entity.LinkXmlPath = bits[bits.size - 1]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            entity.LinkXmlPath = entity.LinkXmlPath.replace("/opds-books/", "")
                        }

                    } else if (entity.LinkXmlPath.contains("/opds-comics/")) {
                        try {
                            val bits = entity.LinkXmlPath.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            entity.LinkXmlPath = bits[bits.size - 1]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            entity.LinkXmlPath = entity.LinkXmlPath.replace("/opds-comics/", "")
                        }

                    }
//                    if (entity.LinkXmlPath.contains("{ampersand}")) entity.LinkXmlPath = entity.LinkXmlPath.replace("{ampersand}", "&")
//                    entity.LinkXmlPath = StringEscapeUtils.unescapeXml(entity.LinkXmlPath)
                    linkXMLPath = true
                } else if (attributes.getValue("rel").equals("previous", ignoreCase = true)) {
                    entity.LinkPrevious = attributes.getValue("href")
                    if (entity.LinkPrevious.contains("/opds-books/")) {
                        try {
                            val bits = entity.LinkPrevious.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            entity.LinkPrevious = bits[bits.size - 1]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            entity.LinkPrevious = entity.LinkPrevious.replace("/opds-books/", "")
                        }

                    } else if (entity.LinkPrevious.contains("/opds-comics/")) {
                        try {
                            val bits = entity.LinkPrevious.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            entity.LinkPrevious = bits[bits.size - 1]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            entity.LinkPrevious = entity.LinkPrevious.replace("/opds-comics/", "")
                        }

                    }
//                    if (entity.LinkPrevious.contains("{ampersand}")) entity.LinkPrevious = entity.LinkPrevious.replace("{ampersand}", "&")
//                    entity.LinkPrevious = StringEscapeUtils.unescapeXml(entity.LinkPrevious)
                    linkPrevious = true
                } else if (attributes.getValue("rel").equals("next", ignoreCase = true)) {
                    entity.LinkNext = attributes.getValue("href")
                    if (entity.LinkNext.contains("/opds-books/")) {
                        try {
                            val bits = entity.LinkNext.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            entity.LinkNext = bits[bits.size - 1]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            entity.LinkNext = entity.LinkNext.replace("/opds-books/", "")
                        }

                    } else if (entity.LinkNext.contains("/opds-comics/")) {
                        try {
                            val bits = entity.LinkNext.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            entity.LinkNext = bits[bits.size - 1]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            entity.LinkNext = entity.LinkNext.replace("/opds-comics/", "")
                        }

                    }
//                    if (entity.LinkNext.contains("{ampersand}")) entity.LinkNext = entity.LinkNext.replace("{ampersand}", "&")
//                    entity.LinkNext = StringEscapeUtils.unescapeXml(entity.LinkNext)
                    linkNext = true
                } else if (attributes.getValue("rel").equals("http://vaemendis.net/opds-pse/stream", ignoreCase = true)) {
                    entity.LinkPse = attributes.getValue("href")
                    if (entity.LinkPse.contains("/opds-books/")) {
                        val bits1 = entity.LinkPse.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkPse = bits1[bits1.size - 1]
                        linkPse = true
                        val stringTotalPages = attributes.getValue("pse:count")
                        val bits2 = stringTotalPages.split("/opds-books/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.TotalPages = bits2[bits2.size - 1]
                        totalPages = true
                    } else if (entity.LinkPse.contains("/opds-comics/")) {
                        val bits1 = entity.LinkPse.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.LinkPse = bits1[bits1.size - 1]
                        linkPse = true
                        val stringTotalPages = attributes.getValue("pse:count")
                        val bits2 = stringTotalPages.split("/opds-comics/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        entity.TotalPages = bits2[bits2.size - 1]
                        totalPages = true
                    }
//                    if (entity.LinkPse.contains("{ampersand}")) entity.LinkPse = entity.LinkPse.replace("{ampersand}", "&")
                    entity.LinkPse = StringEscapeUtils.escapeXml10(entity.LinkPse)
//                    if (entity.TotalPages.contains("{ampersand}")) entity.TotalPages = entity.LinkNext.replace("{ampersand}", "&")
//                    entity.TotalPages = StringEscapeUtils.unescapeXml(entity.TotalPages)
                }
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (isDebugOpdsHandler) Timber.d("End Element: $qName")
        if (qName.equals("ENTRY", ignoreCase = true)) {
            saxList.add(entity.toBook())
            entry = false
            if (saxList.size > maxResults) throw SAXException("Results greater than maxResults: $maxResults")
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (id) {
            entity.Id = String(ch, start, length)
            if (isDebugOpdsHandler) Timber.d("Found id: ${entity.Id}")
            id = false
        }
        if (title) {
            entity.Title = String(ch, start, length)
//            if (entity.Title.contains("{ampersand}")) entity.Title = entity.Title.replace("{ampersand}", "&")
//            entity.Title = StringEscapeUtils.unescapeXml(entity.Title)
            entity.Title = stripHtml(entity.Title)
            if (isDebugOpdsHandler) Timber.d("Found title: ${entity.Title}")
            title = false
        }
        if (author) {
            entity.Author = String(ch, start, length)
//            if (entity.Author.contains("{ampersand}")) entity.Author = entity.Author.replace("{ampersand}", "&")
//            entity.Author = StringEscapeUtils.unescapeXml(entity.Author)
            entity.Author = stripHtml(entity.Author)
            if (isDebugOpdsHandler) Timber.d("Found author: ${entity.Author}")
            author = false
        }
        if (content) {
            entity.Content = String(ch, start, length)
//            if (entity.Content.contains("{ampersand}")) entity.Content = entity.Content.replace("{ampersand}", "&")
//            entity.Content = StringEscapeUtils.unescapeXml(entity.Content)
            entity.Content = stripHtml(entity.Content)
            if (isDebugOpdsHandler) Timber.d("Found content: ${entity.Content}")
            content = false
        }
        if (linkAcquisition) {
            if (isDebugOpdsHandler) Timber.d("Found linkAcquisition: ${entity.LinkAcquisition}")
            linkAcquisition = false
        }
        if (linkSubsection) {
            if (isDebugOpdsHandler) Timber.d("Found linkSubsection: ${entity.LinkSubsection}")
            linkSubsection = false
        }
        if (linkThumbnail) {
            if (isDebugOpdsHandler) Timber.d("Found linkThumbnail: ${entity.LinkThumbnail}")
            linkThumbnail = false
        }
        if (linkXMLPath) {
            if (isDebugOpdsHandler) Timber.d("Found linkXmlPath: ${entity.LinkXmlPath}")
            linkXMLPath = false
        }
        if (linkPrevious) {
            if (isDebugOpdsHandler) Timber.d("Found linkPrevious: ${entity.LinkPrevious}")
            linkPrevious = false
        }
        if (linkNext) {
            if (isDebugOpdsHandler) Timber.d("Found linkNext: ${entity.LinkNext}")
            linkNext = false
        }
        if (linkPse) {
            if (isDebugOpdsHandler) Timber.d("Found linkPse: ${entity.LinkPse}")
            linkPse = false
        }
        if (totalPages) {
            if (isDebugOpdsHandler) Timber.d("Found totalPages: ${entity.TotalPages}")
            totalPages = false
        }
    }

    private fun stripHtml(html: String) =
            fromHtml(html).toString().replace("\n".toRegex(), "").trim { it <= ' ' }

    private fun String.unescapeXml() = StringEscapeUtils.unescapeXml(this)

    private fun String.unescapeAmpersand() = replace("{ampersand}", "&")

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
            title = Title.unescapeAmpersand().unescapeXml()
            author = Author.unescapeAmpersand().unescapeXml()
            content = Content.unescapeAmpersand().unescapeXml()
            content = getFormattedContent()
            linkAcquisition = LinkAcquisition.unescapeAmpersand().unescapeXml()
            linkSubsection = LinkSubsection.unescapeAmpersand().unescapeXml()
            linkThumbnail = LinkThumbnail.unescapeAmpersand().unescapeXml()
            linkXmlPath = LinkXmlPath.unescapeAmpersand().unescapeXml()
            linkPrevious = LinkPrevious.unescapeAmpersand().unescapeXml()
            linkNext = LinkNext.unescapeAmpersand().unescapeXml()
            linkPse = LinkPse.unescapeAmpersand().unescapeXml()
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

}