package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

internal class GlidePassthroughLoader : ModelLoader<InputStream, InputStream> {

    override fun handles(model: InputStream): Boolean {
        return true
    }

    override fun buildLoadData(model: InputStream, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val key = ObjectKey(model)
        return ModelLoader.LoadData(key, GlidePassthroughFetcher(model))
    }

    internal class Factory : ModelLoaderFactory<InputStream, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<InputStream, InputStream> {
            return GlidePassthroughLoader()
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

}