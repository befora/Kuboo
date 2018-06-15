package com.sethchhim.kuboo_client.ui.reader.comic.adapter

import android.os.Parcelable
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivityImpl2_Content
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicFragment
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicFragmentImpl1_Single
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicFragmentImpl2_Dual
import javax.inject.Inject

class ReaderComicAdapter internal constructor(private val readerComicActivityImpl2Content: ReaderComicActivityImpl2_Content) : FragmentStatePagerAdapter(readerComicActivityImpl2Content.supportFragmentManager) {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var viewModel: ViewModel

    override fun getItem(position: Int): ReaderComicFragment {
        val book = readerComicActivityImpl2Content.currentBook
        val isLocal = readerComicActivityImpl2Content.isLocal
        val isPositionDual = getIsPositionDual(position)

        return when (Settings.DUAL_PANE && isPositionDual) {
            true -> ReaderComicFragmentImpl2_Dual.newInstance(book, isLocal, position)
            false -> ReaderComicFragmentImpl1_Single.newInstance(book, isLocal, position)
        }
    }

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE

    override fun getCount() = viewModel.getReaderListSize()

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    private fun getIsPositionDual(position: Int) = viewModel.getReaderItemAt(position)?.page1 != Constants.KEY_SINGLE

}