package com.sethchhim.kuboo_client.data.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.sethchhim.kuboo_remote.model.BookData
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@Entity
@Parcelize
@SuppressLint("ParcelCreator")
data class Download(
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
        override var timeAccessed: Int = 0,
        @Ignore var ignored: String? = null) : BookData(), Parcelable {

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

}