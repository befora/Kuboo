package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.kuboo_local.KubooLocal

class LocalRepository(private val kubooLocal: KubooLocal) {

    internal fun cleanupParser() = kubooLocal.cleanupParser()

    internal fun getLocalComicInfo() = kubooLocal.getLocalComicInfo()

    internal fun getLocalImageInputStream(position: Int) = kubooLocal.getLocalInputStreamAt(position)

}