package com.sethchhim.kuboo_client.ui.main.browser

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Constants.ARG_BOOK
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_remote.model.Book

class BrowserSeriesFragment : BrowserBaseFragment() {

    private lateinit var seriesBook: Book

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentSwipeRefreshLayout.setOnRefreshListener { onSwipeRefresh() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        seriesBook = arguments?.getParcelable(ARG_BOOK) as Book
        pathHorizontalScrollView.gone()
        populateSeries()
    }

    override fun onSwipeRefresh() {
        super.onSwipeRefresh()
        populateSeries()
    }

    private fun populateSeries() {
        setStateLoading()
        resetRecyclerView()
        paginationHandler.reset()
        val stringUrl = viewModel.getActiveServer() + seriesBook.linkXmlPath
        viewModel.getListByUrl(stringUrl).observe(this, Observer { result ->
            val book = Book().apply { linkSubsection = seriesBook.linkXmlPath }
            handleMediaResult(book, result)
        })
    }

    companion object {
        fun newInstance(book: Book) = BrowserSeriesFragment().apply {
            arguments = Bundle().apply { putParcelable(ARG_BOOK, book) }
        }
    }

}