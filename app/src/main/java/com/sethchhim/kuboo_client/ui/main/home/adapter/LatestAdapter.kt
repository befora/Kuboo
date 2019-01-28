package com.sethchhim.kuboo_client.ui.main.home.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BR
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.databinding.BrowserItemLatestBinding
import com.sethchhim.kuboo_client.ui.main.home.HomeFragmentImpl1_Content
import com.sethchhim.kuboo_client.util.DiffUtilHelper
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import kotlinx.android.synthetic.main.browser_item_latest.view.*
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject

class LatestAdapter(private val homeFragmentImpl1Content: HomeFragmentImpl1_Content, val viewModel: ViewModel) : BaseQuickAdapter<Book, LatestAdapter.LatestHolder>(R.layout.browser_item_latest, viewModel.getLatestList()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject
    lateinit var kubooRemote: KubooRemote
    @Inject
    lateinit var systemUtil: SystemUtil

    private val mainActivity = homeFragmentImpl1Content.mainActivity

    override fun convert(helper: LatestHolder, item: Book) {
        val binding = helper.binding
        binding.setVariable(BR.item, item)
        helper.loadImage(helper, item)
    }

    override fun getItemId(position: Int) = data[position].id.toLong()

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<BrowserItemLatestBinding>(homeFragmentImpl1Content.layoutInflater, layoutResId, parent, false)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }

    inner class LatestHolder(view: View) : BaseViewHolder(view) {
        val binding = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as BrowserItemLatestBinding

        internal fun loadImage(helper: LatestHolder, item: Book) {
            Glide.with(homeFragmentImpl1Content)
                    .load(item.getPreviewUrl())
                    .apply(RequestOptions()
                            .priority(Priority.LOW)
                            .disallowHardwareConfig()
                            .format(DecodeFormat.PREFER_RGB_565)
                            .error(Settings.ERROR_DRAWABLE))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            setStateInvalid(helper, item)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            setStateValid(helper, item)
                            return false
                        }
                    })
                    .into(helper.itemView.browser_item_latest_imageView)
        }
    }

    internal fun setStateValid(helper: LatestHolder, item: Book) {
        helper.itemView.browser_item_latest_imageView.transitionName = item.getPreviewUrl()
        helper.itemView.browser_item_latest_imageView.setOnClickListener { startReadAt(helper.adapterPosition, helper.itemView) }
        helper.itemView.browser_item_latest_imageView.visible()
    }

    internal fun setStateInvalid(helper: LatestHolder, item: Book) {
        helper.itemView.browser_item_latest_imageView.transitionName = item.getPreviewUrl()
        helper.itemView.browser_item_latest_imageView.setOnClickListener { startReadAt(helper.adapterPosition, helper.itemView) }
        helper.itemView.browser_item_latest_imageView.visible()
    }

    private fun startReadAt(adapterPosition: Int, itemView: View) {
        val item = data[adapterPosition]
        item?.let {
            Timber.d("Latest selected: position[$adapterPosition] title[${item.title}]")
            mainActivity.startReader(ReadData(book = item, bookmarksEnabled = false, sharedElement = itemView.browser_item_latest_imageView, source = Source.LATEST))
        } ?: mainActivity.toast("Failed to find book!")
    }

    internal fun update(result: List<Book>) {
        val diffUtilHelper = DiffUtilHelper(this)
        diffUtilHelper.liveData.observe(homeFragmentImpl1Content, Observer {
            if (it == true) onDiffUtilUpdateFinished(result)
        })
        diffUtilHelper.updateBookList(data, result)
    }

    private fun onDiffUtilUpdateFinished(result: List<Book>) {
        Timber.i("DiffUtil successful. oldDataSize[${data.size}] newDataSize[${result.size}]")
        viewModel.setLatestList(result)
    }

}