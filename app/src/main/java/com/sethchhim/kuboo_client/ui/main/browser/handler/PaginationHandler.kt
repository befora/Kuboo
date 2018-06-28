package com.sethchhim.kuboo_client.ui.main.browser.handler

import android.view.View
import android.widget.Button
import com.sethchhim.kuboo_client.Constants.URL_PATH_FOLDER_INDEX
import com.sethchhim.kuboo_client.Constants.URL_PATH_GRID_INDEX
import com.sethchhim.kuboo_client.Extensions.fadeInvisible
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.ui.main.browser.BrowserBaseFragmentImpl1_Content
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Pagination
import kotlinx.android.synthetic.main.browser_layout_content.view.*
import kotlinx.android.synthetic.main.browser_layout_pagination.view.*

class PaginationHandler(private val browserFragment: BrowserBaseFragmentImpl1_Content, val view: View) {

    private val contentRecyclerView = view.browser_layout_content_browserContentRecyclerView
    private val nextButton = view.browser_layout_pagination_button2
    private val previousButton = view.browser_layout_pagination_button1

    private lateinit var next: String
    private lateinit var previous: String
    private lateinit var self: String
    private var size = 0

    internal fun process(result: Pagination, book: Book, list: List<Book>) {
        previous = result.previous
        self = result.self
        next = result.next
        size = list.size

        setPaginationLayout()
        setPaginationButton(previousButton, previous, book)
        setPaginationButton(nextButton, next, book)
    }

    internal fun reset() {
        view.browser_layout_pagination_linearLayout2.invisible()
        setButtonDisabled(nextButton)
        setButtonDisabled(previousButton)
    }

    private fun setPaginationLayout() {
        val nextIndex = getPaginationIndex(next)
        val previousIndex = getPaginationIndex(previous)
        val selfIndex = getPaginationIndex(self)

        when (selfIndex.isEmpty() && next.isEmpty()) {
            true -> view.browser_layout_pagination_linearLayout2.fadeInvisible()
            false -> view.browser_layout_pagination_linearLayout2.fadeVisible()
        }

        view.browser_layout_pagination_textView1.text =
                try {
                    when (selfIndex.isEmpty()) {
                        true -> when (previousIndex.isEmpty()) {
                            true -> "1"
                            false -> (previousIndex.toInt() + 1).toString()
                        }
                        false -> when (selfIndex == "0") {
                            true -> "1"
                            false -> (selfIndex.toInt() + 1).toString()
                        }
                    }
                } catch (e: NumberFormatException) {
                    selfIndex
                }


        view.browser_layout_pagination_textView3.text =
                try {
                    val isNextValid = next.contains(URL_PATH_FOLDER_INDEX) || next.contains(URL_PATH_GRID_INDEX)
                    when (isNextValid) {
                        true -> nextIndex
                        false -> (selfIndex.toInt() + size).toString()

                    }
                } catch (e: NumberFormatException) {
                    size.toString()
                }

    }

    private fun getPaginationIndex(string: String): String {
        if (string.contains(URL_PATH_FOLDER_INDEX)) {
            val startIndex = string.indexOf("=") + 1
            val endIndex = string.indexOf("&")
            return string.substring(startIndex, endIndex)
        } else if (string.contains(URL_PATH_GRID_INDEX)) {
            val startIndex = string.lastIndexOf("=") + 1
            val endIndex = string.length
            return string.substring(startIndex, endIndex)
        }
        return ""
    }

    private fun setPaginationButton(button: Button, stringUrl: String, book: Book) {
        when (stringUrl.isEmpty()) {
            true -> setButtonDisabled(button)
            false -> setButtonEnabled(button, stringUrl, book)
        }
    }

    private fun setButtonEnabled(button: Button, pageUrl: String, book: Book) {
        button.alpha = 1f
        button.isClickable = true

        button.setOnClickListener {
            val paginationBook = book.apply { linkSubsection = pageUrl }
            browserFragment.populatePaginationContent(paginationBook)
            contentRecyclerView.scrollToPosition(0)
        }
    }

    private fun setButtonDisabled(button: Button) {
        button.alpha = 0.2f
        button.isClickable = false

        button.setOnClickListener(null)
    }

}