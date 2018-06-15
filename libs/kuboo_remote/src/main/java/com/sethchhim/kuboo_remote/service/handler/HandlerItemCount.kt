package com.sethchhim.kuboo_remote.service.handler

import com.sethchhim.kuboo_remote.model.OpdsEntity
import com.sethchhim.kuboo_remote.util.Settings.isDebugItemCountHandler
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import timber.log.Timber

class HandlerItemCount(private val itemCountList: MutableList<String>) : DefaultHandler() {

    private val mEntity = OpdsEntity()
    private var itemCount = false

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String?, attributes: Attributes) {
        if (isDebugItemCountHandler) Timber.d("Start Element: $qName")
        if (qName != null) {
            if (qName.equals("TITLE", ignoreCase = true)) {
                itemCount = true
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (isDebugItemCountHandler) Timber.d("End Element: $qName")

        if (qName.equals("TITLE", ignoreCase = true)) {
            if (itemCount) {
                var result = mEntity.ItemCount
                if (result.contains("Comics - ")) {
                    val startIndex = 9
                    val endIndex = result.indexOf(" items")
                    result = result.substring(startIndex, endIndex)
                } else if (result.contains("Books - ")) {
                    val startIndex = 8
                    val endIndex = result.indexOf(" items")
                    result = result.substring(startIndex, endIndex)
                }
                itemCountList.add(result)
                throw SAXException()
            }
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (itemCount) {
            mEntity.ItemCount = String(ch, start, length)
            if (isDebugItemCountHandler) Timber.d("Found item count: ${mEntity.ItemCount}")
        }
    }

}