package com.sethchhim.kuboo_client.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_remote.model.Book

@SuppressLint("Registered")
open class BaseActivity : BaseActivityImpl2_Read() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransitionDuration()
        setTheme(getAppTheme())

        bookmarkDialog = dialogUtil.getDialogBookmark(this)
        loadingDialog = dialogUtil.getDialogLoading(this)

        initIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initIntent(intent)
    }

    private fun initIntent(intent: Intent) = intent.apply {
        source = (getSerializableExtra(Constants.ARG_SOURCE) as Source?) ?: Source.UNKNOWN
        currentBook = getParcelableExtra(Constants.ARG_BOOK) ?: Book()
        previousBook = Book()
        nextBook = Book()
        transitionUrl = getStringExtra(Constants.ARG_TRANSITION_URL) ?: ""
    }

}


