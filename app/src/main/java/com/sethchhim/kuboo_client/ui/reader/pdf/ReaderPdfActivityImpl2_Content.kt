package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SeekBar
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.mini.R
import com.sethchhim.kuboo_client.Extensions.toUri
import com.sethchhim.kuboo_client.data.model.OutlineItem
import com.sethchhim.kuboo_client.ui.reader.pdf.adapter.ReaderPdfAdapter
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

@SuppressLint("Registered")
open class ReaderPdfActivityImpl2_Content : ReaderPdfActivityImpl1_View(), SeekBar.OnSeekBarChangeListener {

    protected fun initListeners() {
        overlaySeekBar.setOnSeekBarChangeListener(this)
    }

    protected fun populateContent() {
        val file = File(currentBook.filePath)
        when (file.exists()) {
            true -> loadUri(file.toUri())
            false -> onFileIsInvalid()
        }
    }

    private fun loadUri(uri: Uri) {
        mimetype = intent.type
        key = uri.toString()
        if (uri.scheme == "file") {
            title = uri.lastPathSegment
            path = uri.path
        } else {
            title = uri.toString()
            try {
                val stm = contentResolver.openInputStream(uri)
                buffer = stm?.readBytes()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }

        prefs = getPreferences(Context.MODE_PRIVATE)
        layoutEm = prefs!!.getFloat("layoutEm", 8f)
        fitPage = prefs!!.getBoolean("fitPage", false)
        currentPage = prefs!!.getInt(key, 0)
        searchHitPage = -1
        hasLoaded = false

        openDocument()
    }

    private fun openDocument() {
        document = when (path != null) {
            true -> Document.openDocument(path)
            false -> Document.openDocument(buffer, mimetype)
        }
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
        val passwordOkay = document.authenticatePassword(password)
        if (passwordOkay)
            loadDocument()
        else
            askPassword(R.string.dlog_password_retry)
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
            if (startPage in 0..(pageCount - 1))
                runSearch(startPage, direction, searchNeedle!!)
    }

    private fun loadDocument() {
        try {
            val metaTitle = document.getMetaData(Document.META_INFO_TITLE)
            if (metaTitle != null)
                title = metaTitle
            isReflowable = document.isReflowable
            if (isReflowable) {
                document.layout(layoutW, layoutH, layoutEm)
            }
            pageCount = document.countPages()
            if (currentPage < 0 || currentPage >= pageCount) currentPage = 0
            loadOutline()
            onLoadDocumentSuccess()
        } catch (e: Exception) {
            pageCount = 1
            currentPage = 0
            onLoadDocumentFail()
        }
    }

    private fun onLoadDocumentFail() {
        Timber.d("onLoadDocumentFail")
    }

    private fun onLoadDocumentSuccess() {
        Timber.d("onLoadDocumentSuccess ${document.countPages()}")
        viewPager.adapter = ReaderPdfAdapter(this)
    }

//    protected fun relayoutDocument() {
//        isRequestRelay = true
//        try {
//            val mark = document.makeBookmark(currentPage)
//            document.layout(layoutW, layoutH, layoutEm)
//            pageCount = document.countPages()
//            currentPage = document.findBookmark(mark)
//        } catch (x: Throwable) {
//            pageCount = 1
//            currentPage = 0
//            throw x
//        }
//        loadOutline()
//        loadPage()
//    }

    private fun loadOutline() {
        val outline = document.loadOutline()
        if (outline != null) {
            flatOutline = ArrayList()
            flattenOutline(outline, "")
            flatOutline!!.sortWith(Comparator { o1, o2 -> o1.currentPage - o2.currentPage })
            loadOutlineTotal()
        } else {
            flatOutline = null
        }

        if (flatOutline != null) {
            if (!isRequestRelay) onLoadOutlineSuccess()
        } else {
            if (!isRequestRelay) onLoadOutlineFail()
        }
    }

    private fun flattenOutline(outline: Array<com.artifex.mupdf.fitz.Outline>, indent: String) {

        for (node in outline) {
            if (node.title != null)
                flatOutline!!.add(OutlineItem(indent + node.title, node.page, 1))
            if (node.down != null)
                flattenOutline(node.down, "$indent    ")
        }
    }

    private fun loadOutlineTotal() {
        for (i in flatOutline!!.indices) {
            if (i != flatOutline!!.size - 1) {
                val currentStart = flatOutline!![i].currentPage
                val nextPosition = i + 1
                val currentEnd = flatOutline!![nextPosition].currentPage - 1
                flatOutline!![i].totalPages = currentEnd - (currentStart - 1)
            } else {
                val currentStart = flatOutline!![i].currentPage
                val currentEnd = document.countPages() - 1
                flatOutline!![i].totalPages = currentEnd - (currentStart - 1)
            }
        }
    }

    protected fun onLoadOutlineSuccess() {}

    protected fun onLoadOutlineFail() {}


    protected fun onFileIsInvalid() {
        toast(getString(com.sethchhim.kuboo_client.R.string.reader_something_went_wrong))
        finish()
    }

    override fun goToFirstPage() {
        currentPage = 0
        viewPager.currentItem = currentPage
    }

    override fun goToLastPage() {
        currentPage = pageCount - 1
        viewPager.currentItem = currentPage
    }

    override fun goToPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            viewPager.currentItem = currentPage
        }
    }

    override fun goToNextPage() {
        if (currentPage < pageCount - 1) {
            currentPage++
            viewPager.currentItem = currentPage
        }
    }

    fun gotoPage(p: Int) {
        if (p >= 0 && p < pageCount && p != currentPage) {
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

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

}