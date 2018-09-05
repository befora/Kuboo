package com.sethchhim.kuboo_client.ui.reader.pdf.adapter

import android.os.Parcelable
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.ui.reader.pdf.ReaderPdfActivityImpl2_Content
import com.sethchhim.kuboo_client.ui.reader.pdf.ReaderPdfFragmentImpl1_Single
import timber.log.Timber

class ReaderPdfAdapter internal constructor(private val readerPdfActivity: ReaderPdfActivityImpl2_Content) : FragmentStatePagerAdapter(readerPdfActivity.supportFragmentManager) {

    init {
        BaseApplication.appComponent.inject(this)
    }

    override fun getItem(position: Int): ReaderPdfFragmentImpl1_Single {
        val book = readerPdfActivity.currentBook
        val isLocal = readerPdfActivity.isLocal
       return ReaderPdfFragmentImpl1_Single.newInstance(book, isLocal, position)
    }

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE

    override fun getCount() = readerPdfActivity.document.countPages()

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        try {
            super.destroyItem(container, position, `object`)
        } catch (e: Exception) {
            Timber.e("Failed to destroyItem! position[$position]")
        }
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

}