package com.sethchhim.kuboo_client.ui.reader.pdf

import android.os.Bundle
import android.view.View
import com.sethchhim.kuboo_client.Constants.ARG_BOOK
import com.sethchhim.kuboo_client.Constants.ARG_LOCAL
import com.sethchhim.kuboo_client.Constants.ARG_POSITION
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.util.AppExecutors
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class ReaderPdfFragment : DaggerFragment() {

    @Inject lateinit var appExecutors: AppExecutors
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    protected var position = 0
    protected var isLocal = false

    lateinit var book: Book
    lateinit var readerPdfActivity: ReaderPdfActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readerPdfActivity = (activity as ReaderPdfActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            isLocal = getBoolean(ARG_LOCAL)
            position = getInt(ARG_POSITION)
            book = getParcelable(ARG_BOOK)
        }
    }

}