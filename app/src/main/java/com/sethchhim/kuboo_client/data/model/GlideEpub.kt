package com.sethchhim.kuboo_client.data.model

import com.sethchhim.kuboo_remote.model.Book

data class GlideEpub(val book: Book, val position: Int, val singleInstance: Boolean = false)