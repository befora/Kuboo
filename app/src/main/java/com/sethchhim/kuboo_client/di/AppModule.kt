package com.sethchhim.kuboo_client.di

import android.arch.persistence.room.Room
import android.content.Context
import com.sethchhim.kuboo_client.Constants.DATABASE_NAME
import com.sethchhim.kuboo_client.data.AppDatabase
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.repository.*
import com.sethchhim.kuboo_client.service.IntentService
import com.sethchhim.kuboo_client.service.NotificationService
import com.sethchhim.kuboo_client.ui.scope.AppScope
import com.sethchhim.kuboo_client.util.*
import com.sethchhim.kuboo_local.KubooLocal
import com.sethchhim.kuboo_remote.KubooRemote
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    //<------ Persistent Storage ------>
    @Provides
    @AppScope
    fun provideAppDatabaseDao(context: Context) = Room
            .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
            .appDatabaseDao()

    @Provides
    @AppScope
    fun provideSharedPreferencesHelper(context: Context) = SharedPrefsHelper(context)

    @Provides
    @AppScope
    fun provideViewModel(
            browserRepository: BrowserRepository,
            downloadsRepository: DownloadsRepository,
            favoriteRepository: FavoriteRepository,
            fetchRepository: FetchRepository,
            localRepository: LocalRepository,
            loginRepository: LoginRepository,
            readerRepository: ReaderRepository,
            recentRepository: RecentRepository,
            remoteRepository: RemoteRepository) = ViewModel(browserRepository, downloadsRepository, favoriteRepository, fetchRepository, localRepository, loginRepository, readerRepository, recentRepository, remoteRepository)

    @Provides
    @AppScope
    fun provideBrowserRepository() = BrowserRepository()

    @Provides
    @AppScope
    fun provideDownloadsRepository() = DownloadsRepository()

    @Provides
    @AppScope
    fun provideFavoriteRepository() = FavoriteRepository()

    @Provides
    @AppScope
    fun provideFetchRepository(kubooRemote: KubooRemote, notificationService: NotificationService) = FetchRepository(kubooRemote, notificationService)

    @Provides
    @AppScope
    fun provideLocalRepository(kubooLocal: KubooLocal) = LocalRepository(kubooLocal)

    @Provides
    @AppScope
    fun provideLoginRepository(sharedPrefsHelper: SharedPrefsHelper) = LoginRepository(sharedPrefsHelper)

    @Provides
    @AppScope
    fun provideReaderRepository(kubooLocal: KubooLocal, kubooRemote: KubooRemote) = ReaderRepository(kubooLocal, kubooRemote)

    @Provides
    @AppScope
    fun provideRecentRepository() = RecentRepository()

    @Provides
    @AppScope
    fun provideRemoteRepository(kubooRemote: KubooRemote) = RemoteRepository(kubooRemote)

    //<------ Local ------>
    @Provides
    @AppScope
    fun provideKubooLocal(appExecutors: AppExecutors) = KubooLocal(appExecutors.diskIO, appExecutors.mainThread)

    //<------ Network ------>
    @Provides
    @AppScope
    fun provideKubooApi(context: Context, appExecutors: AppExecutors) = KubooRemote(context, appExecutors.networkIO, appExecutors.mainThread)

    //<------ Service ------>
    @Provides
    @AppScope
    fun provideNotificationService(context: Context, kubooRemote: KubooRemote) = NotificationService(context, kubooRemote)

    @Provides
    @AppScope
    fun provideIntentService() = IntentService()

    //<------ Util ------>
    @Provides
    @AppScope
    fun provideAppExecutors() = AppExecutors()

    @Provides
    @AppScope
    fun provideDialogUtil(context: Context) = DialogUtil(context)

    @Provides
    @AppScope
    fun provideFileUtil() = FileUtil()

    @Provides
    @AppScope
    fun provideGlideUtil() = GlideUtil()

    @Provides
    @AppScope
    fun provideSystemUtil(context: Context) = SystemUtil(context)

    @Provides
    @AppScope
    fun provideViewUtil() = ViewUtil()

}