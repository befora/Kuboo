package com.sethchhim.kuboo_client

import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.di.AppComponent
import com.sethchhim.kuboo_client.di.DaggerAppComponent
import com.sethchhim.kuboo_client.service.OnClearFromRecentService
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import com.tonyodev.fetch2.Download
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
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
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            if (viewModel.isLoginListEmpty()) {
                viewModel.addLogin(Login(nickname = "Books Server",
                        server = "http://192.168.1.100:2202/opds-books/",
                        username = "",
                        password = ""))

                viewModel.addLogin(Login(nickname = "Comics Server",
                        server = "http://192.168.1.100:2202/opds-comics/",
                        username = "",
                        password = ""))
            }
        }

        //on application exit service
        startService(Intent(this, OnClearFromRecentService::class.java))

        //restore settings
        sharedPrefsHelper.restoreSettings()

        //restore favorite items
        viewModel.getFavoriteListFromDao().observeForever { result ->
            result?.let {
                Timber.i("Loading favorite items: size[${it.size}]")
                viewModel.setFavoriteList(it)
            }
        }

        //restore downloaded items
        viewModel.getDownloadListFromService(MutableLiveData<List<Download>>().apply {
            observeForever { result ->
                result?.let {
                    Timber.i("Loading downloaded items: size[${it.size}]")
                    viewModel.setDownloadList(it)
                }
            }
        })
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}