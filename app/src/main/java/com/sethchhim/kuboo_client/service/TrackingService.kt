package com.sethchhim.kuboo_client.service

import androidx.work.*
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Constants.KEY_LOGIN_NICKNAME
import com.sethchhim.kuboo_client.Constants.KEY_LOGIN_PASSWORD
import com.sethchhim.kuboo_client.Constants.KEY_LOGIN_SERVER
import com.sethchhim.kuboo_client.Constants.KEY_LOGIN_USERNAME
import com.sethchhim.kuboo_client.Constants.TAG_TRACKING_SERVICE
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrackingService : Worker() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    override fun doWork(): Result {
        val login = Login().apply {
            nickname = inputData.getString(KEY_LOGIN_NICKNAME) ?: ""
            server = inputData.getString(KEY_LOGIN_SERVER) ?: ""
            username = inputData.getString(KEY_LOGIN_USERNAME) ?: ""
            password = inputData.getString(KEY_LOGIN_PASSWORD) ?: ""
        }
        startOneTimeTrackingService(login)
        return Result.SUCCESS
    }

    internal fun startPeriodicTrackingService() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        val login = viewModel.getActiveLogin()
        val inputData = Data.Builder()
                .putString(KEY_LOGIN_NICKNAME, login.nickname)
                .putString(KEY_LOGIN_SERVER, login.server)
                .putString(KEY_LOGIN_USERNAME, login.username)
                .putString(KEY_LOGIN_PASSWORD, login.password)
                .build()
        val trackingWork = PeriodicWorkRequest
                .Builder(TrackingService::class.java, Settings.DOWNLOAD_TRACKING_INTERVAL.toLong(), TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(TAG_TRACKING_SERVICE, ExistingPeriodicWorkPolicy.REPLACE, trackingWork)
    }

    internal fun startOneTimeTrackingService(login: Login) {
        viewModel.getDownloadListFavoriteCompressedFromDao().observeForever {
            it?.let {
                it
                        .filter { it.isFavorite }
                        .forEach {
                            viewModel.deleteDownloadsBefore(it)
                            startTrackingByBook(login, it)
                        }
                kubooRemote.resumeAll()
            }
        }
    }

    internal fun startTrackingByBook(login: Login, book: Book) {
        val isBookServerMatchActiveServer = login.server == book.server
        if (isBookServerMatchActiveServer) {
            Timber.d("Tracking of book start: title[${book.title}] isBookServerMatchActiveServer[$isBookServerMatchActiveServer]")
            val startTime = System.currentTimeMillis()
            viewModel.getSeriesNeighborsRemote(login, book, book.server + book.linkXmlPath, Settings.DOWNLOAD_TRACKING_LIMIT).observeForever {
                it?.let { result ->
                    val mutableResult = result.apply { forEach { it.isFavorite = true } }.toMutableList()
                    handleResult(login, mutableResult, book, startTime)
                }
                        ?: Timber.e("Tracking of book failed to getSeriesNeighborsRemote: title[${book.title}]")
            }
        } else {
            Timber.w("Tracking of book failed to match active server: title[${book.title}] isBookServerMatchActiveServer[$isBookServerMatchActiveServer]")
        }
    }

    private fun handleResult(login: Login, seriesNeighbors: MutableList<Book>, book: Book, startTime: Long) {
        val isRequireNextPage = seriesNeighbors.size < Settings.DOWNLOAD_TRACKING_LIMIT && book.linkNext.isNotEmpty()
        when (isRequireNextPage) {
            true -> getRemainingSeriesNeighbors(login, seriesNeighbors, book, startTime)
            false -> handleResultFinal(login, book, seriesNeighbors, startTime)
        }
    }

    private fun getRemainingSeriesNeighbors(login: Login, seriesNeighbors: MutableList<Book>, book: Book, startTime: Long) {
        val remainingCount = Settings.DOWNLOAD_TRACKING_LIMIT - seriesNeighbors.size
        viewModel.getSeriesNeighborsNextPageRemote(login, book.server + book.linkNext, seriesLimit = remainingCount).observeForever {
            it?.let { result ->
                result.forEach { it.isFavorite = true }
                seriesNeighbors.addAll(result)
                val firstItem = try {
                    result[0]
                } catch (e: IndexOutOfBoundsException) {
                    book
                }
                handleResult(login, seriesNeighbors, firstItem, startTime)
            }
                    ?: Timber.e("Tracking of book failed to getSeriesNeighborsNextPageRemote: title[${book.title}]")
        }
    }

    private fun handleResultFinal(login: Login, book: Book, seriesNeighbors: MutableList<Book>, startTime: Long) {
        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.d("Tracking of book finished.  title[${book.title}] [$elapsedTime ms]")
        if (seriesNeighbors.isNotEmpty()) viewModel.startDownloads(login, seriesNeighbors, Settings.DOWNLOAD_SAVE_PATH)
    }

}