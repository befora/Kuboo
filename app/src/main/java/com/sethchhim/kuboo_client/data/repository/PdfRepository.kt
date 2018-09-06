package com.sethchhim.kuboo_client.data.repository

import com.artifex.mupdf.fitz.Document
import com.sethchhim.kuboo_client.data.model.GlidePdf
import com.sethchhim.kuboo_client.data.task.pdf.Task_GetPdfImageInputStream
import com.sethchhim.kuboo_client.data.task.pdf.Task_GetPdfOutline

class PdfRepository {

    internal lateinit var document: Document

    internal fun initPdf(filePath: String): Document {
        document = Document.openDocument(filePath)
        return document
    }

    internal fun getPdfPageCount() = document.countPages()

    internal fun getPdfImageInputStream(glidePdf: GlidePdf) = Task_GetPdfImageInputStream(document, glidePdf).liveData

    internal fun getPdfOutline() = Task_GetPdfOutline(document).liveData

}