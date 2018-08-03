package com.sethchhim.kuboo_client.ui.reader.base

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.model.Progress

@Suppress("UNCHECKED_CAST")
@SuppressLint("Registered")
open class ReaderBaseActivityImpl2_Content : ReaderBaseActivityImpl1_Overlay() {

    private val dualPaneLiveData = MutableLiveData<Boolean>()

    protected fun populateDualPaneList(): MutableLiveData<Boolean> {
        dualPaneLiveData.removeAllObservers(this)
        when (isLocal) {
            true -> viewModel.getSinglePaneList().toLocalDualPaneList()
            false -> viewModel.getSinglePaneList().toRemoteDualPaneList()
        }
        return dualPaneLiveData
    }

    protected fun populateSinglePaneList() {
        viewModel.setSinglePaneList(when (isLocal) {
            true -> viewModel.createLocalSinglePaneList(currentBook)
            false -> viewModel.createRemoteSinglePaneReaderList(currentBook)
        })
    }

    protected fun isDownload() = source == Source.DOWNLOAD

    private fun List<PageUrl>.toLocalDualPaneList() = viewModel.singleToDualLocal(this).apply {
        (this[0] as MutableLiveData<Progress>).observe(this@ReaderBaseActivityImpl2_Content, Observer { result -> result?.let { handleProgress(it) } })
        (this[1] as MutableLiveData<List<PageUrl>>).observe(this@ReaderBaseActivityImpl2_Content, Observer { result -> result?.let { handleDualPaneResult(it) } })
    }

    private fun List<PageUrl>.toRemoteDualPaneList() = viewModel.singleToDualRemote(this@ReaderBaseActivityImpl2_Content, this).apply {
        (this[0] as MutableLiveData<Progress>).observe(this@ReaderBaseActivityImpl2_Content, Observer { result -> result?.let { handleProgress(it) } })
        (this[1] as MutableLiveData<List<PageUrl>>).observe(this@ReaderBaseActivityImpl2_Content, Observer { result -> result?.let { handleDualPaneResult(it) } })
    }

    private fun handleDualPaneResult(list: List<PageUrl>) {
        viewModel.setDualPaneList(list)
        dualPaneLiveData.value = true
    }

}