package com.sethchhim.kuboo_client.ui.base

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.isVisible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.enum.ScreenOrientation
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.data.model.copyProgress
import com.sethchhim.kuboo_client.ui.base.custom.LoadingStage
import com.sethchhim.kuboo_client.ui.preview.PreviewActivity
import com.sethchhim.kuboo_client.ui.preview.PreviewActivityLandscape
import com.sethchhim.kuboo_client.ui.preview.PreviewActivityPortrait
import com.sethchhim.kuboo_client.ui.reader.book.ReaderEpubActivity
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivity
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivityLandscape
import com.sethchhim.kuboo_client.ui.reader.comic.ReaderComicActivityPortrait
import com.sethchhim.kuboo_client.ui.reader.pdf.ReaderPdfActivity
import com.sethchhim.kuboo_remote.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("Registered")
open class BaseActivityImpl2_Read : BaseActivityImpl1_Dialog() {

    internal fun startPreview(readData: ReadData) {
        val isNotBannedFromPreview = !readData.book.isBannedFromPreview()
        when (Settings.PREVIEW && isNotBannedFromPreview) {
            true -> startPreviewActivity(readData)
            false -> startReader(readData)
        }
    }

    internal fun startReader(readData: ReadData) {
        showLoadingDialog(loadingStage = LoadingStage.PING)

        //If remote book, ping for response before starting reader activity.
        val isDownload = readData.source == Source.DOWNLOAD
        when (isDownload) {
            true -> when (readData.book.isLocalValid()) {
                true -> startPreload(readData)
                false -> {
                    hideLoadingDialog()
                    showToastFileDoesNotExist()
                }
            }
            false -> viewModel.pingServer(readData.book.server).observe(this, Observer { result ->
                isLoadingRequired = false

                when (result?.isSuccessful ?: false) {
                    true -> startBookmarkSearch(readData)
                    false -> {
                        hideLoadingDialog()
                        showToastError()
                    }
                }
            })
        }
    }

