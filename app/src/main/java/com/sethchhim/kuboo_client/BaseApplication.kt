package com.sethchhim.kuboo_client

import com.sethchhim.kuboo_client.di.AppComponent
import com.sethchhim.kuboo_client.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out BaseApplication> {
        appComponent = DaggerAppComponent
                .builder()
                .context(this)
                .build()
        return appComponent
    }

    companion object {
        lateinit var appComponent: AppComponent
    }

}