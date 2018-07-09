package com.sethchhim.kuboo_remote.model

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.sethchhim.kuboo_remote.util.Settings.MAX_PAGE_WIDTH_DEFAULT
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.io.File
import java.net.URL
import java.text.NumberFormat
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
@Entity
data class Book(
        @PrimaryKey(autoGenerate = true) override var autoId: Int = 0,
        override var id: Int = 0,
        override var title: String = "",
        override var author: String = "",
        override var content: String = "",
        override var linkAcquisition: String = "",
        override var linkSubsection: String = "",
        override var linkThumbnail: String = "",
        override var linkXmlPath: String = "",
        override var linkPrevious: String = "",
        override var linkNext: String = "",
        override var linkPse: String = "",
        override var currentPage: Int = 0,
        override var totalPages: Int = 0,
        override var server: String = "",
        override var filePath: String = "",
        override var bookMark: String = "",
        override var isFavorite: Boolean = false,
        override var isFinished: Boolean = false,
        override var timeAccessed: Int = 0) : BookData(), Parcelable {

    fun print() {
        Timber.i("======================================")
        Timber.i("autoId: $autoId")
        Timber.i("id: $id")
        Timber.i("title: $title")
        Timber.i("author: $author")
        Timber.i("content: $content")
        Timber.i("linkAcquisition: $linkAcquisition")
        Timber.i("linkSubsection: $linkSubsection")
        Timber.i("linkThumbnail: $linkThumbnail")
        Timber.i("linkXmlPath: $linkXmlPath")
        Timber.i("linkPrevious: $linkPrevious")
        Timber.i("linkNext: $linkNext")
        Timber.i("linkPse: $linkPse")
        Timber.i("currentPage: $currentPage")
        Timber.i("totalPages: $totalPages")
        Timber.i("server: $server")
        Timber.i("bookMark: $bookMark")
        Timber.i("isFavorite: $isFavorite")
        Timber.i("isFinished: $isFinished")
        Timber.i("======================================")
    }

    fun getPseCover(maxWidth: Int): String {
        var result: String
        if (isComic()) {
            result = linkPse
            result = result.replace("{pageNumber}", "00")
            result = result.replace("{maxWidth}", maxWidth.toString())
        } else {
            result = linkThumbnail
        }

        return result
    }

    fun getPse(maxWidth: Int, index: Int): String {
        var result = linkPse
        result = result.replace("{pageNumber}", index.toMinimumTwoDigits())
        result = result.replace("{maxWidth}", maxWidth.toString())
        return result
    }

    fun setTimeAccessed() {
        timeAccessed = (Date().time / 1000).toInt()
    }

    fun isEmpty(): Boolean {
        val idEmpty = this.id == 0
        val titleEmpty = this.title.isEmpty()
        val authorEmpty = this.author.isEmpty()
        val contentEmpty = this.content.isEmpty()
        return idEmpty && titleEmpty && authorEmpty && contentEmpty
    }

    fun isEpub(): Boolean {
        return linkAcquisition.endsWith(".epub", ignoreCase = true)
    }

    fun isComic(): Boolean {
        return linkAcquisition.endsWith(".cb7", ignoreCase = true)
                || linkAcquisition.endsWith(".cba", ignoreCase = true)
                || linkAcquisition.endsWith(".cbr", ignoreCase = true)
                || linkAcquisition.endsWith(".cbt", ignoreCase = true)
                || linkAcquisition.endsWith(".cbz", ignoreCase = true)
                || linkAcquisition.endsWith(".zip", ignoreCase = true)
                || linkAcquisition.endsWith(".rar", ignoreCase = true)
    }

    fun isPdf(): Boolean {
        return linkAcquisition.endsWith(".pdf", ignoreCase = true)
    }

    fun isRar(): Boolean {
        return linkAcquisition.endsWith("cbr", ignoreCase = true)
                || linkAcquisition.endsWith("rar", ignoreCase = true)
    }

    fun isLocal() = filePath.isNotEmpty()

    fun isRemote() = filePath.isEmpty()

    fun isLocalValid() = File(filePath).exists()

    internal fun containsBookmarks(): Boolean {
        return isComic() || isPdf()
    }

    internal fun isSupportedType(): Boolean {
        return isComic() || isEpub() || isPdf()
    }

    fun isBannedFromRecent(): Boolean {
        return this.linkXmlPath.equals("all", ignoreCase = true) or
                this.linkXmlPath.equals("?latest=true", ignoreCase = true) or
                this.linkXmlPath.contains("?search=true&searchstring=", ignoreCase = true) or
                this.linkXmlPath.contains("all?search=true&displayFiles=true&index=", ignoreCase = true)
    }

    fun getIdString() = try {
        id.toString()
    } catch (e: Exception) {
        Timber.e("Failed to get id! ${e.message}")
        "0"
    }

    fun getXmlId(): Int {
        try {
            if (this.linkXmlPath.contains("/?displayFiles=true")) {
                val index = this.linkXmlPath.lastIndexOf("/?displayFiles=true")
                return Integer.parseInt(this.linkXmlPath.substring(0, index))
            } else {
                val index = this.linkXmlPath.lastIndexOf("/")
                Timber.d("AAA linkXmlPath ${Integer.parseInt(this.linkXmlPath.substring(0, index))}")
                return Integer.parseInt(this.linkXmlPath.substring(0, index))
            }
        } catch (e: Exception) {
            Timber.e("Failed to get xml id! ${e.message}")
            e.printStackTrace()
        }
        return 0
    }

    fun getXmlIdString() = try {
        getXmlId().toString()
    } catch (e: Exception) {
        Timber.e("Failed to get xml id! ${e.message}")
        "0"
    }

    internal fun getFormattedContent(): String {
        var result = content
        try {
            if (result.contains("[") && result.contains("]")) {
                //restart text in brackets
                val startBracketIndex = result.indexOf("[")
                val endBracketIndex = result.indexOf("]")
                val s = result.substring(startBracketIndex, endBracketIndex + 2)
                result = result.replace(s, "")
            }
        } catch (ignored: Exception) {
        }

        if (result.contains(" CBZ ")) {
            result = result.replace(" CBZ ", "\n\n" + author + "\n\nCBZ ")
        }
        if (result.contains(" CBR ")) {
            result = result.replace(" CBR ", "\n\n" + author + "\n\nCBR ")
        }
        if (result.contains(" ZIP ")) {
            result = result.replace(" ZIP ", "\n\n" + author + "\n\nZIP ")
        }
        if (result.contains(" RAR ")) {
            result = result.replace(" RAR ", "\n\n" + author + "\n\nRAR ")
        }
        if (result.contains(" EPUB ")) {
            result = result.replace(" EPUB ", "\n\n" + author + "\n\nEPUB ")
        }
        if (result.contains(" PDF ")) {
            result = result.replace(" PDF ", "\n\n" + author + "\n\nPDF ")
        }

        if (result.contains(") ")) {
            //add new line after parenthesis
            result = result.replace(") ", ")\n\n")
        }
        return result
    }


    private fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

//    fun setSinglePageNumber(isDualPane: Boolean, currentItem: Int, singlePageUrlList: ArrayList<PageUrl>, widePageUrlList: ArrayList<PageUrl>) {
//        if (isDualPane) {
//            val wideItem = widePageUrlList[currentItem].page0
//
//            singlePageUrlList.forEachIndexed { index, pageUrlItem ->
//                val singleItem = pageUrlItem.page0
//                val isMatch = singleItem == wideItem
//                if (isMatch) {
//                    this.currentPage = index
//                    Timber.i("[READER] Dual pane detected widePage[$currentItem] singlePage[$index]")
//                }
//            }
//        }
//    }
//
//    fun PageUrl.getRealPageNumber(): Int {
//        val url = page0
//        if (!url.isEmpty()) {
//            val startIndex = url.indexOf("=") + 1
//            val endIndex = url.indexOf("&")
//            val pageNumberString = url.substring(startIndex, endIndex)
//            return Integer.parseInt(pageNumberString)
//        } else {
//            return 0
//        }
//    }

    fun isMatch(book: Book) = this.id == book.id && this.getXmlId() == book.getXmlId() && this.server == book.server

    fun isMatchXmlId(book: Book) = this.getXmlId() == book.getXmlId()

    fun isMatchCurrentPage(book: Book) = currentPage == book.currentPage

    fun getAcquisitionUrl() = server + linkAcquisition

    fun getPreviewUrl() = server + linkThumbnail

    fun getPreviewUrl(maxWidth: Int) = server + getPseCover(maxWidth)

    fun getPreviewUrl(login: Login, maxWidth: Int) = login.server + getPseCover(maxWidth)

    fun getPreviewUrlMatchingWidthTo(previewUrl: String?): String? {
        if (previewUrl == null) {
            return null
        } else {
            val startIndex = previewUrl.lastIndexOf("=") + 1
            val endIndex = previewUrl.length
            val maxWidth = previewUrl.substring(startIndex, endIndex)

            val maxWidthInt = try {
                maxWidth.toInt()
            } catch (e: Exception) {
                MAX_PAGE_WIDTH_DEFAULT
            }

            return getPreviewUrl(maxWidthInt)
        }
    }

    private fun Int.toMinimumTwoDigits(): String {
        val numberFormat = NumberFormat.getInstance(Locale.US)
        numberFormat.minimumIntegerDigits = 2
        return numberFormat.format(this)
    }

}