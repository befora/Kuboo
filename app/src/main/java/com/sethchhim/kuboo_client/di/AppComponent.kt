package com.sethchhim.kuboo_client.di

import android.content.Context
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.glide.GlideLocalFetcher
import com.sethchhim.kuboo_client.data.glide.GlideModule
import com.sethchhim.kuboo_client.data.glide.GlidePassthroughFetcher
import com.sethchhim.kuboo_client.data.glide.GlideRemoteFetcher
import com.sethchhim.kuboo_client.data.repository.FetchRepository
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import com.sethchhim.kuboo_client.service.IntentService
import com.sethchhim.kuboo_client.service.OnClearFromRecentService
import com.sethchhim.kuboo_client.service.TrackingService
import com.sethchhim.kuboo_client.ui.about.adapter.AboutPagerAdapter
import com.sethchhim.kuboo_client.ui.about.adapter.FaqAdapter
import com.sethchhim.kuboo_client.ui.about.adapter.LicenseAdapter
import com.sethchhim.kuboo_client.ui.log.adapter.LogAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserPathAdapter
import com.sethchhim.kuboo_client.ui.main.downloads.adapter.DownloadListAdapter
import com.sethchhim.kuboo_client.ui.main.home.adapter.LatestAdapter
import com.sethchhim.kuboo_client.ui.main.home.adapter.RecentAdapter
import com.sethchhim.kuboo_client.ui.main.login.adapter.LoginAdapter
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragmentImp0_View
import com.sethchhim.kuboo_client.ui.reader.comic.adapter.ReaderComicAdapter
import com.sethchhim.kuboo_client.ui.reader.pdf.adapter.ReaderPdfAdapter
import com.sethchhim.kuboo_client.ui.scope.AppScope
import com.sethchhim.kuboo_client.ui.splash.preload.Task_PreloadBase
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@AppScope
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (InjectorsModule::class)])
interface AppComponent : AndroidInjector<BaseApplication> {

    override fun inject(baseApplication: BaseApplication)

    fun inject(aboutPagerAdapter: AboutPagerAdapter)
    fun inject(browserContentAdapter: BrowserContentAdapter)
    fun inject(browserPathAdapter: BrowserPathAdapter)
    fun inject(downloadListAdapter: DownloadListAdapter)
    fun inject(faqAdapter: FaqAdapter)
    fun inject(fetchRepository: FetchRepository)
    fun inject(glideLocalFetcher: GlideLocalFetcher)
    fun inject(glideModule: GlideModule)
    fun inject(glidePassthroughFetcher: GlidePassthroughFetcher)
    fun inject(glideRemoteFetcher: GlideRemoteFetcher)
    fun inject(intentService: IntentService)
    fun inject(latestAdapter: LatestAdapter)
    fun inject(licenseAdapter: LicenseAdapter)
    fun inject(logAdapter: LogAdapter)
    fun inject(loginAdapter: LoginAdapter)
    fun inject(onClearFromRecentService: OnClearFromRecentService)
    fun inject(taskPreloadBase: Task_PreloadBase)
    fun inject(readerComicAdapter: ReaderComicAdapter)
    fun inject(readerPdfAdapter: ReaderPdfAdapter)
    fun inject(recentAdapter: RecentAdapter)
    fun inject(settingsFragment: SettingsFragmentImp0_View)
    fun inject(task_LocalBase: Task_LocalBase)
    fun inject(trackingService: TrackingService)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }

}