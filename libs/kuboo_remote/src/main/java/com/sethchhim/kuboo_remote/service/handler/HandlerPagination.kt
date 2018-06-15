package com.sethchhim.kuboo_remote.service.handler

import android.text.Html
import com.sethchhim.kuboo_remote.model.OpdsEntity
import com.sethchhim.kuboo_remote.model.Pagination
import com.sethchhim.kuboo_remote.util.Settings.isDebugPaginationHandler
import org.apache.commons.text.StringEscapeUtils
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
                        entity.LinkPrevious = attributes.getValue("href")

                        if (entity.LinkPrevious.contains("/opds-books/")) {
                            try {
                                val bits = entity.LinkPrevious.split("/opds-books/")
                                entity.LinkPrevious = bits[bits.size - 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                entity.LinkPrevious = entity.LinkPrevious.replace("/opds-books/", "")
                            }

                        } else if (entity.LinkPrevious.contains("/opds-comics/")) {
                            try {
                                val bits = entity.LinkPrevious.split("/opds-comics/")
                                entity.LinkPrevious = bits[bits.size - 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                entity.LinkPrevious = entity.LinkPrevious.replace("/opds-comics/", "")
                            }

                        }

                        linkPrevious = true
                    }
                    attributes.getValue("rel").equals("self", ignoreCase = true) -> {
                        entity.LinkSelf = attributes.getValue("href")
                        if (entity.LinkSelf.contains("/opds-books/")) {
                            try {
                                val bits = entity.LinkSelf.split("/opds-books/")
                                entity.LinkSelf = bits[bits.size - 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                entity.LinkSelf = entity.LinkSelf.replace("/opds-books/", "")
                            }
                        } else if (entity.LinkSelf.contains("/opds-comics/")) {
                            try {
                                val bits = entity.LinkSelf.split("/opds-comics/")
                                entity.LinkSelf = bits[bits.size - 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                entity.LinkSelf = entity.LinkSelf.replace("/opds-comics/", "")
                            }
                        }
                        linkSelf = true
                    }
                    attributes.getValue("rel").equals("next", ignoreCase = true) -> {
                        entity.LinkNext = attributes.getValue("href")
                        if (entity.LinkNext.contains("/opds-books/")) {
                            try {
                                val bits = entity.LinkNext.split("/opds-books/")
                                entity.LinkNext = bits[bits.size - 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                entity.LinkNext = entity.LinkNext.replace("/opds-books/", "")
                            }

                        } else if (entity.LinkNext.contains("/opds-comics/")) {
                            try {
                                val bits = entity.LinkNext.split("/opds-comics/")
                                entity.LinkNext = bits[bits.size - 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                entity.LinkNext = entity.LinkNext.replace("/opds-comics/", "")
                            }
                        }
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
                pagination.previous = entity.LinkPrevious

                if (pagination.previous.contains("{ampersand}")) {
                    pagination.previous = pagination.previous.replace("{ampersand}", "&")
                    pagination.previous = StringEscapeUtils.unescapeXml(pagination.previous)
                }
                pagination.previous = stripHtml(pagination.previous)

                if (isDebugPaginationHandler) Timber.d("Found content: $entity.Content")

                linkPrevious = false
            }
            if (linkSelf) {
                pagination.self = entity.LinkSelf

                if (pagination.self.contains("{ampersand}")) {
                    pagination.self = pagination.self.replace("{ampersand}", "&")
                    pagination.self = StringEscapeUtils.unescapeXml(pagination.self)
                }
                pagination.self = stripHtml(pagination.self)

                linkSelf = false
            }
            if (linkNext) {
                pagination.next = entity.LinkNext

                if (pagination.next.contains("{ampersand}")) {
                    pagination.next = pagination.next.replace("{ampersand}", "&")
                    pagination.next = StringEscapeUtils.unescapeXml(pagination.next)
                }
                pagination.next = stripHtml(pagination.next)

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

    private fun stripHtml(html: String): String =
            Html.fromHtml(html).toString().replace("\n".toRegex(), "").trim { it <= ' ' }

}