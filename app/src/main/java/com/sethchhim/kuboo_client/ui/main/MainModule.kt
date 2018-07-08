package com.sethchhim.kuboo_client.ui.main

import com.sethchhim.kuboo_client.ui.main.browser.BrowserBaseFragment
import com.sethchhim.kuboo_client.ui.main.browser.BrowserRecentFragment
import com.sethchhim.kuboo_client.ui.main.browser.BrowserRemoteFragment
import com.sethchhim.kuboo_client.ui.main.browser.BrowserSeriesFragment
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragment
import com.sethchhim.kuboo_client.ui.main.home.HomeFragment
import com.sethchhim.kuboo_client.ui.main.login.browser.LoginBrowserFragment
import com.sethchhim.kuboo_client.ui.main.login.edit.LoginEditFragment
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragment
import com.sethchhim.kuboo_client.ui.scope.MainScope
import com.sethchhim.kuboo_client.ui.state.FailFragment
import com.sethchhim.kuboo_client.ui.state.LoadingFragment
import com.sethchhim.kuboo_client.ui.state.WelcomeFragment
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    @MainScope
    fun provideBrowserBaseFragment() = BrowserBaseFragment()

    @Provides
    @MainScope
    fun provideBrowserRemoteFragment() = BrowserRemoteFragment()

    @Provides
    @MainScope
    fun provideBrowserRecentFragment() = BrowserRecentFragment()

    @Provides
    @MainScope
    fun provideBrowserSeriesFragment() = BrowserSeriesFragment()

    @Provides
    @MainScope
    fun provideDownloadFragment() = DownloadsFragment()


    @Provides
    @MainScope
    fun provideFailFragment() = FailFragment()

    @Provides
    @MainScope
    fun provideLoadingFragment() = LoadingFragment()

    @Provides
    @MainScope
    fun provideLoginBrowserFragment() = LoginBrowserFragment()

    @Provides
    @MainScope
    fun provideLoginEditFragment() = LoginEditFragment()

    @Provides
    @MainScope
    fun provideHomeFragment() = HomeFragment()

    @Provides
    @MainScope
    fun provideSettingsFragment() = SettingsFragment()

    @Provides
    @MainScope
    fun provideWelcomeFragment() = WelcomeFragment()

}