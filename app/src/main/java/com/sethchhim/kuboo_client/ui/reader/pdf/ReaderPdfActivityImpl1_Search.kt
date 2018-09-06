package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint

@SuppressLint("Registered")
open class ReaderPdfActivityImpl1_Search: ReaderPdfActivityImpl0_View(){

    protected var searchHitPage: Int = 0
    private var searchNeedle: String? = null
    private var stopSearch: Boolean = false

    protected fun search(direction: Int) {
        val startPage = when (searchHitPage == currentPage) {
            true -> currentPage + direction
            false -> currentPage
        }
        searchHitPage = -1
        if (searchNeedle!!.isEmpty())
            searchNeedle = null
        if (searchNeedle != null)
            if (startPage in 0..(viewModel.getPdfPageCount() - 1))
                runSearch(startPage, direction, searchNeedle!!)
    }

    protected fun resetSearch() {
        stopSearch = true
        searchHitPage = -1
        searchNeedle = null
    }

    private fun runSearch(startPage: Int, direction: Int, needle: String) {
//        stopSearch = false
//        var searchPage = startPage
//        if (stopSearch || needle != searchNeedle)
//            return
//        for (i in 0..8) {
//            val currentPage = document.loadPage(searchPage)
//            val hits = currentPage.search(searchNeedle)
//            currentPage.destroy()
//            if (hits != null && hits.isNotEmpty()) {
//                searchHitPage = searchPage
//                break
//            }
//            searchPage += direction
//            if (searchPage < 0 || searchPage >= pageCount)
//                break
//        }
//        if (stopSearch || needle != searchNeedle) {
//        } else if (searchHitPage == currentPage) {
//            loadPage()
//        } else if (searchHitPage >= 0) {
//            currentPage = searchHitPage
//            loadPage()
//        } else {
//            if (searchPage in 0..(pageCount - 1)) {
//
//            } else {
//            }
//        }
    }

}