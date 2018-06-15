package com.sethchhim.kuboo_client.ui.reader.base

import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicFragment
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicFragmentImpl1_Single
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicFragmentImpl2_Dual
import com.sethchhim.kuboo_client.ui.scope.ComicScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ReaderModuleSub {

    @ComicScope
    @ContributesAndroidInjector
    internal abstract fun readerComicFragment(): ReaderComicFragment

    @ComicScope
    @ContributesAndroidInjector
    internal abstract fun readerComicFragmentDualImpl(): ReaderComicFragmentImpl2_Dual

    @ComicScope
    @ContributesAndroidInjector
    internal abstract fun readerComicFragmentSingleImpl(): ReaderComicFragmentImpl1_Single

}