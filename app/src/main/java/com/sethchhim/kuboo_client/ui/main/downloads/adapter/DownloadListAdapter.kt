package com.sethchhim.kuboo_client.ui.main.downloads.adapter

import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
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
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragmentImpl1_Content
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Status
import kotlinx.android.synthetic.main.browser_item_download.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import timber.log.Timber
import javax.inject.Inject

class DownloadListAdapter(val downloadsFragment: DownloadsFragmentImpl1_Content) : ListAdapter<Book, DownloadListAdapter.ItemViewHolder>(DiffCallback()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject lateinit var context: Context
    @Inject lateinit var systemUtil: SystemUtil

    private val recyclerView = downloadsFragment.contentRecyclerView
    private val mainActivity = downloadsFragment.mainActivity
    private val viewModel = downloadsFragment.viewModel

    internal var list = mutableListOf<Book>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val holder = ItemViewHolder(parent.context.layoutInflater.inflate(R.layout.browser_item_download, parent, false))
        holder.itemView.onClick { holder.onItemSelected() }
        holder.itemView.onLongClick { holder.onItemLongSelected() }
        return holder
    }

    override fun onBindViewHolder(holder: DownloadListAdapter.ItemViewHolder, position: Int) {
        val book = getItem(position)
        viewModel.getFetchDownload(book).observe(downloadsFragment, Observer {
            it?.let { setStateStart(holder, it, book.isFavorite) }
        })
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.browser_item_download_materialBadgeTextView.gone()
    }

    override fun submitList(list: MutableList<Book>?) {
        super.submitList(list)
        list?.let { this.list = list }
    }

    override fun getItemId(position: Int) = getItem(position).id.toLong()

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun onItemSelected() {
            val book = getItem(adapterPosition)
            viewModel.getFetchDownload(book).observe(downloadsFragment, Observer {
                if (it?.status == Status.COMPLETED) mainActivity.startReader(ReadData(book = book, source = Source.DOWNLOAD))
            })
        }

        internal fun onItemLongSelected() {
            val book = getItem(adapterPosition)
            val deleteDialog = mainActivity.dialogUtil.getDialogDownloadItemSettings(mainActivity, book, object : DialogUtil.OnDialogSelect0 {
                override fun onSelect0() {
                    when (book.isFavorite) {
                        true -> onDeleteFavorite()
                        false -> onDeleteSingle()
                    }
                }

                private fun onDeleteSingle() {
                    viewModel.getFetchDownload(book).observe(downloadsFragment, Observer { it?.let { viewModel.deleteFetchDownload(it) } })
                }

                private fun onDeleteFavorite() {
                    viewModel.deleteFetchSeries(book = book, keepBook = false)
                }
            })

            deleteDialog.apply {
                show()
                findViewById<TextView>(android.R.id.message)?.apply { textSize = 10F }
                findViewById<Switch>(R.id.dialog_layout_download_item_settings_switch1)?.apply {
                    setTextColor(mainActivity.getAppThemeTextColor())
                    isChecked = book.isFavorite
                    onCheckedChange { _, isChecked -> onDownloadTrackingToggled(book, isChecked) }
                }
            }
        }

        private fun onDownloadTrackingToggled(book: Book, isChecked: Boolean) {
            book.isFavorite = isChecked

            //update adapter
            viewModel.getFetchDownload(book).observe(downloadsFragment, Observer {
                it?.let { updatePosition(book, it) }
            })

            //update database and trigger download tracking
            viewModel.addDownload(book).observe(downloadsFragment, Observer {
                when (isChecked) {
                    true -> onDownloadTrackingStart(book)
                    false -> onDownloadTrackingStop(book)
                }
            })
        }

        private fun onDownloadTrackingStart(book: Book) {
            viewModel.deleteFetchSeries(book = book, keepBook = true)

            //artificial delay otherwise tracking will not trigger
            launch(UI) {
                delay(1000)
                mainActivity.trackingService.startOneTimeTrackingService(viewModel.getActiveLogin())
            }
        }

        private fun onDownloadTrackingStop(book: Book) {
            viewModel.deleteFetchSeries(book = book, keepBook = true)
        }
    }

    private fun ProgressBar.loadProgress(holder: DownloadListAdapter.ItemViewHolder) {
        val book = try {
            getItem(holder.adapterPosition)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

        book?.let {
            max = it.totalPages - 1
            progress = it.currentPage
            visible()
        }
    }

    private fun ImageView.loadFolderThumbnail(holder: ItemViewHolder, download: Download) {
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

    private fun setStateStart(holder: ItemViewHolder, download: com.tonyodev.fetch2.Download, favorite: Boolean) {
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

    private fun setStateConditional(holder: ItemViewHolder, download: Download, favorite: Boolean = false) {
        when (download.status) {
            Status.QUEUED -> setStateLoading(holder, download, favorite)
            Status.DOWNLOADING -> setStateDownloading(holder, download)
            Status.COMPLETED -> setStateCompleted(holder, download, favorite)
            Status.PAUSED -> setStatePaused(holder, download, favorite)
            Status.CANCELLED -> setStateCancelled(holder, download, favorite)
            else -> if (download.error != Error.NONE) setStateFail(holder, download, favorite)
        }
    }

    private fun setStatePaused(holder: ItemViewHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.visible()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_paused)
        holder.itemView.browser_item_download_textView4.onClick { viewModel.resumeFetchDownload(download) }
    }

    private fun setStateCancelled(holder: ItemViewHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.visible()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_cancelled)
        holder.itemView.browser_item_download_textView4.onClick { viewModel.retryFetchDownload(download) }
    }

    private fun setStateFail(holder: ItemViewHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.visible()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_materialBadgeTextView.loadCount(download, favorite)

        holder.itemView.browser_item_download_textView2.text = download.error.name
        holder.itemView.browser_item_download_textView3.onClick { viewModel.retryFetchDownload(download) }
    }

    private fun setStateLoading(holder: ItemViewHolder, download: Download, favorite: Boolean) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_queued)
        holder.itemView.browser_item_download_materialBadgeTextView.gone()
    }

    private fun setStateDownloading(holder: ItemViewHolder, download: Download) {
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

    private fun setStateCompleted(holder: ItemViewHolder, download: Download, favorite: Boolean) {
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

    internal fun updatePosition(book: Book, download: com.tonyodev.fetch2.Download) {
        val index = list.indexOf(book)
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(index)
        if (viewHolder != null) setStateConditional(viewHolder as ItemViewHolder, download, book.isFavorite)
    }

    private fun MaterialBadgeTextView.loadCount(download: com.tonyodev.fetch2.Download, favorite: Boolean) {
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
                        false -> gone()
                    }
                }
            })
            false -> gone()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book) = oldItem.isMatch(newItem)
    }

}