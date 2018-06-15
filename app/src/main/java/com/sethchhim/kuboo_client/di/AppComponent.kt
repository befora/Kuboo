package com.sethchhim.kuboo_client.di

import android.content.Context
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.glide.GlideLocalFetcher
import com.sethchhim.kuboo_client.data.glide.GlideModule
import com.sethchhim.kuboo_client.data.glide.GlidePassthroughFetcher
import com.sethchhim.kuboo_client.data.glide.GlideRemoteFetcher
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_client.service.IntentService
import com.sethchhim.kuboo_client.service.OnClearFromRecentService
import com.sethchhim.kuboo_client.ui.about.adapter.AboutPagerAdapter
import com.sethchhim.kuboo_client.ui.about.adapter.FaqAdapter
import com.sethchhim.kuboo_client.ui.about.adapter.LicenseAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadsAdapter
import com.sethchhim.kuboo_client.ui.main.login.adapter.LoginAdapter
import com.sethchhim.kuboo_client.ui.main.recent.adapter.RecentAdapter
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragmentImp0_View
import com.sethchhim.kuboo_client.ui.reader.comic.adapter.ReaderComicAdapter
import com.sethchhim.kuboo_client.ui.scope.AppScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@AppScope
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (InjectorsModule::class)])
interface AppComponent : AndroidInjector<BaseApplication> {

    override fun inject(baseApplication: BaseApplication)

    fun inject(glideModule: GlideModule)
    fun inject(glideLocalFetcher: GlideLocalFetcher)
    fun inject(glideRemoteFetcher: GlideRemoteFetcher)

    fun inject(aboutPagerAdapter: AboutPagerAdapter)
    fun inject(browserContentAdapter: BrowserContentAdapter)
    fun inject(browserPathAdapter: BrowserPathAdapter)
    fun inject(downloadsAdapter: DownloadsAdapter)
    fun inject(faqAdapter: FaqAdapter)
    fun inject(loginAdapter: LoginAdapter)
    fun inject(readerComicAdapter: ReaderComicAdapter)
    fun inject(recentAdapter: RecentAdapter)
    fun inject(task_LocalBase: Task_LocalBase)
    fun inject(settingsFragment: SettingsFragmentImp0_View)
    fun inject(intentService: IntentService)
    fun inject(onClearFromRecentService: OnClearFromRecentService)
    fun inject(glidePassthroughFetcher: GlidePassthroughFetcher)
    fun inject(licenseAdapter: LicenseAdapter)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

}