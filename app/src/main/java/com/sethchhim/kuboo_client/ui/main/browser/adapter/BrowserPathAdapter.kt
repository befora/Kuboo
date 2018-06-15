package com.sethchhim.kuboo_client.ui.main.browser.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BR
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Constants.URL_PATH_ROOT
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.databinding.BrowserItemPathBinding
import com.sethchhim.kuboo_client.ui.main.browser.BrowserBaseFragmentImpl1_Content
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import kotlinx.android.synthetic.main.browser_item_path.view.*
import kotlinx.android.synthetic.main.browser_layout_path.view.*
import org.jetbrains.anko.layoutInflater
import javax.inject.Inject

class BrowserPathAdapter(private val browserFragment: BrowserBaseFragmentImpl1_Content, val view: View, val viewModel: ViewModel) : BaseQuickAdapter<Book, BrowserPathAdapter.PathHolder>(R.layout.browser_item_path, viewModel.getPathList()) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject lateinit var context: Context
    @Inject lateinit var systemUtil: SystemUtil

    private val pathHorizontalScrollView = view.browser_layout_path_horizontalScrollView

    override fun convert(helper: PathHolder, item: Book) {
        val itemView = helper.itemView
        val binding = helper.binding
        binding.setVariable(BR.item, item)

        when (item.linkSubsection) {
            URL_PATH_ROOT -> itemView.browser_item_path_textView.text = context.getString(R.string.browser_folder)
        }

        val isFocus = viewModel.getPathPosition() == helper.layoutPosition + 1
        if (isFocus) {
            itemView.post {
                itemView.browser_item_path_textView.setTypeface(null, Typeface.BOLD)
                itemView.browser_item_path_textView.textSize = 14F
                itemView.browser_item_path_textView.setTextColor(ContextCompat.getColor(context, R.color.lightColorAccent))
                itemView.browser_item_path_textView.animateGrow()
                pathHorizontalScrollView.smoothScrollTo(helper.itemView.left, 0)
            }
        } else {
            itemView.browser_item_path_textView.setTypeface(null, Typeface.NORMAL)
            itemView.browser_item_path_textView.textSize = 12F
            itemView.browser_item_path_textView.setTextColor(ContextCompat.getColor(context, R.color.secondaryText))
        }

        helper.itemView.browser_item_path_appCompatImageView.apply {
            setColorFilter(systemUtil.getPrimaryTextColor())
            visibility = when (helper.layoutPosition > 0) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        }

        helper.itemView.setOnClickListener {
            viewModel.setPathPosition(helper.layoutPosition + 1)
            notifyItemRangeChanged(0, viewModel.getPathSize())
            browserFragment.populateContent(item, addPath = false)
        }
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<BrowserItemPathBinding>(context.layoutInflater, layoutResId, parent, false)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }

    inner class PathHolder(view: View) : BaseViewHolder(view) {
        val binding = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as BrowserItemPathBinding
    }

    private fun TextView.animateGrow() {
        val growAnimation = ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        growAnimation.duration = 200
        startAnimation(growAnimation)
    }

}
