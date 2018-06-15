package com.sethchhim.kuboo_client.data.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import java.io.InputStream

internal class GlideRemoteLoader(private val client: Call.Factory) : ModelLoader<GlideUrl, InputStream> {

    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideUrl, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(model, GlideRemoteFetcher(client, model))
    }

    internal class Factory(private val client: Call.Factory) : ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return GlideRemoteLoader(client)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

}