package com.sethchhim.kuboo_client

import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.di.AppComponent
import com.sethchhim.kuboo_client.di.DaggerAppComponent
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_remote.KubooRemote
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class BaseApplication : DaggerApplication() {

    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var viewModel: ViewModel

    override fun applicationInjector(): AndroidInjector<out BaseApplication> {
        appComponent = DaggerAppComponent
                .builder()
                .context(this)
                .build()
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefsHelper.restoreSettings()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}