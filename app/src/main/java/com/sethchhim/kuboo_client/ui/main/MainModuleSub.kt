package com.sethchhim.kuboo_client.ui.main

import com.sethchhim.kuboo_client.ui.main.browser.*
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragment
import com.sethchhim.kuboo_client.ui.main.home.HomeFragment
import com.sethchhim.kuboo_client.ui.main.login.browser.LoginBrowserFragment
import com.sethchhim.kuboo_client.ui.main.login.edit.LoginEditFragment
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragment
import com.sethchhim.kuboo_client.ui.main.settings.advanced.SettingsAdvancedFragment
import com.sethchhim.kuboo_client.ui.scope.HomeScope
import com.sethchhim.kuboo_client.ui.state.FailFragment
import com.sethchhim.kuboo_client.ui.state.LoadingFragment
import com.sethchhim.kuboo_client.ui.state.WelcomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainModuleSub {

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun downloadFragment(): DownloadsFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun failFragment(): FailFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun loginBrowserFragment(): LoginBrowserFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun loginEditFragment(): LoginEditFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun loadingFragment(): LoadingFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun recentFragment(): HomeFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun settingsFragment(): SettingsFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun settingsAdvancedFragment(): SettingsAdvancedFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun welcomeFragment(): WelcomeFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun browserLatestFragment(): BrowserLatestFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun browserRecentFragment(): BrowserRecentFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun browserRemoteFragment(): BrowserRemoteFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun browserSearchFragment(): BrowserSearchFragment

    @HomeScope
    @ContributesAndroidInjector
    internal abstract fun browserSeriesFragment(): BrowserSeriesFragment

}