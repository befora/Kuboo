package com.sethchhim.kuboo_client.data.repository

import com.sethchhim.epublibdroid_kotlin.task.Task_EpubCoverInputStream
import com.sethchhim.kuboo_client.data.model.GlideEpub
import com.sethchhim.kuboo_local.KubooLocal

class LocalRepository(private val kubooLocal: KubooLocal) {

    internal fun cleanupParser() = kubooLocal.cleanupParser()

    internal fun getLocalComicInfo() = kubooLocal.getLocalComicInfo()

    internal fun getLocalImageInputStream(position: Int) = kubooLocal.getLocalInputStreamAt(position)

    internal fun getLocalImageInputStreamSingleInstance(filePath: String, position: Int) = kubooLocal.getLocalImageInputStreamSingleInstance(filePath, position)

    internal fun getEpubCoverInputStream(glideEpub: GlideEpub) = Task_EpubCoverInputStream(kubooLocal.mainThread, kubooLocal.diskIO, glideEpub.book.filePath).liveData

}