    private fun startReaderActivity(readData: ReadData) {
        readData.apply {
            isLoadingRequired = false
            hideBookmarkDialog()

            val readerClass = book.getReaderClass()
            when (readerClass == null) {
                true -> showToastFileTypeNotSupported()
                false -> {
                    val intent = Intent(this@BaseActivityImpl2_Read, readerClass).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(Constants.ARG_BOOK, book)
                        putExtra(Constants.ARG_SOURCE, readData.source)
                        putExtra(Constants.ARG_TRANSITION_URL, sharedElement?.transitionName)
                    }

                    val isNotBannedFromTransition = !book.isBannedFromTransition()
                    val isSharedElementValid = sharedElement?.isVisible() ?: false
                    when (isNotBannedFromTransition && isSharedElementValid) {
                        true -> GlobalScope.launch(Dispatchers.Main) {
                            delay(300)
                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@BaseActivityImpl2_Read, sharedElement!!, sharedElement!!.transitionName)
                            startActivity(intent, options.toBundle())
                        }
                        false -> this@BaseActivityImpl2_Read.startActivity(intent)
                    }
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                delay(500)
                hideLoadingDialog()
            }
        }
    }

    private fun startPreviewActivity(readData: ReadData) {
        readData.apply {
            val previewActivity = getPreviewClass()
            val intent = Intent(this@BaseActivityImpl2_Read, previewActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(Constants.ARG_BOOK, book)
                putExtra(Constants.ARG_TRANSITION_URL, sharedElement?.transitionName)
            }

            sharedElement?.let {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@BaseActivityImpl2_Read, it, it.transitionName)
                startActivity(intent, options.toBundle())
            } ?: this@BaseActivityImpl2_Read.startActivity(intent)
        }
    }

    private fun startBookmarkSearch(readData: ReadData) {
        when (readData.bookmarksEnabled) {
            true -> searchForLocalBookmark(readData)
            false -> startPreload(readData)
        }
    }

    private fun searchForLocalBookmark(readData: ReadData) {
        showLoadingDialog(loadingStage = LoadingStage.BOOKMARK)

        //Bookmark Stage 1: Search for local bookmark
        when (readData.book.isComic()) {
            true ->
                //search for recent item that is in the same series
                viewModel.getRecentByXmlId(readData.book, filterByActiveServer = true).observe(this, Observer { result ->
                    if (!isLoadingCancelled) handleLocalBookmark(readData, result)
                })
            false ->
                //search for recent item that is exact book
                viewModel.getRecentByBook(readData.book, filterByActiveServer = true).observe(this, Observer { result ->
                    if (!isLoadingCancelled) handleLocalBookmark(readData, result)
                })
        }
    }

    private fun handleLocalBookmark(readData: ReadData, localBookmark: Book?) {
        val isDownload = readData.source == Source.DOWNLOAD
        when (isDownload) {
            true -> startPreload(when (localBookmark != null) {
                true -> readData.copyProgress(localBookmark)
                false -> readData
            })
            false -> searchForRemoteBookmark(readData, localBookmark)
        }
    }

    private fun searchForRemoteBookmark(readData: ReadData, localBookmark: Book?) = readData.apply {
        when (localBookmark != null) {
            true ->
                //Bookmark Stage 2a: Search for remote bookmark of local bookmark
                viewModel.getRemoteUserApi(localBookmark).observe(this@BaseActivityImpl2_Read, Observer { result ->
                    isLoadingRequired = false

                    if (!isLoadingCancelled) when (result != null) {
                        true -> showBookmarkDialog(this, result)
                        false -> showBookmarkDialog(this, localBookmark)
                    }
                })
            false ->
                //Bookmark Stage 2b: Search for remote bookmark of selected book
                viewModel.getRemoteUserApi(book).observe(this@BaseActivityImpl2_Read, Observer { result ->
                    isLoadingRequired = false

                    if (!isLoadingCancelled) when (result != null) {
                        true -> showBookmarkDialog(this, result)
                        false ->
                            //Bookmark Stage 2c: No remote or local bookmark found, no action required.
                            startPreload(this)
                    }
                })
        }
    }

    private fun showBookmarkDialog(readData: ReadData, savedBook: Book) {
        hideLoadingDialog()
        bookmarkDialog.apply {
            readData.onLoadCallback?.apply { setOnDismissListener { onFinishLoad() } }

            show()

            val textView = findViewById<TextView>(R.id.dialog_layout_bookmark_textView)!!
            val imageView = findViewById<ImageView>(R.id.dialog_layout_bookmark_imageView)!!
            val progressBar = findViewById<ProgressBar>(R.id.dialog_layout_bookmark_progressBar)!!
            val button0 = findViewById<Button>(R.id.dialog_layout_bookmark_button0)!!
            val button1 = findViewById<Button>(R.id.dialog_layout_bookmark_button1)!!

            textView.text = savedBook.title

            val string0 = if (savedBook.isComic() || savedBook.isPdf()) {
                "• ${context.getString(R.string.dialog_resume_page)} ${savedBook.currentPage + 1} ${context.getString(R.string.dialog_of)} ${savedBook.totalPages}"
            } else if (savedBook.isEpub()) {
                val chapter = try {
                    when (savedBook.bookMark.contains("#")) {
                        true -> savedBook.bookMark.substringBeforeLast("#").toInt()
                        false -> -1
                    }
                } catch (e: Exception) {
                    -1
                }
                val progress = try {
                    when (savedBook.bookMark.contains("#")) {
                        true -> (savedBook.bookMark.substringAfterLast("#").toFloat() * 100).toInt()
                        false -> -1
                    }
                } catch (e: Exception) {
                    -1
                }
                "• ${context.getString(R.string.dialog_resume_chapter)} $chapter [$progress%]"
            } else {
                "ERROR"
            }
            val string1 = "• ${context.getString(R.string.dialog_decline_bookmark)}"

            val bookmarkPreviewUrl = savedBook.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)
            imageView.transitionName = bookmarkPreviewUrl

            button0.apply {
                text = string0
                val bookmarkReadData = ReadData(book = savedBook, bookmarksEnabled = readData.bookmarksEnabled, sharedElement = imageView, source = Source.BOOKMARK)
                this.setOnClickListener { onClickBookmarkResume(bookmarkReadData) }
            }

            button1.apply {
                text = string1
                this.setOnClickListener { onClickBookmarkDecline(readData) }
            }

            progressBar.max = savedBook.totalPages
            progressBar.progress = savedBook.currentPage

            Glide.with(this@BaseActivityImpl2_Read)
                    .load(bookmarkPreviewUrl)
                    .apply(RequestOptions()
                            .priority(Priority.IMMEDIATE)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .dontAnimate()
                            .dontTransform())
                    .into(imageView)
        }

        //preload savedBook first image
        glideUtil.preload(context = this, stringUrl = readData.book.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT))
    }

    private fun hideBookmarkDialog() = bookmarkDialog.apply {
        if (isShowing) dismiss()
    }

    private fun onClickBookmarkResume(readData: ReadData) {
        startPreload(readData)
    }

    private fun onClickBookmarkDecline(readData: ReadData) {
        hideBookmarkDialog()
        startPreload(readData.apply {
            if (book.currentPage != 0) book.currentPage = 0
        })
    }

    private fun startPreload(readData: ReadData) {
        showLoadingDialog(loadingStage = LoadingStage.ASSET)

        val isDownload = readData.source == Source.DOWNLOAD
        when (isDownload) {
            true -> startReaderActivity(readData)
            false -> when (readData.book.isComic()) {
                true -> preloadComic(readData)
                false -> preloadBook(readData)
            }
        }
    }

    private fun preloadBook(readData: ReadData) = viewModel.getRemoteFile(readData.book.linkAcquisition, File(cacheDir.path)).observe(this, Observer { result ->
        result?.let {
            readData.book.filePath = it.path
            onPreloadSuccess(readData)
        } ?: onPreloadFailure()
    })

    private fun preloadComic(readData: ReadData) = glideUtil.preload(this, readData.book).observe(this, Observer { result ->
        when (result) {
            true -> onPreloadSuccess(readData)
            false -> onPreloadFailure()
        }
    })

    private fun onPreloadFailure() {
        hideLoadingDialog()
        showToastFailedToLoadImageAssets()
    }

    private fun onPreloadSuccess(readData: ReadData) = GlobalScope.launch(Dispatchers.Main) {
        if (!isLoadingCancelled) {
            hideLoadingDialog()
            startReaderActivity(readData)
        }
    }


    private fun getPreviewClass() = when (Settings.SCREEN_ORIENTATION) {
        ScreenOrientation.PORTRAIT_ONLY.value -> PreviewActivityPortrait::class.java
        ScreenOrientation.LANDSCAPE_ONLY.value -> PreviewActivityLandscape::class.java
        else -> PreviewActivity::class.java
    }

    private fun Book.getReaderClass() = when {
        isComic() -> when (Settings.SCREEN_ORIENTATION) {
            ScreenOrientation.PORTRAIT_ONLY.value -> ReaderComicActivityPortrait::class.java
            ScreenOrientation.LANDSCAPE_ONLY.value -> ReaderComicActivityLandscape::class.java
            else -> ReaderComicActivity::class.java
        }
        isEpub() -> ReaderEpubActivity::class.java
        isPdf() -> ReaderPdfActivity::class.java
        else -> null
    }

}