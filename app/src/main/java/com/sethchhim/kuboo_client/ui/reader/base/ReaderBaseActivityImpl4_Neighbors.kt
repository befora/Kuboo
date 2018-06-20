package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Neighbors
import timber.log.Timber

/**
 * Created by Seth Chhim on 12/30/2017.
 * Project: kuboo
 * Contact: sethchhim@gmail.com
 */

@SuppressLint("Registered")
open class ReaderBaseActivityImpl4_Neighbors : ReaderBaseActivityImpl3_Bookmark() {

    protected fun populateNeighbors() = when (isLocal) {
        true -> populateNeighborsLocal()
        false -> populateNeighborsRemote()
    }

    private fun handlePopulateNeighborResult(result: Neighbors) {
        previousBook = result.previousBook ?: Book()
        nextBook = result.nextBook ?: Book()

        if (!nextBook.isEmpty()) {
            when (isLocal) {
                true -> preloadLocal()
                false -> preloadRemote()
            }
        }
    }

    private fun onPopulateNeighborsFail() {
        Timber.e("onPopulateNeighborsFail")
    }

    private fun onPopulateNeighborsRemoteSuccess(result: Neighbors) {
        val isNextBookEmpty = result.nextBook?.isEmpty() == true
        val isCurrentBookContainsLinkNext = currentBook.linkNext.isNotEmpty()
        if (isNextBookEmpty && isCurrentBookContainsLinkNext) {
            populateNeighborsRemoteAtNextPage()
        } else {
            handlePopulateNeighborResult(result)
        }
    }

    private fun populateNeighborsLocal() {
        viewModel.getNeighborsLocal(currentBook).observe(this, Observer { result ->
            when (result != null) {
                true -> handlePopulateNeighborResult(result!!)
                false -> onPopulateNeighborsFail()
            }
        })
    }

    private fun populateNeighborsRemote() {
        val stringUrl = viewModel.getActiveServer() + currentBook.linkXmlPath
        viewModel.getNeighborsRemote(currentBook, stringUrl).observe(this, Observer { result ->
            when (result != null) {
                true -> onPopulateNeighborsRemoteSuccess(result!!)
                false -> onPopulateNeighborsFail()
            }
        })
    }

    private fun populateNeighborsRemoteAtNextPage() {
        val stringUrl = viewModel.getActiveServer() + currentBook.linkNext
        viewModel.getNeighborsRemote(currentBook, stringUrl).observe(this, Observer { result ->
            if (result != null) handlePopulateNeighborResult(result)
        })
    }

    private fun preloadLocal() {
        //no action required
    }

    private fun preloadRemote() = glideUtil.apply {
        if (nextBook.isComic()) {
            //preload next preview image
            preload(this@ReaderBaseActivityImpl4_Neighbors, nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT))

            //preload next first image
            preload(this@ReaderBaseActivityImpl4_Neighbors, nextBook.server + nextBook.getPse(Settings.MAX_PAGE_WIDTH, 0))
        }
    }

}