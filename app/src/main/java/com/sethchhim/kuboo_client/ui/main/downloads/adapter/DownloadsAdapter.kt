package com.sethchhim.kuboo_client.ui.main.downloads.adapter

import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Extensions.fadeGone
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.gone
import com.sethchhim.kuboo_client.Extensions.guessFilename
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.Extensions.toBook
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragmentImpl1_Content
import com.sethchhim.kuboo_client.util.DiffUtilDownloads
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Status
import kotlinx.android.synthetic.main.browser_item_download.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import timber.log.Timber
import javax.inject.Inject

class DownloadsAdapter(private val downloadsFragment: DownloadsFragmentImpl1_Content, val viewModel: ViewModel) : BaseQuickAdapter<Download, DownloadsAdapter.DownloadHolder>(R.layout.browser_item_download, viewModel.getDownloadList()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject lateinit var context: Context
    @Inject lateinit var kubooRemote: KubooRemote
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

    override fun convert(holder: DownloadHolder, item: Download) {
        setStateStart(holder, item)
        loadFolderThumbnail(holder, item)
    }

    override fun getItemId(position: Int): Long {
        return data[position].id.toLong()
    }

    inner class DownloadHolder(view: View) : BaseViewHolder(view) {
        internal fun onItemSelected() = try {
            val download = data[adapterPosition]
            if (download.status == Status.COMPLETED) {
                readBook(download)
            } else {
                //do nothing
            }
        } catch (e: Exception) {
            Timber.e(e.message)
        }

        private fun readBook(download: Download) {
            viewModel.getDownloadBookByUrl(download.url).observe(mainActivity, Observer { result1 ->
                result1?.let {
                    val book = result1.toBook()
                    book.filePath = download.file
                    mainActivity.startReader(ReadData(
                            book = book))
                }
            })
        }

        internal fun onItemLongSelected() {
            //TODO selection mode
            viewModel.deleteDownload(data[adapterPosition])
        }
    }

    private fun loadFolderThumbnail(holder: BaseViewHolder, download: Download) {
        val thumbnailStringUrl = download.url.plus("?cover=true")

        val requestOptions = RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

        Glide.with(downloadsFragment)
                .load(thumbnailStringUrl)
                .apply(requestOptions)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.itemView.browser_item_download_imageView1.fadeVisible()
                        holder.itemView.browser_item_download_imageView2.fadeVisible()
                        holder.itemView.browser_item_download_imageView4.fadeGone()
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
                .into(holder.itemView.browser_item_download_imageView4)
    }

    private fun setStateStart(holder: BaseViewHolder, download: Download) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()

        val fileName = download.request.url.guessFilename()
        holder.itemView.browser_item_download_textView1.text = fileName
        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_queued)

        setStateConditional(holder, download)
    }

    private fun setStateConditional(holder: BaseViewHolder, download: Download) {
        when (download.status) {
            Status.QUEUED -> setStateLoading(holder)
            Status.DOWNLOADING -> setStateDownloading(holder, download)
            Status.COMPLETED -> setStateDone(holder, download)
            Status.PAUSED -> setStatePaused(holder, download)
            Status.CANCELLED -> setStateCancelled(holder, download)
            else -> {
                if (download.error != Error.NONE) setStateFail(holder, download)
            }
        }
    }

    private fun setStatePaused(holder: BaseViewHolder, download: Download) {
        holder.itemView.browser_item_download_textView4.visible()
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()

        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_paused)
        holder.itemView.browser_item_download_textView4.onClick { kubooRemote.resume(download) }
    }

    private fun setStateCancelled(holder: BaseViewHolder, download: Download) {
        holder.itemView.browser_item_download_textView4.visible()
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()

        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_cancelled)
        holder.itemView.browser_item_download_textView4.onClick { kubooRemote.retry(download) }
    }

    private fun setStateFail(holder: BaseViewHolder, download: Download) {
        holder.itemView.browser_item_download_textView3.visible()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()

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
            Error.FETCH_ALREADY_EXIST -> holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_fetch_already_exist)
            else -> {
                //ayyy lmao
            }
        }
        holder.itemView.browser_item_download_textView3.onClick { kubooRemote.retry(download) }
    }

    private fun setStateLoading(holder: BaseViewHolder) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.invisible()
        holder.itemView.browser_item_download_textView2.text = context.getString(R.string.downloads_queued)
    }

    private fun setStateDownloading(holder: BaseViewHolder, download: Download) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.gone()
        holder.itemView.browser_item_download_numberProgressBar.visible()
        holder.itemView.browser_item_download_numberProgressBar.progress = download.progress
        val downloaded = systemUtil.readableFileSize(download.downloaded)
        val of = context.getString(R.string.downloads_of)
        val total = systemUtil.readableFileSize(download.total)
        holder.itemView.browser_item_download_textView2.text = "$downloaded $of $total"
    }

    private fun setStateDone(holder: BaseViewHolder, download: Download) {
        holder.itemView.browser_item_download_textView3.gone()
        holder.itemView.browser_item_download_textView4.gone()
        holder.itemView.browser_item_download_textView5.visible()
        holder.itemView.browser_item_download_numberProgressBar.invisible()

        val downloaded = systemUtil.readableFileSize(download.downloaded)
        val of = context.getString(R.string.downloads_of)
        val total = systemUtil.readableFileSize(download.total)
        holder.itemView.browser_item_download_textView2.text = "$downloaded $of $total"
    }

    private fun updatePosition(index: Int, download: Download) {
        val viewHolder = recyclerview.findViewHolderForAdapterPosition(index)
        if (viewHolder != null) setStateConditional(viewHolder as BaseViewHolder, download)
    }

    private fun onDiffUtilUpdateFinished(result: List<Download>) = viewModel.setDownloadList(result)

    internal fun update(result: List<Download>) {
        val sortedResult = mutableListOf<Download>().apply {
            addAll(result)
            sortBy { it.url.guessFilename() }
        }

        val diffUtilHelper = DiffUtilDownloads(this)
        diffUtilHelper.liveData.observe(downloadsFragment, Observer { if (it == true) onDiffUtilUpdateFinished(sortedResult) })
        diffUtilHelper.updateDownloadList(data, sortedResult)


        if (sortedResult.isEmpty()) downloadsFragment.onPopulateContentEmpty()
    }

    internal fun update(download: Download) {
        data.forEachIndexed { index, it ->
            if (it.id == download.id) {
                updatePosition(index, download)
            }
        }
    }

}