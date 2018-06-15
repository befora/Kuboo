package com.sethchhim.kuboo_client.di

import com.sethchhim.kuboo_client.ui.about.AboutActivity
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.ui.main.MainModule
import com.sethchhim.kuboo_client.ui.main.MainModuleSub
import com.sethchhim.kuboo_client.ui.preview.PreviewActivity
import com.sethchhim.kuboo_client.ui.reader.base.ReaderModule
import com.sethchhim.kuboo_client.ui.reader.base.ReaderModuleSub
import com.sethchhim.kuboo_client.ui.reader.book.ReaderEpubActivity
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivity
import com.sethchhim.kuboo_client.ui.reader.pdf.ReaderPdfActivity
import com.sethchhim.kuboo_client.ui.scope.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InjectorsModule {

    @BaseScope
    @ContributesAndroidInjector()
    abstract fun baseActivity(): BaseActivity

    @MainScope
    @ContributesAndroidInjector(modules = [(MainModule::class), (MainModuleSub::class)])
    abstract fun mainActivity(): MainActivity

    @PreviewScope
    @ContributesAndroidInjector
    abstract fun previewActivity(): PreviewActivity

    @ReaderScope
    @ContributesAndroidInjector()
    abstract fun readerEpubActivity(): ReaderEpubActivity

    @ReaderScope
    @ContributesAndroidInjector(modules = [(ReaderModule::class), (ReaderModuleSub::class)])
    abstract fun readerComicActivity(): ReaderComicActivity

    @ReaderScope
    @ContributesAndroidInjector()
    abstract fun readerPdfActivity(): ReaderPdfActivity

    @AboutScope
    @ContributesAndroidInjector
    abstract fun aboutActivity(): AboutActivity

}