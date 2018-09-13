package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.sethchhim.kuboo_client.data.model.GlideLocal
import java.io.InputStream

internal class GlideLocalLoader : ModelLoader<GlideLocal, InputStream> {

    override fun handles(model: GlideLocal): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideLocal, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val key = ObjectKey("${model.book.filePath}:${model.position}")
        return ModelLoader.LoadData(key, GlideLocalFetcher(model))
    }

    internal class Factory : ModelLoaderFactory<GlideLocal, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideLocal, InputStream> {
            return GlideLocalLoader()
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

}