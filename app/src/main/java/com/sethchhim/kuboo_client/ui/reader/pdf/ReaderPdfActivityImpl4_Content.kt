package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.support.v4.view.ViewPager
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SeekBar
import com.artifex.mupdf.fitz.Document
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.enum.ScaleType
import com.sethchhim.kuboo_client.data.model.OutlineItem
import com.sethchhim.kuboo_client.ui.reader.pdf.adapter.ReaderPdfAdapter
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File
import java.util.*

@SuppressLint("Registered")
open class ReaderPdfActivityImpl4_Content : ReaderPdfActivityImpl3_Menu() {

    protected fun initListeners() {
        viewPager.clearOnPageChangeListeners()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                setOverlayPosition(position)
                savePdfBookmark(position)
            }
        })
        overlaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewPager.currentItem = seekBar.progress
            }
        })
    }

    protected fun populateContent() {
        val file = File(currentBook.filePath)
        when (file.exists()) {
            true -> onFileIsValid(file)
            false -> onFileIsInvalid(file)
        }
    }

    protected fun refreshViewpager() {
        val currentItem = viewPager.currentItem
        viewPager.adapter = ReaderPdfAdapter(this)
        viewPager.currentItem = currentItem
    }

    private fun openDocument() {
        val document = viewModel.initPdf(currentBook.filePath)
        val needsPassword: Boolean = document.needsPassword()
        if (needsPassword)
            askPassword("The document is encrypted")
        else
            loadDocument()
    }

    private fun askPassword(message: String) {
        val passwordView = EditText(this)
        passwordView.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        passwordView.transformationMethod = PasswordTransformationMethod.getInstance()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Password:")
        builder.setMessage(message)
        builder.setView(passwordView)
        builder.setPositiveButton(android.R.string.ok) { _, _ -> checkPassword(passwordView.text.toString()) }
        builder.setNegativeButton(android.R.string.cancel) { _, _ -> finish() }
        builder.setOnCancelListener { finish() }
        builder.create().show()
    }

    private fun checkPassword(password: String) {
        val passwordOkay = viewModel.getPdfDocument().authenticatePassword(password)
        if (passwordOkay)
            loadDocument()
        else
            askPassword("Invalid password.")
    }

    private fun loadDocument() {
        try {
            val document = viewModel.getPdfDocument()
            val metaTitle = document.getMetaData(Document.META_INFO_TITLE)
            if (metaTitle != null && metaTitle.isNotEmpty()) title = metaTitle
            val pageCount = viewModel.getPdfPageCount()
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
        when (it != null) {
            true -> onLoadOutlineSuccess(it!!)
            false -> onLoadOutlineFail()
        }
    })

    override fun goToFirstPage() {
        viewPager.currentItem = 0
    }

    override fun goToLastPage() {
        viewPager.adapter?.let {
            viewPager.currentItem = it.count - 1
        }
    }

    override fun goToPreviousPage() {
        when (viewPager.currentItem == 0) {
            true -> onSwipeOutOfBoundsStart()
            false -> viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    override fun goToNextPage() {
        viewPager.adapter?.let {
            when (viewPager.currentItem == it.count - 1) {
                true -> onSwipeOutOfBoundsEnd()
                false -> viewPager.currentItem = viewPager.currentItem + 1
            }
        }
    }

    private fun onFileIsValid(file: File) {
        title = file.name
        currentPage = currentBook.currentPage
        savePdfBookmark(currentPage)
        searchHitPage = -1
        openDocument()
    }

    private fun onFileIsInvalid(file: File) {
        Timber.e("Pdf file does not exist! filePath[${file.path}]")
        toast(getString(com.sethchhim.kuboo_client.R.string.reader_something_went_wrong))
        finish()
    }

    private fun onLoadDocumentSuccess() {
        viewPager.adapter = ReaderPdfAdapter(this)
        viewPager.currentItem = currentBook.currentPage
        setOverlay(currentBook.currentPage, currentBook.totalPages)
    }

    private fun onLoadDocumentFail() {
        Timber.d("onLoadDocumentFail")
    }

    private fun onLoadOutlineSuccess(outlineList: ArrayList<OutlineItem>) {
        Timber.d("Pdf outline load successful. size[${outlineList.size}]")
    }

    private fun onLoadOutlineFail() {
        Timber.e("Pdf outline failed to load!")
    }

    private fun printOutline() {
        when (flatOutline != null) {
            true -> flatOutline?.forEachWithIndex { i, item ->
                Timber.d("$i  ${item.title} currentPage[${item.currentPage}] total[${item.totalPages}]")
            }
            false -> Timber.e("Outline is null")
        }
    }

    protected fun setScaleType(menuItem: MenuItem, scaleType: Int) {
        menuItem.isChecked = true
        when (scaleType) {
            0 -> Settings.SCALE_TYPE = ScaleType.ASPECT_FILL.value
            1 -> Settings.SCALE_TYPE = ScaleType.ASPECT_FIT.value
            2 -> Settings.SCALE_TYPE = ScaleType.FIT_WIDTH.value
        }
        sharedPrefsHelper.saveScaleType()
        viewPager.adapter?.notifyDataSetChanged()
    }

}