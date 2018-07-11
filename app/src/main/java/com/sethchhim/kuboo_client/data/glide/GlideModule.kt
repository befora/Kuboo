package com.sethchhim.kuboo_client.data.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.model.GlideLocal
import com.sethchhim.kuboo_remote.KubooRemote
import java.io.InputStream
import javax.inject.Inject


@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
class GlideModule : AppGlideModule() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var kubooRemote: KubooRemote

    override fun isManifestParsingEnabled() = false

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)

        val passthroughFactory = GlidePassthroughLoader.Factory()
        registry.prepend(InputStream::class.java, InputStream::class.java, passthroughFactory)

        val remoteFactory = GlideRemoteLoader.Factory(kubooRemote.getOkHttpClient())
        registry.replace(GlideUrl::class.java, InputStream::class.java, remoteFactory)

        val localFactory = GlideLocalLoader.Factory()
        registry.replace(GlideLocal::class.java, InputStream::class.java, localFactory)
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDiskCache(DiskLruCacheFactory(Glide.getPhotoCacheDir(context)!!.absolutePath, 100 * 1024 * 1024))
    }

}