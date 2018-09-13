package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.sethchhim.kuboo_client.data.model.GlideEpub
import java.io.InputStream

internal class GlideEpubLoader : ModelLoader<GlideEpub, InputStream> {

    override fun handles(model: GlideEpub): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideEpub, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val key = ObjectKey("${model.book.filePath}:${model.position}")
        return ModelLoader.LoadData(key, GlideEpubFetcher(model))
    }

    internal class Factory : ModelLoaderFactory<GlideEpub, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideEpub, InputStream> {
            return GlideEpubLoader()
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

}