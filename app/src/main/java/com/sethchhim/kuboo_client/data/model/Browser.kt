package com.sethchhim.kuboo_client.data.model

import android.annotation.SuppressLint
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.sethchhim.kuboo_remote.model.Book

@SuppressLint("ParcelCreator")
class Browser(var type: Int, var book: Book) : MultiItemEntity {

    companion object {
        const val FOLDER = 1
        const val MEDIA = 2
        const val MEDIA_FORCE_LIST = 3
    }

    override fun getItemType(): Int {
        return type
    }

}