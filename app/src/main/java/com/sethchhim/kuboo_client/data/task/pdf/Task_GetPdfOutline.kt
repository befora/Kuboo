package com.sethchhim.kuboo_client.data.task.pdf

import android.arch.lifecycle.MutableLiveData
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.Outline
import com.sethchhim.kuboo_client.data.model.OutlineItem
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber
import java.util.ArrayList
import kotlin.Comparator


open class Task_GetPdfOutline(private val document: Document) : Task_LocalBase() {

    internal val liveData = MutableLiveData<ArrayList<OutlineItem>>()

    init {
        executors.diskIO.execute {
            try {
                val flatOutline = ArrayList<OutlineItem>()
                val outline = document.loadOutline()
                if (outline != null) {
                    flattenOutline(outline, "", flatOutline)
                    flatOutline.sortWith(Comparator { o1, o2 -> o1.currentPage - o2.currentPage })
                    loadOutlineTotal(flatOutline)
                    executors.mainThread.execute { liveData.value = flatOutline }
                }
            } catch (e: Exception) {
                Timber.e(e)
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

    private fun flattenOutline(outline: Array<Outline>, indent: String, flatOutline: ArrayList<OutlineItem>) {
        for (node in outline) {
            if (node.title != null) flatOutline.add(OutlineItem(indent + node.title, node.page, 1))
            if (node.down != null) flattenOutline(node.down, "$indent    ", flatOutline)
        }
    }

    private fun loadOutlineTotal(flatOutline: ArrayList<OutlineItem>) {
        flatOutline.forEachIndexed { index, _ ->
            if (index != flatOutline.size - 1) {
                val currentStart = flatOutline[index].currentPage
                val nextPosition = index + 1
                val currentEnd = flatOutline[nextPosition].currentPage - 1
                flatOutline[index].totalPages = currentEnd - (currentStart - 1)
            } else {
                val currentStart = flatOutline[index].currentPage
                val currentEnd = document.countPages() - 1
                flatOutline[index].totalPages = currentEnd - (currentStart - 1)
            }
        }
    }

}