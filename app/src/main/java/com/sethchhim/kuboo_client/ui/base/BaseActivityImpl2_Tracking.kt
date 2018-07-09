package com.sethchhim.kuboo_client.ui.base

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber

@SuppressLint("Registered")
open class BaseActivityImpl2_Tracking : BaseActivityImpl1_Read() {

    private lateinit var startSeriesBook: Book

    internal fun startTrackingService() = viewModel.getDownloadListFavoriteCompressed()
            .filter { it.isFavorite }
            .forEach {
                viewModel.deleteDownloadsBefore(it)
                startTrackingByBook(it)
            }

    internal fun startTrackingByBook(book: Book) {
        startSeriesBook = book
        if (viewModel.getActiveServer() == startSeriesBook.server) {
            val startTime = System.currentTimeMillis()
            viewModel.getSeriesNeighborsRemote(book, startSeriesBook.server + startSeriesBook.linkXmlPath, Settings.DOWNLOAD_TRACKING_LIMIT).observe(this, Observer {
                it?.let { result ->
                    val mutableResult = result.apply { forEach { it.isFavorite = true } }.toMutableList()
                    handleResult(mutableResult, startSeriesBook.linkNext, startTime)
                }
            })
        }
    }

    internal fun stopTrackingByBook(book: Book) = viewModel.getDownloadList()
            .filter { it.getXmlId() == book.getXmlId() }
            .sortedBy { it.id }
            .filterIndexed { i, _ -> i != 0 }
            .forEach {
                viewModel.getFetchDownload(it).observe(this, Observer {
                    it?.let { viewModel.deleteFetchDownload(it) }
                })
            }

    private fun handleResult(seriesNeighbors: MutableList<Book>, linkNext: String, startTime: Long) {
        val isRequireNextPage = seriesNeighbors.size < Settings.DOWNLOAD_TRACKING_LIMIT && linkNext.isNotEmpty()
        when (isRequireNextPage) {
            true -> getRemainingSeriesNeighbors(seriesNeighbors, linkNext, startTime)
            false -> handleResultFinal(seriesNeighbors, startTime)
        }
    }

    private fun getRemainingSeriesNeighbors(seriesNeighbors: MutableList<Book>, linkNext: String, startTime: Long) {
        val remainingCount = Settings.DOWNLOAD_TRACKING_LIMIT - seriesNeighbors.size
        viewModel.getSeriesNeighborsNextPageRemote(startSeriesBook.server + linkNext, seriesLimit = remainingCount).observe(this, Observer {
            it?.let { result ->
                result.forEach { it.isFavorite = true }
                seriesNeighbors.addAll(result)
                val firstItem = try {
                    result[0]
                } catch (e: IndexOutOfBoundsException) {
                    startSeriesBook
                }
                handleResult(seriesNeighbors, firstItem.linkNext, startTime)
            }
        })
    }

    private fun handleResultFinal(seriesNeighbors: MutableList<Book>, startTime: Long) {
        if (seriesNeighbors.isNotEmpty()) viewModel.startDownloads(seriesNeighbors, Settings.DOWNLOAD_SAVE_PATH)
        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Tracking service finished [$elapsedTime ms]")
    }

}