package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SeekBar
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.mini.R
import com.sethchhim.kuboo_client.data.model.OutlineItem
import com.sethchhim.kuboo_client.ui.reader.pdf.adapter.ReaderPdfAdapter
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File
import java.util.*

@SuppressLint("Registered")
open class ReaderPdfActivityImpl2_Content : ReaderPdfActivityImpl1_View(), SeekBar.OnSeekBarChangeListener {

    protected fun initListeners() {
        overlaySeekBar.setOnSeekBarChangeListener(this)
    }

    protected fun populateContent() {
        val file = File(currentBook.filePath)
        when (file.exists()) {
            true -> loadUri()
            false -> onFileIsInvalid()
        }
    }

    private fun loadUri() {
        currentPage = currentBook.currentPage
        searchHitPage = -1
        val file = File(currentBook.filePath)
        when (file.exists()) {
            true -> openDocument()
            false -> {
                Timber.e("Pdf file does not exist! filePath[${file.path}]")
                finish()
            }
        }
    }

    private fun openDocument() {
        val document = viewModel.initPdf(currentBook.filePath)
        val needsPassword: Boolean = document.needsPassword()
        if (needsPassword)
            askPassword(R.string.dlog_password_message)
        else
            loadDocument()
    }

    private fun askPassword(message: Int) {
        val passwordView = EditText(this)
        passwordView.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        passwordView.transformationMethod = PasswordTransformationMethod.getInstance()

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dlog_password_title)
        builder.setMessage(message)
        builder.setView(passwordView)
        builder.setPositiveButton(android.R.string.ok) { dialog, id -> checkPassword(passwordView.text.toString()) }
        builder.setNegativeButton(android.R.string.cancel) { dialog, id -> finish() }
        builder.setOnCancelListener { dialog -> finish() }
        builder.create().show()
    }

    private fun checkPassword(password: String) {
        val passwordOkay = viewModel.getPdfDocument().authenticatePassword(password)
        if (passwordOkay)
            loadDocument()
        else
            askPassword(R.string.dlog_password_retry)
    }

    override fun onProgressChanged(seekBar: SeekBar, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    protected fun resetSearch() {
        stopSearch = true
        searchHitPage = -1
        searchNeedle = null
    }

    private fun runSearch(startPage: Int, direction: Int, needle: String) {
//        stopSearch = false
//        var searchPage = startPage
//        if (stopSearch || needle != searchNeedle)
//            return
//        for (i in 0..8) {
//            val currentPage = document.loadPage(searchPage)
//            val hits = currentPage.search(searchNeedle)
//            currentPage.destroy()
//            if (hits != null && hits.isNotEmpty()) {
//                searchHitPage = searchPage
//                break
//            }
//            searchPage += direction
//            if (searchPage < 0 || searchPage >= pageCount)
//                break
//        }
//        if (stopSearch || needle != searchNeedle) {
//        } else if (searchHitPage == currentPage) {
//            loadPage()
//        } else if (searchHitPage >= 0) {
//            currentPage = searchHitPage
//            loadPage()
//        } else {
//            if (searchPage in 0..(pageCount - 1)) {
//
//            } else {
//            }
//        }
    }

    protected fun search(direction: Int) {
        val startPage = when (searchHitPage == currentPage) {
            true -> currentPage + direction
            false -> currentPage
        }
        searchHitPage = -1
        if (searchNeedle!!.isEmpty())
            searchNeedle = null
        if (searchNeedle != null)
            if (startPage in 0..(viewModel.getPdfPageCount() - 1))
                runSearch(startPage, direction, searchNeedle!!)
    }

    private fun loadDocument() {
        try {
            val document = viewModel.getPdfDocument()
            val pageCount = viewModel.getPdfPageCount()
            val metaTitle = document.getMetaData(Document.META_INFO_TITLE)
            if (metaTitle != null) title = metaTitle
            isReflowable = document.isReflowable
            if (isReflowable) document.layout(layoutW, layoutH, 0f)
            if (currentPage < 0 || currentPage >= pageCount) currentPage = 0
            loadOutline()
            onLoadDocumentSuccess()
        } catch (e: Exception) {
            currentPage = 0
            onLoadDocumentFail()
        }
    }

    private fun onLoadDocumentFail() {
        Timber.d("onLoadDocumentFail")
    }

    private fun onLoadDocumentSuccess() {
        viewPager.adapter = ReaderPdfAdapter(this, viewModel)
    }

    protected fun relayoutDocument() {
//        isRequestRelay = true
//        try {
//            val document = viewModel.getPdfDocument()
//            val mark = document.makeBookmark(currentPage)
//            document.layout(layoutW, layoutH, 0f)
//            currentPage = document.findBookmark(mark)
//        } catch (x: Throwable) {
//            currentPage = 0
//            throw x
//        }
//        loadOutline()
//        loadPage()

    }

    private fun loadOutline() = viewModel.getPdfOutline().observe(this, Observer {
        when (it == null) {
            true -> onLoadOutlineSuccess(it!!)
            false -> onLoadOutlineFail()
        }
    })

    private fun onLoadOutlineSuccess(outlineList: ArrayList<OutlineItem>) {
        Timber.d("Pdf outline load successful. size[${outlineList.size}]")
    }

    private fun onLoadOutlineFail() {
        Timber.e("Pdf outline failed to load!")
    }


    private fun onFileIsInvalid() {
        toast(getString(com.sethchhim.kuboo_client.R.string.reader_something_went_wrong))
        finish()
    }

    override fun goToFirstPage() {
        currentPage = 0
        viewPager.currentItem = currentPage
    }

    override fun goToLastPage() {
        currentPage = viewModel.getPdfPageCount() - 1
        viewPager.currentItem = currentPage
    }

    override fun goToPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            viewPager.currentItem = currentPage
        }
    }

    override fun goToNextPage() {
        if (currentPage < viewModel.getPdfPageCount() - 1) {
            currentPage++
            viewPager.currentItem = currentPage
        }
    }

    fun gotoPage(p: Int) {
        if (p >= 0 && p < viewModel.getPdfPageCount() && p != currentPage) {
            currentPage = p
            viewPager.currentItem = currentPage
        }
    }

    fun gotoURI(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        try {
            startActivity(intent)
        } catch (x: Throwable) {

        }
    }

}