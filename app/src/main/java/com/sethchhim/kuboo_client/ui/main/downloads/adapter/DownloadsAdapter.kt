package com.sethchhim.kuboo_client.ui.main.downloads.adapter

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.matrixxun.starry.badgetextview.MaterialBadgeTextView
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Extensions.fadeGone
import com.sethchhim.kuboo_client.Extensions.fadeInvisible
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.filteredBySeries
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.guessFilename
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.Extensions.toReadable
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragmentImpl1_Content
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.DiffUtilHelper
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Status
import kotlinx.android.synthetic.main.browser_item_download.view.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import javax.inject.Inject


class DownloadsAdapter(private val downloadsFragment: DownloadsFragmentImpl1_Content, val viewModel: ViewModel) : BaseQuickAdapter<Book, DownloadsAdapter.DownloadHolder>(R.layout.browser_item_download, viewModel.getDownloadListFavoriteCompressed()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject lateinit var context: Context
    @Inject lateinit var systemUtil: SystemUtil

    private lateinit var recyclerview: RecyclerView
    private val mainActivity = downloadsFragment.mainActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        holder.itemView.onClick { holder.onItemSelected() }
        holder.itemView.onLongClick { holder.onItemLongSelected() }
        return holder
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerview = recyclerView
    }

    override fun convert(holder: DownloadHolder, book: Book) {
        viewModel.getFetchDownload(book).observe(downloadsFragment, Observer {
            it?.let { setStateStart(holder, it, book.isFavorite) }
        })
    }

    override fun getItemId(position: Int) = data[position].id.toLong()

    inner class DownloadHolder(view: View) : BaseViewHolder(view) {
        internal fun onItemSelected() {
            val book = data[adapterPosition]
            viewModel.getFetchDownload(book).observe(downloadsFragment, Observer {
                if (it?.status == Status.COMPLETED) mainActivity.startReader(ReadData(book))
            })
        }

        internal fun onItemLongSelected() {
            val book = data[adapterPosition]
            val deleteDialog = mainActivity.dialogUtil.getDialogDownloadItemSettings(mainActivity, book, object : DialogUtil.OnDialogSelect0 {
                override fun onSelect0() {
                    when (book.isFavorite) {
                        true -> onDeleteFavorite()
                        false -> onDeleteSingle()
                    }
                }

                private fun onDeleteSingle() {
                    viewModel.getFetchDownload(book).observe(downloadsFragment, Observer { it?.let { viewModel.deleteFetchDownload(it) } })
                    viewModel.deleteDownload(book = book, liveData = MutableLiveData<List<Book>>().apply {
                        observe(downloadsFragment, Observer {
                            it?.let { downloadsFragment.handleResult(it) }
                        })
                    })
                }

                private fun onDeleteFavorite() {
                    viewModel.deleteFetchSeries(book = book, keepBook = false)
                    viewModel.deleteDownloadSeries(book = book, keepBook = false, liveData = MutableLiveData<List<Book>>().apply {
                        observe(downloadsFragment, Observer {
                            it?.let { downloadsFragment.handleResult(it) }
                        })
                    })
                }
            })

            deleteDialog.apply {
                show()
                findViewById<TextView>(android.R.id.message)?.apply { textSize = 10F }
                findViewById<Switch>(R.id.dialog_layout_download_item_settings_switch1)?.apply {
                    setTextColor(mainActivity.getAppThemeTextColor())
                    isChecked = book.isFavorite
                    onCheckedChange { _, isChecked ->
                        book.isFavorite = isChecked
                        viewModel.addDownload(book)
                        when (isChecked) {
                            true -> onStartDownloadTracking(book)
                            false -> onStopDownloadTracking(book)
                        }
                    }
                }
            }
        }

        private fun onStartDownloadTracking(book: Book) {
            viewModel.deleteFetchSeries(book = book, keepBook = true)
            viewModel.deleteDownloadSeries(book = book, keepBook = true, liveData = MutableLiveData<List<Book>>().apply {
                observe(downloadsFragment, Observer {
                    it?.let {
                        downloadsFragment.handleResult(it)
                        mainActivity.startSeriesDownload(book)
                    }
                })
            })
        }

        private fun onStopDownloadTracking(book: Book) {
            viewModel.deleteDownloadSeries(book, keepBook = true, liveData = MutableLiveData<List<Book>>().apply {
                observe(downloadsFragment, Observer {
                    it?.let {
                        val firstItem = viewModel.getFirstDownloadInSeries(book)
                        viewModel.getFetchDownload(firstItem).observe(downloadsFragment, Observer { it?.let { updatePosition(firstItem, it) } })
                    }
                })
            })
            mainActivity.stopSeriesDownload(book)
        }
    }

    private fun ProgressBar.loadProgress(holder: DownloadHolder) {
        val book = data[holder.adapterPosition]
        max = book.totalPages
        progress = book.currentPage
        visible()
    }

    private fun ImageView.loadFolderThumbnail(holder: DownloadsAdapter.DownloadHolder, download: Download) {
        val isMatchServer = download.url.contains(viewModel.getActiveServer())
        val requestOptions = RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .onlyRetrieveFromCache(when (isMatchServer) {
                    true -> false
                    false -> true
                })

        val thumbnailStringUrl = download.url.plus("?cover=true")
        Glide.with(downloadsFragment)
                .load(thumbnailStringUrl)
                .apply(requestOptions)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.itemView.browser_item_download_imageView1.fadeVisible()
                        holder.itemView.browser_item_download_imageView2.fadeVisible()
                        holder.itemView.browser_item_download_imageView4.fadeInvisible()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.itemView.browser_item_download_imageView1.fadeGone()
                        holder.itemView.browser_item_download_imageView2.fadeGone()
                        holder.itemView.browser_item_download_imageView3.gone()
                        holder.itemView.browser_item_download_imageView4.fadeVisible()
                        return false
                    }
                })
                .into(this)
    }

    private fun setStateStart(holder: DownloadsAdapter.DownloadHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_imageView4.loadFolderThumbnail(holder, download)
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_progressBar.loadProgress(holder)
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        val fileName = download.url.guessFilename()
        holder.itemView.browser_item_download_textView1.text = fileName
        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_queued)

        setStateConditional(holder, download)
    }

    private fun setStateConditional(holder: DownloadsAdapter.DownloadHolder, download: Download, favorite: Boolean = false) {
        when (download.status) {
            Status.QUEUED -> setStateLoading(holder, download, favorite)
            Status.DOWNLOADING -> setStateDownloading(holder, download)
            Status.COMPLETED -> setStateCompleted(holder, download, favorite)
            Status.PAUSED -> setStatePaused(holder, download, favorite)
            Status.CANCELLED -> setStateCancelled(holder, download, favorite)
            else -> if (download.error != com.tonyodev.fetch2.Error.NONE) setStateFail(holder, download, favorite)
        }
    }

    private fun setStatePaused(holder: DownloadHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.visible()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_paused)
        holder.itemView.browser_item_download_textView4.onClick { viewModel.resumeFetchDownload(download) }
    }

    private fun setStateCancelled(holder: DownloadHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.visible()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_cancelled)
        holder.itemView.browser_item_download_textView4.onClick { viewModel.retryFetchDownload(download) }
    }

    private fun setStateFail(holder: DownloadHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.visible()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        when (download.error) {
            Error.UNKNOWN -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_unknown)
            Error.FILE_NOT_CREATED -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_file_not_created)
            Error.CONNECTION_TIMED_OUT -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_connection_timed_out)
            Error.UNKNOWN_HOST -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_uknown_host)
            Error.HTTP_NOT_FOUND -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_http_not_found)
            Error.WRITE_PERMISSION_DENIED -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_write_permission_denied)
            Error.NO_STORAGE_SPACE -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_no_storage_space)
            Error.NO_NETWORK_CONNECTION -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_no_network_connection)
            Error.EMPTY_RESPONSE_FROM_SERVER -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_empty_response_from_server)
            Error.REQUEST_ALREADY_EXIST -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_request_already_exist)
            Error.DOWNLOAD_NOT_FOUND -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_download_not_found)
            Error.FETCH_DATABASE_ERROR -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_fetch_database_error)
            else -> {
                //ayyy lmao
            }
        }
        holder.itemView.browser_item_download_textView3.onClick { viewModel.retryFetchDownload(download) }
    }

    private fun setStateLoading(holder: DownloadHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)
        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_queued)
        holder.itemView.browser_item_download_materialBadgeTextView.gone()
    }

    private fun setStateDownloading(holder: DownloadHolder, download: Download) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.visible()
        holder.itemView.browser_item_download_numberProgressBar.progress = download.progress
        val downloaded = download.downloaded.toReadable()
        val of = context.getString(R.string.downloads_of)
        val total = download.total.toReadable()
        holder.itemView.browser_item_download_textView2.text = "$downloaded $of $total"
    }

    private fun setStateCompleted(holder: DownloadHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.visible()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)
        val downloaded = download.downloaded.toReadable()
        val of = context.getString(R.string.downloads_of)
        val total = download.total.toReadable()
        holder.itemView.browser_item_download_textView2.text = "$downloaded $of $total"
    }

    internal fun updatePosition(book: Book, download: Download) {
        val index = data.indexOf(book)
        val viewHolder = recyclerview.findViewHolderForAdapterPosition(index)
        if (viewHolder != null) setStateConditional(viewHolder as DownloadHolder, download, book.isFavorite)
    }

    internal fun updateData(result: List<Book>) {
        val diffUtilHelper = DiffUtilHelper(this)
        diffUtilHelper.liveData.observe(downloadsFragment, Observer {
            if (it == true) onDiffUtilUpdateFinished(result)
        })
        diffUtilHelper.updateBookList(data, result)
    }

    private fun onDiffUtilUpdateFinished(result: List<Book>) {
        data.clear()
        data.addAll(result)
    }

    private fun MaterialBadgeTextView.loadCount(download: Download, favorite: Boolean) {
        when (favorite) {
            true -> viewModel.getFetchDownloads().observe(downloadsFragment, Observer {
                it?.let {
                    val count = it
                            .filteredBySeries(download)
                            .filterIndexed { index, download ->
                                val isNotParent = index != 0
                                val isCompleted = download.status == Status.COMPLETED
                                isNotParent && isCompleted
                            }
                            .count()

                    when (count > 0) {
                        true -> {
                            text = "+$count"
                            fadeVisible()
                        }
                        false -> fadeGone()
                    }
                }
            })
            false -> fadeGone()
        }
    }

}
