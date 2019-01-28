package com.sethchhim.kuboo_client.ui.main.home.adapter

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BR
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.Settings.ROUNDED_CORNERS_VALUE
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.databinding.BrowserItemRecentBinding
import com.sethchhim.kuboo_client.ui.main.home.HomeFragmentImpl1_Content
import com.sethchhim.kuboo_client.ui.main.home.custom.RecentLinearLayoutManager
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.DiffUtilHelper
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import kotlinx.android.synthetic.main.browser_item_recent.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject

class RecentAdapter(private val homeFragmentImpl1Content: HomeFragmentImpl1_Content, val viewModel: ViewModel) : BaseQuickAdapter<Book, RecentAdapter.RecentHolder>(R.layout.browser_item_recent, viewModel.getRecentList()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject
    lateinit var dialogUtil: DialogUtil
    @Inject
    lateinit var kubooRemote: KubooRemote
    @Inject
    lateinit var systemUtil: SystemUtil

    private val mainActivity = homeFragmentImpl1Content.mainActivity
    private val layoutManager = homeFragmentImpl1Content.recentRecyclerView.layoutManager as RecentLinearLayoutManager

    override fun convert(helper: RecentHolder, item: Book) {
        helper.loadImage(item.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT))
        helper.itemView.browser_item_recent_imageView.transitionName = item.getPreviewUrl(Settings.THUMBNAIL_SIZE_RECENT)

        val binding = helper.binding
        binding.updateBook(item)
        binding.remoteSync(helper, item)
    }

    override fun getItemId(position: Int) = data[position].id.toLong()

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<BrowserItemRecentBinding>(homeFragmentImpl1Content.layoutInflater, layoutResId, parent, false)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        holder.itemView.browser_item_recent_imageView.setOnClickListener { holder.onItemSelected() }
        holder.itemView.browser_item_recent_imageView.setOnLongClickListener { holder.onItemLongSelected() }
        return holder
    }

    inner class RecentHolder(view: View) : BaseViewHolder(view) {
        val binding = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as BrowserItemRecentBinding

        init {
            view.browser_item_recent_textView.setOnClickListener { openSeries() }
        }

        internal fun onItemSelected() {
            startReadAt(adapterPosition, itemView)
        }

        internal fun onItemLongSelected(): Boolean {
            showRemoveDialog(adapterPosition)
            return true
        }

        internal fun setStateValid() {
            itemView.browser_item_recent_progressBar.fadeVisible()
            itemView.browser_item_recent_textView.text = mainActivity.getString(R.string.main_open_series)
            itemView.browser_item_recent_textView.fadeVisible()
            itemView.browser_item_recent_imageView.visible()
        }

//        internal fun setStateInvalid() {
//            itemView.browser_item_recent_progressBar.gone()
//            itemView.browser_item_recent_textView.onClick { deleteBookAt(adapterPosition) }
//            itemView.browser_item_recent_textView.text = mainActivity.getString(R.string.main_remove)
//            itemView.browser_item_recent_textView.fadeVisible()
//            itemView.browser_item_recent_imageView.visible()
//
//            itemView.browser_item_recent_imageView.onClick { showRemoveDialog(adapterPosition) }
//            itemView.browser_item_recent_imageView.onLongClick { showRemoveDialog(adapterPosition) }
//        }

        private fun showRemoveDialog(position: Int) {
            val book = viewModel.getRecentAt(position) ?: Book()
            dialogUtil.getDialogRecentRemove(mainActivity, book).apply {
                setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_remove)) { _, _ -> deleteBookAt(position) }
                setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel)) { dialog, _ -> dialog.dismiss() }
                show()

                findViewById<TextView>(android.R.id.message)?.apply {
                    textSize = 11F
                }
            }
        }

        private fun openSeries() {
            val item = viewModel.getRecentAt(adapterPosition)
            item?.let { mainActivity.showFragmentBrowserSeries(item) }
        }

        internal fun loadImage(stringUrl: String) {
            Glide.with(homeFragmentImpl1Content)
                    .load(stringUrl)
                    .apply(RequestOptions()
                            .priority(Priority.HIGH)
                            .disallowHardwareConfig()
                            .format(DecodeFormat.PREFER_RGB_565)
                            .transform(RoundedCorners(Settings.ROUNDED_CORNERS_VALUE))
                            .error(Settings.ERROR_DRAWABLE))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            setStateValid()
                            return false
                        }
                    })
                    .into(itemView.browser_item_recent_imageView)
        }
    }

    private fun BrowserItemRecentBinding.remoteSync(helper: RecentHolder, item: Book) {
        GlobalScope.launch(Dispatchers.Main) {
            //add delay to prevent remote request while fast scrolling
            delay(600)
            try {
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val currentPosition = helper.adapterPosition
                if (currentPosition in firstVisible..lastVisible) {
                    viewModel.getRemoteUserApi(item).observe(homeFragmentImpl1Content, Observer { result ->
                        result?.let {
                            updateBook(item)
                            viewModel.addRecent(result, setTimeAccessed = false)
                        }
                    })
                }
            } catch (e: Exception) {
                //views could be destroyed during delay, do nothing
            }
        }
    }

    private fun BrowserItemRecentBinding.updateBook(item: Book) {
        setVariable(BR.item, item)
    }

    private fun startReadAt(adapterPosition: Int, itemView: View) {
        val item = viewModel.getRecentAt(adapterPosition)
        item?.let {
            Timber.d("Recent selected: position[$adapterPosition] title[${item.title}]")
            mainActivity.startReader(ReadData(book = item, bookmarksEnabled = false, sharedElement = itemView.browser_item_recent_imageView, source = Source.RECENT))
        } ?: mainActivity.toast("Failed to find book!")
    }

    private fun deleteBookAt(position: Int) {
        val item = viewModel.getRecentAt(position)
        item?.let {
            viewModel.removeRecent(item).observe(homeFragmentImpl1Content, Observer { result ->
                Timber.d("Recent removed: position[$position] title[${item.title}]")
                homeFragmentImpl1Content.handleRecentResult(result)

                dialogUtil.getSnackBarDeleteRecent(mainActivity.frameLayout, it).apply {
                    setAction(context.getString(R.string.dialog_undo)) { onClickSnackBarUndoDeleteRecent(item) }
                    show()
                }
            })
        } ?: mainActivity.toast("Failed to find book!")
    }

    private fun onClickSnackBarUndoDeleteRecent(item: Book) = viewModel.addRecent(item).observe(homeFragmentImpl1Content, Observer { result ->
        homeFragmentImpl1Content.handleRecentResult(result)
    })

    internal fun update(result: List<Book>) {
        val diffUtilHelper = DiffUtilHelper(this)
        diffUtilHelper.liveData.observe(homeFragmentImpl1Content, Observer {
            if (it == true) onDiffUtilUpdateFinished(result)
        })
        diffUtilHelper.updateBookList(data, result)
    }

    private fun onDiffUtilUpdateFinished(result: List<Book>) {
        Timber.i("DiffUtil successful. oldDataSize[${data.size}] newDataSize[${result.size}]")

        //scroll to beginning if first item has changed
        if (data.isNotEmpty()) {
            val oldFirstRecent = viewModel.getRecentAt(0)
            val newFirstRecent = result[0]
            val isFirstChanged = oldFirstRecent != null && !oldFirstRecent.isMatch(newFirstRecent)
            Timber.d("isFirstChanged: $isFirstChanged  oldFirst[${oldFirstRecent?.title}] newFirst[${newFirstRecent.title}]")
            if (isFirstChanged) homeFragmentImpl1Content.scrollToFirstRecent()
        }

        viewModel.setRecentList(result)
    }

}