package com.sethchhim.kuboo_client.util

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sethchhim.kuboo_client.data.model.Browser
import com.sethchhim.kuboo_remote.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch

@ObsoleteCoroutinesApi
class DiffUtilHelper(val adapter: BaseQuickAdapter<*, *>) {

    internal val liveData = MutableLiveData<Boolean>()

    private lateinit var oldData: List<Book>

    private val diffCallback by lazy(LazyThreadSafetyMode.NONE) { DiffCallback() }
    private val eventActor = GlobalScope.actor<List<Book>>(capacity = Channel.CONFLATED, context = GlobalScope.coroutineContext) { for (list in channel) internalUpdate(list) }

    internal fun updateBookList(oldData: List<Book>, newData: List<Book>) {
        this.oldData = oldData
        eventActor.offer(newData)
    }

    internal fun updateBrowserList(oldData: List<Browser>, newData: List<Book>) {
        val oldDataToBookList = mutableListOf<Book>()
        oldData.forEach { oldDataToBookList.add(it.book) }
        this.oldData = oldDataToBookList
        eventActor.offer(newData)
    }

    private suspend fun internalUpdate(newData: List<Book>) {
        val result = DiffUtil.calculateDiff(diffCallback.apply { newList = newData }, false)
        GlobalScope.launch(Dispatchers.Main) {
            liveData.value = true
            result.dispatchUpdatesTo(adapter)
        }.join()
    }

    private inner class DiffCallback : DiffUtil.Callback() {
        lateinit var newList: List<Book>
        override fun getOldListSize() = oldData.size
        override fun getNewListSize() = newList.size
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldData[oldItemPosition].isTheSameContentAs(newList[newItemPosition])
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldData[oldItemPosition].isTheSameItemAs(newList[newItemPosition])
    }

    private fun Book.isTheSameItemAs(book: Book): Boolean {
        return this.id == book.id
                && this.title == book.title
                && this.server == book.server
    }

    private fun Book.isTheSameContentAs(book: Book): Boolean {
        return this.currentPage == book.currentPage
                && this.bookMark == book.bookMark
    }

}