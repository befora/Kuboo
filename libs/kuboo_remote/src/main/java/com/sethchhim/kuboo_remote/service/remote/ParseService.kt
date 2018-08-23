package com.sethchhim.kuboo_remote.service.remote

import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Neighbors
import com.sethchhim.kuboo_remote.model.Pagination
import com.sethchhim.kuboo_remote.service.handler.HandlerItemCount
import com.sethchhim.kuboo_remote.service.handler.HandlerOpds
import com.sethchhim.kuboo_remote.service.handler.HandlerPagination
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import timber.log.Timber
import java.io.StringReader
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class ParseService {

    internal fun parseNeighbor(login: Login, book: Book, stringXml: String, maxResults: Int = Int.MAX_VALUE): Neighbors {
        val neighbors = Neighbors().apply { currentBook = book }
        try {
            val saxList = mutableListOf<Book>()
            getSaxParser().parse(getInputSource(stringXml), HandlerOpds(login, saxList, maxResults))
            saxList.searchForNeighbors(neighbors)
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return neighbors
    }

    internal fun parseNeighborNextPage(login: Login, book: Book, stringXml: String, maxResults: Int = Int.MAX_VALUE): Neighbors {
        val neighbors = Neighbors().apply { currentBook = book }
        try {
            val saxList = mutableListOf<Book>()
            getSaxParser().parse(getInputSource(stringXml), HandlerOpds(login, saxList, maxResults))
            saxList.searchForNeighborsNextPage(neighbors)
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return neighbors
    }


    internal fun parseSeriesNeighbors(login: Login, book: Book, stringXml: String, seriesLimit: Int, maxResults: Int = Int.MAX_VALUE): MutableList<Book> {
        val neighbors = mutableListOf<Book>()
        try {
            val saxList = mutableListOf<Book>()
            getSaxParser().parse(getInputSource(stringXml), HandlerOpds(login, saxList, maxResults))
            saxList.searchForSeriesNeighbors(book, neighbors, seriesLimit)
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return neighbors
    }

    internal fun parseSeriesNeighborsNextPage(login: Login, stringXml: String, seriesLimit: Int, maxResults: Int = Int.MAX_VALUE): MutableList<Book> {
        val neighbors = mutableListOf<Book>()
        try {
            val saxList = mutableListOf<Book>()
            getSaxParser().parse(getInputSource(stringXml), HandlerOpds(login, saxList, maxResults))
            saxList.searchForSeriesNeighborsNextPage(neighbors, seriesLimit)
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return neighbors
    }

    internal fun parseOpds(login: Login, stringXml: String, maxResults: Int = Int.MAX_VALUE): MutableList<Book> {
        val saxList = mutableListOf<Book>()
        try {
            getSaxParser().parse(getInputSource(stringXml), HandlerOpds(login, saxList, maxResults))
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saxList
    }

    internal fun parsePagination(stringXml: String): Pagination {
        val pagination = Pagination()
        try {
            getSaxParser().parse(getInputSource(stringXml), HandlerPagination(pagination))
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pagination
    }

    internal fun parseItemCount(stringXml: String): String {
        val itemCount = mutableListOf<String>()
        try {
            getSaxParser().parse(getInputSource(stringXml), HandlerItemCount(itemCount))
        } catch (e: SAXException) {
//            Timber.i(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            return itemCount[0]
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        return "0"

    }

    private fun getInputSource(stringXml: String): InputSource {
        val formattedStringXml = stringXml.removeNewLines().escapeAmpersand()
        return InputSource(StringReader(formattedStringXml))
    }

    private fun getSaxParser(): SAXParser {
        val saxParserFactory = SAXParserFactory.newInstance()
        return saxParserFactory.newSAXParser()
    }

    private fun String.removeNewLines() = replace("\n", "").replace("\r", "")

    private fun String.escapeAmpersand() = replace("&", "{ampersand}")

    private fun MutableList<Book>.searchForNeighbors(neighbors: Neighbors): Neighbors {
        var position = -2
        forEachIndexed { index, it ->
            if (it.id == neighbors.currentBook?.id) {
                position = index
                return@forEachIndexed
            }
        }

        try {
            val previousPosition = position - 1
            neighbors.previousBook = this[previousPosition]
            Timber.i("Found previousBook! position[$previousPosition] title[${neighbors.previousBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
        }

        try {
            val nextPosition = position + 1
            neighbors.nextBook = this[nextPosition]
            Timber.i("Found nextBook! position[$nextPosition] title[${neighbors.nextBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
        }

        return neighbors
    }

    private fun MutableList<Book>.searchForNeighborsNextPage(neighbors: Neighbors): Neighbors {
        val position = 0

        try {
            neighbors.nextBook = this[position]
            Timber.i("Found nextBook! position[$position] title[${neighbors.nextBook?.title}]")
        } catch (e: IndexOutOfBoundsException) {
        }

        return neighbors
    }

    private fun MutableList<Book>.searchForSeriesNeighbors(currentBook: Book, seriesNeighborsList: MutableList<Book>, seriesLimit: Int): MutableList<Book> {
        var position = -2

        forEachIndexed { index, it ->
            if (it.id == currentBook.id) {
                position = index
                return@forEachIndexed
            }
        }

        for (index in position + 1..position + seriesLimit) {
            try {
                seriesNeighborsList.add(this[index])
            } catch (e: IndexOutOfBoundsException) {
            }
        }

        return seriesNeighborsList
    }

    private fun MutableList<Book>.searchForSeriesNeighborsNextPage(seriesNeighborsList: MutableList<Book>, seriesLimit: Int): MutableList<Book> {
        val position = -1

        for (index in position + 1..position + seriesLimit) {
            try {
                seriesNeighborsList.add(this[index])
            } catch (e: IndexOutOfBoundsException) {
            }
        }

        return seriesNeighborsList
    }
}

