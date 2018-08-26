package com.sethchhim.kuboo_client

import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.di.AppComponent
import com.sethchhim.kuboo_client.di.DaggerAppComponent
import com.sethchhim.kuboo_client.util.LogUtil
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Login
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class BaseApplication : DaggerApplication() {

    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var logUtil: LogUtil
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
                        server = "https://192.168.1.100:2202/opds-books/",
                        username = "",
                        password = ""))

                viewModel.addLogin(Login(nickname = "Comics Server",
                        server = "https://192.168.1.100:2202/opds-comics/",
                        username = "",
                        password = ""))
            }

            //fake data for log activity
            viewModel.getLogList().observeForever { result ->
                result?.let { if (it.isEmpty()) logUtil.addMockLogData() }
            }
        }

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
        viewModel.getDownloadsListFromAppDatabase().observeForever { result ->
            result?.let {
                Timber.i("Loading downloaded items: size[${it.size}]")
                viewModel.setDownloadList(it)
            }
        }
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}