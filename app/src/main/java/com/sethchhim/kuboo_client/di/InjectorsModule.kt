package com.sethchhim.kuboo_client.di

import com.sethchhim.kuboo_client.ui.about.AboutActivity
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.log.LogActivity
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.ui.main.MainModule
import com.sethchhim.kuboo_client.ui.main.MainModuleSub
import com.sethchhim.kuboo_client.ui.preview.PreviewActivity
import com.sethchhim.kuboo_client.ui.preview.PreviewActivityLandscape
import com.sethchhim.kuboo_client.ui.preview.PreviewActivityPortrait
import com.sethchhim.kuboo_client.ui.reader.base.ReaderModule
import com.sethchhim.kuboo_client.ui.reader.base.ReaderModuleSub
import com.sethchhim.kuboo_client.ui.reader.book.ReaderEpubActivity
import com.sethchhim.kuboo_client.ui.reader.book.ReaderEpubActivityLandscape
import com.sethchhim.kuboo_client.ui.reader.book.ReaderEpubActivityPortrait
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivity
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivityLandscape
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivityPortrait
import com.sethchhim.kuboo_client.ui.reader.pdf.ReaderPdfActivity
import com.sethchhim.kuboo_client.ui.scope.*
import com.sethchhim.kuboo_client.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InjectorsModule {

    @BaseScope
    @ContributesAndroidInjector()
    abstract fun baseActivity(): BaseActivity

    @PreviewScope
    @ContributesAndroidInjector
    abstract fun logActivity(): LogActivity

    @BaseScope
    @ContributesAndroidInjector()
    abstract fun splashActivity(): SplashActivity

    @MainScope
    @ContributesAndroidInjector(modules = [(MainModule::class), (MainModuleSub::class)])
    abstract fun mainActivity(): MainActivity

    @PreviewScope
    @ContributesAndroidInjector
    abstract fun previewActivity(): PreviewActivity

    @PreviewScope
    @ContributesAndroidInjector
    abstract fun previewActivityLandscape(): PreviewActivityLandscape

    @PreviewScope
    @ContributesAndroidInjector
    abstract fun previewActivityPortrait(): PreviewActivityPortrait

    @ReaderScope
    @ContributesAndroidInjector()
    abstract fun readerEpubActivity(): ReaderEpubActivity

    @ReaderScope
    @ContributesAndroidInjector()
    abstract fun readerEpubActivityLandscape(): ReaderEpubActivityLandscape

    @ReaderScope
    @ContributesAndroidInjector()
    abstract fun readerEpubActivityPortrait(): ReaderEpubActivityPortrait

    @ReaderScope
    @ContributesAndroidInjector(modules = [(ReaderModule::class), (ReaderModuleSub::class)])
    abstract fun readerComicActivity(): ReaderComicActivity

    @ReaderScope
    @ContributesAndroidInjector(modules = [(ReaderModule::class), (ReaderModuleSub::class)])
    abstract fun readerComicActivityLandscape(): ReaderComicActivityLandscape

    @ReaderScope
    @ContributesAndroidInjector(modules = [(ReaderModule::class), (ReaderModuleSub::class)])
    abstract fun readerComicActivityPortrait(): ReaderComicActivityPortrait

    @ReaderScope
    @ContributesAndroidInjector(modules = [(ReaderModule::class), (ReaderModuleSub::class)])
    abstract fun readerPdfActivity(): ReaderPdfActivity

    @AboutScope
    @ContributesAndroidInjector
    abstract fun aboutActivity(): AboutActivity

}