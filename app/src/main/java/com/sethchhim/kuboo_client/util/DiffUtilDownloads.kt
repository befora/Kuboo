package com.sethchhim.kuboo_client.util

import android.arch.lifecycle.MutableLiveData
import android.support.v7.util.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tonyodev.fetch2.Download
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch

@ObsoleteCoroutinesApi
class DiffUtilDownloads(val adapter: BaseQuickAdapter<*, *>) {

    internal val liveData = MutableLiveData<Boolean>()

    private lateinit var oldData: List<Download>

    private val diffCallback by lazy(LazyThreadSafetyMode.NONE) { DiffCallback() }
    private val eventActor = GlobalScope.actor<List<Download>>(capacity = Channel.CONFLATED, context = GlobalScope.coroutineContext) { for (list in channel) internalUpdate(list) }

    internal fun updateDownloadList(oldData: List<Download>, newData: List<Download>) {
        this.oldData = oldData
        eventActor.offer(newData)
    }

    private suspend fun internalUpdate(newData: List<Download>) {
        val result = DiffUtil.calculateDiff(diffCallback.apply { newList = newData }, false)
        GlobalScope.launch(Dispatchers.Main) {
            liveData.value = true
            result.dispatchUpdatesTo(adapter)
        }.join()
    }

    private inner class DiffCallback : DiffUtil.Callback() {
        lateinit var newList: List<Download>
        override fun getOldListSize() = oldData.size
        override fun getNewListSize() = newList.size
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldData[oldItemPosition].isTheSameContentAs(newList[newItemPosition])

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldData[oldItemPosition].isTheSameItemAs(newList[newItemPosition])
    }

    private fun Download.isTheSameItemAs(download: Download): Boolean {
        return this.id == download.id
    }

    private fun Download.isTheSameContentAs(download: Download): Boolean {
        return this.progress == download.progress
                && this.status == download.status
                && this.error == download.error
    }

}