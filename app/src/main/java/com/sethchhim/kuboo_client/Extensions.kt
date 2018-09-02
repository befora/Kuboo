package com.sethchhim.kuboo_client

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.sethchhim.kuboo_client.data.model.Favorite
import com.sethchhim.kuboo_client.data.model.PageUrl
import com.sethchhim.kuboo_client.data.model.Recent
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType
import com.sethchhim.kuboo_client.ui.state.LoadingFragment
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Neighbors
import com.tonyodev.fetch2.Download
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.net.URL
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Extensions {

    internal fun Any.identify() = System.identityHashCode(this)

    internal fun AlertDialog.setMessageTextSize(size: Float) {
        findViewById<TextView>(android.R.id.message)?.textSize = size
    }

    private fun Double.limitDecimalTwo() = DecimalFormat("#.00").format(this)

    internal fun Download.isMatch(download: Download) = this.id == download.id

    internal fun Download.isMatchSeries(download: Download) = this.group == download.group

    internal fun Float.convertPixelsToDp(): Float {
        val metrics = Resources.getSystem().displayMetrics
        val dp = this / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

    internal fun Float.convertDpToPixel(): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = this * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    internal fun FragmentManager.show(fragment: Fragment, containerViewId: Int) {
        fragment.retainInstance = true
        val uniqueTag = fragment.javaClass.simpleName
        val backStateName = fragment.javaClass.name
        val isFragmentPopped = try {
            popBackStackImmediate(backStateName, 0)
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
        val isFragmentExist = findFragmentByTag(uniqueTag) != null
        if (!isFragmentPopped) {
            val isLoadingFragment = fragment is LoadingFragment
            if (isLoadingFragment) {
                Timber.i("loadingFragment: name[$uniqueTag]  exists[$isFragmentExist]")
                beginTransaction()
                        .replace(containerViewId, fragment, uniqueTag)
                        .commitAllowingStateLoss()
            } else {
                Timber.i("replaceFragment: name[$uniqueTag] exists[$isFragmentExist]")
                beginTransaction()
                        .replace(containerViewId, fragment, uniqueTag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(uniqueTag)
                        .commit()
            }
        } else {
            Timber.i("showFragment: name[$uniqueTag] exists[$isFragmentExist]")
            findFragmentByTag(uniqueTag)?.let {
                beginTransaction()
                        .show(it)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit()
            }
        }
    }

    internal fun FragmentManager.getVisibleFragment(): Fragment {
        fragments.forEach {
            if (it.isVisible) return it
        }
        Timber.e("Can not find visible fragment!")
        return Fragment()
    }

    internal fun FloatingActionButton.showDelayed() {
        if (!isShown) {
            launch(UI) {
                delay(500, TimeUnit.MILLISECONDS)
                try {
                    show()
                } catch (e: RuntimeException) { //ignore
                }
            }
        }
    }

    internal fun Guideline.setGuidePercent(percentage: Float) {
        val layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams.guidePercent = percentage
        setLayoutParams(layoutParams)
    }

    internal fun ImageView.colorFilterNull() = colorFilter?.let { clearColorFilter() }

    internal fun ImageView.colorFilterRed() = setColorFilter(Color.RED, PorterDuff.Mode.DARKEN)

    internal fun ImageView.colorFilterGrey() = setColorFilter(ContextCompat.getColor(context, R.color.md_grey_900), PorterDuff.Mode.MULTIPLY)

    internal fun ImageView.colorFilterWhite() = setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)

    internal fun ImageView.colorFilterBlack() = setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)

    internal fun Int.isEven() = (this % 2) == 0

    internal fun Int.isOdd() = !isEven()

    internal fun Int.minutesToMilliseconds() = TimeUnit.MINUTES.toMillis(this.toLong())

    internal fun Int.toMinimumTwoDigits(): String {
        val numberFormat = NumberFormat.getInstance(Locale.US)
        numberFormat.minimumIntegerDigits = 2
        return numberFormat.format(this)
    }

    internal fun Login.isMatch(login: Login): Boolean {
        return nickname == login.nickname
                && server == login.server
                && username == login.username
                && password == login.password
    }

    internal fun Long.toHourMinuteSecond() = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)))

    internal fun List<Book>.compressFavorite(): MutableList<Book> {
        val favoriteCompressedList = mutableListOf<Book>()
        this
                .sortedWith(compareBy({ it.getXmlId() }, { it.id }))
                .forEach {
                    val isFavorite = it.isFavorite
                    val isNotContainsSeries = !favoriteCompressedList.containsSeries(it.getXmlId())
                    when (isFavorite) {
                        true -> if (isNotContainsSeries) favoriteCompressedList.add(it)
                        false -> favoriteCompressedList.add(it)
                    }
                }
        return favoriteCompressedList
    }

    internal fun List<Download>.filteredBySeries(download: Download) = this.filter { it.group == download.group }.sortedBy { it.tag }

    internal fun List<com.sethchhim.kuboo_client.data.model.Download>.downloadListToBookList(): List<Book> {
        val bookList = mutableListOf<Book>()
        forEach { bookList.add(it.toBook()) }
        return bookList
    }

    internal fun List<Neighbors>.printNeighbors() = forEachIndexed { index, neighbors ->
        Timber.d("index[$index] previous[${neighbors.previousBook}}] current[${neighbors.currentBook}}] next[${neighbors.nextBook}]")
    }

    internal fun List<PageUrl>.printPageUrls() = forEach {
        Timber.d("index[${it.index}] page0[${it.page0}] page1[${it.page1}]")
    }

    internal fun List<Recent>.filterServer(login: Login): List<Recent> {
        if (login.isEmpty()) return this

        val filteredList = mutableListOf<Recent>()
        forEach {
            val isMatch = it.server == login.server
            if (isMatch) filteredList.add(it)
        }
        return filteredList
    }

    internal fun List<Recent>.recentListToBookList(): List<Book>? {
        val bookList = mutableListOf<Book>()
        forEach { bookList.add(it.toBook()) }
        return bookList
    }

    internal fun List<Favorite>.favoriteListToBookList(): List<Book>? {
        val bookList = mutableListOf<Book>()
        forEach { bookList.add(it.toBook()) }
        return bookList
    }

    internal fun Long.millisecondsToSeconds() = TimeUnit.MILLISECONDS.toSeconds(this)

    internal fun Long.toReadable() = when {
        this >= 1024 * 1024 * 1024 -> "${(((this / 1024) / 1024) / 1024.00).limitDecimalTwo()} GB"
        this >= 1024 * 1024 -> "${((this / 1024) / 1024.00).limitDecimalTwo()} MB"
        else -> "${(this / 1024.00).limitDecimalTwo()} KB"
    }

    internal fun <T> LiveData<T>.removeAllObservers(lifecycleOwner: LifecycleOwner) {
        if (hasObservers()) removeObservers(lifecycleOwner)
    }

    internal fun MenuItem.gone() {
        if (isVisible) isVisible = false
    }

    internal fun MenuItem.visible() {
        if (!isVisible) isVisible = true
    }

    internal fun MenuItem.setStateEnabled(context: Context) {
        val colorGreen = ContextCompat.getColor(context, R.color.md_green_500)
        icon?.apply {
            clearColorFilter()
            mutate()
            setColorFilter(colorGreen, PorterDuff.Mode.SRC_ATOP)
        }
        isEnabled = true
    }

    internal fun MenuItem.setStateDisabled(context: Context) {
        val colorGrey = ContextCompat.getColor(context, R.color.md_grey_500)
        icon?.apply {
            clearColorFilter()
            mutate()
            setColorFilter(colorGrey, PorterDuff.Mode.SRC_ATOP)
        }
        isEnabled = true
    }

    internal fun MutableList<Book>.containsSeries(xmlId: Int): Boolean {
        forEach {
            if (it.getXmlId() == xmlId) return true
        }
        return false
    }

    internal fun MutableList<Book>.filteredBySeries(book: Book): MutableList<Book> {
        val xmlId = book.getXmlIdString()
        val filteredList = mutableListOf<Book>()
        forEach {
            val eachXmlId = it.getXmlIdString()
            if (eachXmlId == xmlId) filteredList.add(it)
        }
        filteredList.sortBy { it.id }
        return filteredList
    }

    internal fun MutableList<Download>.getFirstInSeries(download: Download): Download {
        filteredBySeries(download).apply {
            if (isNotEmpty()) return get(0)
        }
        return download
    }

    internal fun MutableList<Download>.isFirstInSeries(download: Download): Boolean {
        filteredBySeries(download).apply {
            val firstItem = get(0)
            if (isNotEmpty() && download == firstItem) return true
        }
        return false
    }

    internal fun String.guessFilename(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this)
        return URLUtil.guessFileName(this, null, fileExtension)
    }

    internal fun String.toGlideUrl(login: Login): GlideUrl {
        val authName = "Authorization"
        val authValue = "Basic " + Base64.encodeToString(("${login.username}:${login.password}").toByteArray(), Base64.NO_WRAP)
        return GlideUrl(this, LazyHeaders.Builder()
                .addHeader(authName, authValue)
                .build())
    }

    internal fun SwipeRefreshLayout.disable() {
        if (isEnabled) isEnabled = false
    }

    internal fun SwipeRefreshLayout.enable() {
        if (!isEnabled) isEnabled = true
    }

    internal fun SwipeRefreshLayout.dismissDelayed() {
        if (isRefreshing) {
            launch(UI) {
                delay(400, TimeUnit.MILLISECONDS)
                try {
                    isRefreshing = false
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

    internal fun View.fadeInvisible() {
        val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
        fadeOutAnimation.duration = 300
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        startAnimation(fadeOutAnimation)
    }

    internal fun View.visible() {
        if (visibility != View.VISIBLE) visibility = View.VISIBLE
    }

    internal fun View.isVisible() = visibility == View.VISIBLE

    internal fun View.gone() {
        if (visibility != View.GONE) visibility = View.GONE
    }

    internal fun View.invisible() {
        if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
    }

    internal fun View.fadeVisible(duration: Long = 400) {
        if (!isShown) {
            alpha = 1.0f
            visibility = View.VISIBLE

            val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation.duration = duration
            startAnimation(fadeInAnimation)
        }
    }

    internal fun View.fadeGone(duration: Long = 400) {
        val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
        fadeOutAnimation.duration = duration
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        startAnimation(fadeOutAnimation)
    }

    internal fun View.setConstraintBottomToTopOf(view: View) {
        var layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams = ConstraintLayout.LayoutParams(layoutParams)
        layoutParams.bottomToTop = view.id
        this.layoutParams = layoutParams
    }

    internal fun View.setConstraintEndToStartOf(view: View) {
        var layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams = ConstraintLayout.LayoutParams(layoutParams)
        layoutParams.endToStart = view.id
        this.layoutParams = layoutParams
    }

    internal fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    internal fun rotateFabOriginal(fab: FloatingActionButton) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(fab).rotation(0f).withLayer().setDuration(300).setInterpolator(interpolator).start()
    }

    internal fun rotateFabLoading(fab: FloatingActionButton) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(fab).rotation(135f).withLayer().setDuration(300).setInterpolator(interpolator).start()
    }

    internal fun Book.getBrowserContentType() =
            when (isBrowserMediaType()) {
                true -> when (Settings.BROWSER_MEDIA_FORCE_LIST) {
                    true -> BrowserContentType.MEDIA_FORCE_LIST
                    false -> BrowserContentType.MEDIA
                }
                false -> BrowserContentType.FOLDER
            }

    internal fun Book.isBrowserMediaType(): Boolean {
        return linkSubsection.endsWith(Constants.URL_PATH_ALL, ignoreCase = true)
                || linkSubsection.endsWith(Constants.URL_PATH_LATEST, ignoreCase = true)
                || linkSubsection.endsWith(Constants.URL_PATH_GRID_DIRECTORY, ignoreCase = true)
                || linkSubsection.contains(Constants.URL_PATH_GRID_DIRECTORY_INDEX, ignoreCase = true)
                || linkSubsection.contains(Constants.URL_PATH_SEARCH, ignoreCase = true)
    }

    internal fun Book.isFileType() = linkAcquisition.isNotEmpty()

    internal fun Book.toFavorite(): Favorite {
        val favorite = Favorite()
        favorite.autoId = this.autoId
        favorite.id = this.id
        favorite.title = this.title
        favorite.author = this.author
        favorite.content = this.content
        favorite.linkAcquisition = this.linkAcquisition
        favorite.linkSubsection = this.linkSubsection
        favorite.linkThumbnail = this.linkThumbnail
        favorite.linkXmlPath = this.linkXmlPath
        favorite.linkPrevious = this.linkPrevious
        favorite.linkNext = this.linkNext
        favorite.linkPse = this.linkPse
        favorite.currentPage = this.currentPage
        favorite.totalPages = this.totalPages
        favorite.server = this.server
        favorite.bookMark = this.bookMark
        favorite.isFavorite = this.isFavorite
        favorite.isFinished = this.isFinished
        favorite.timeAccessed = this.timeAccessed
        favorite.filePath = this.filePath
        return favorite
    }

    internal fun Book.toDownload(): com.sethchhim.kuboo_client.data.model.Download {
        val download = com.sethchhim.kuboo_client.data.model.Download()
        download.autoId = this.autoId
        download.id = this.id
        download.title = this.title
        download.author = this.author
        download.content = this.content
        download.linkAcquisition = this.linkAcquisition
        download.linkSubsection = this.linkSubsection
        download.linkThumbnail = this.linkThumbnail
        download.linkXmlPath = this.linkXmlPath
        download.linkPrevious = this.linkPrevious
        download.linkNext = this.linkNext
        download.linkPse = this.linkPse
        download.currentPage = this.currentPage
        download.totalPages = this.totalPages
        download.server = this.server
        download.bookMark = this.bookMark
        download.isFavorite = this.isFavorite
        download.isFinished = this.isFinished
        download.timeAccessed = this.timeAccessed
        download.filePath = this.filePath
        return download
    }

    internal fun Book.toRecent(): Recent {
        val recent = Recent()
        recent.autoId = this.autoId
        recent.id = this.id
        recent.title = this.title
        recent.author = this.author
        recent.content = this.content
        recent.linkAcquisition = this.linkAcquisition
        recent.linkSubsection = this.linkSubsection
        recent.linkThumbnail = this.linkThumbnail
        recent.linkXmlPath = this.linkXmlPath
        recent.linkPrevious = this.linkPrevious
        recent.linkNext = this.linkNext
        recent.linkPse = this.linkPse
        recent.currentPage = this.currentPage
        recent.totalPages = this.totalPages
        recent.server = this.server
        recent.bookMark = this.bookMark
        recent.isFavorite = this.isFavorite
        recent.isFinished = this.isFinished
        recent.timeAccessed = this.timeAccessed
        recent.filePath = this.filePath
        return recent
    }

    internal fun com.sethchhim.kuboo_client.data.model.Download.toBook(): Book {
        val book = Book()
        book.autoId = this.autoId
        book.id = this.id
        book.title = this.title
        book.author = this.author
        book.content = this.content
        book.linkAcquisition = this.linkAcquisition
        book.linkSubsection = this.linkSubsection
        book.linkThumbnail = this.linkThumbnail
        book.linkXmlPath = this.linkXmlPath
        book.linkPrevious = this.linkPrevious
        book.linkNext = this.linkNext
        book.linkPse = this.linkPse
        book.currentPage = this.currentPage
        book.totalPages = this.totalPages
        book.server = this.server
        book.bookMark = this.bookMark
        book.isFavorite = this.isFavorite
        book.isFinished = this.isFinished
        book.timeAccessed = this.timeAccessed
        book.filePath = this.filePath
        return book
    }

    internal fun Favorite.toBook(): Book {
        val book = Book()
        book.autoId = this.autoId
        book.id = this.id
        book.title = this.title
        book.author = this.author
        book.content = this.content
        book.linkAcquisition = this.linkAcquisition
        book.linkSubsection = this.linkSubsection
        book.linkThumbnail = this.linkThumbnail
        book.linkXmlPath = this.linkXmlPath
        book.linkPrevious = this.linkPrevious
        book.linkNext = this.linkNext
        book.linkPse = this.linkPse
        book.currentPage = this.currentPage
        book.totalPages = this.totalPages
        book.server = this.server
        book.bookMark = this.bookMark
        book.isFavorite = this.isFavorite
        book.isFinished = this.isFinished
        book.timeAccessed = this.timeAccessed
        book.filePath = this.filePath
        return book
    }

    internal fun Recent.toBook(): Book {
        val book = Book()
        book.autoId = this.autoId
        book.id = this.id
        book.title = this.title
        book.author = this.author
        book.content = this.content
        book.linkAcquisition = this.linkAcquisition
        book.linkSubsection = this.linkSubsection
        book.linkThumbnail = this.linkThumbnail
        book.linkXmlPath = this.linkXmlPath
        book.linkPrevious = this.linkPrevious
        book.linkNext = this.linkNext
        book.linkPse = this.linkPse
        book.currentPage = this.currentPage
        book.totalPages = this.totalPages
        book.server = this.server
        book.bookMark = this.bookMark
        book.isFavorite = this.isFavorite
        book.isFinished = this.isFinished
        book.timeAccessed = this.timeAccessed
        book.filePath = this.filePath
        return book
    }

}