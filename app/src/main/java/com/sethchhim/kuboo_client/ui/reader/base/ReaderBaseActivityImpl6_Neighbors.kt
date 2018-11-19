package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_remote.model.Neighbors
import timber.log.Timber

@SuppressLint("Registered")
open class ReaderBaseActivityImpl6_Neighbors : ReaderBaseActivityImpl5_Bookmark() {

    protected fun populateNeighbors() = when (isDownload()) {
        true -> populateNeighborsDownload()
        false -> populateNeighborsRemote()
    }

    private fun populateNeighborsDownload() {
        viewModel.getNeighborsDownload(currentBook).observe(this, Observer {
            it?.let { handlePopulateNeighborResult(it) }
        })
    }

    private fun populateNeighborsRemote() {
        val stringUrl = viewModel.getActiveServer() + currentBook.linkXmlPath
        viewModel.getNeighborsRemote(currentBook, stringUrl).observe(this, Observer { result ->
            when (result != null) {
                true -> onPopulateNeighborsRemoteSuccess(result)
                false -> onPopulateNeighborsFail()
            }
        })
    }

    private fun populateNeighborsRemoteAtNextPage() {
        val stringUrl = viewModel.getActiveServer() + currentBook.linkNext
        viewModel.getNeighborsNextPageRemote(currentBook, stringUrl).observe(this, Observer { result ->
            when (result != null) {
                true -> handlePopulateNeighborResult(result)
                false -> onPopulateNeighborsFail()
            }
        })
    }

    private fun handlePopulateNeighborResult(result: Neighbors) {
        result.previousBook?.let { previousBook = it }
        result.nextBook?.let { nextBook = it }

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
        val isNextBookEmpty = result.nextBook?.isEmpty() ?: true
        val isCurrentBookContainsLinkNext = currentBook.linkNext.isNotEmpty()
        when (isNextBookEmpty && isCurrentBookContainsLinkNext) {
            true -> populateNeighborsRemoteAtNextPage()
            false -> handlePopulateNeighborResult(result)
        }
    }

    private fun preloadLocal() {
        //no action required
    }

    private fun preloadRemote() = glideUtil.apply {
        if (nextBook.isComic()) {
            //preload next preview image
            preload(this@ReaderBaseActivityImpl6_Neighbors, nextBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT))

            //preload next first image
            preload(this@ReaderBaseActivityImpl6_Neighbors, nextBook.server + nextBook.getPse(Settings.MAX_PAGE_WIDTH, 0))
        }
    }

}