package com.sethchhim.kuboo_client.ui.about.adapter

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.Faq
import com.sethchhim.kuboo_client.util.SystemUtil
import kotlinx.android.synthetic.main.about_item_faq.view.*
import javax.inject.Inject

class FaqAdapter(context: Context) : BaseQuickAdapter<Faq, BaseViewHolder>(R.layout.about_item_faq, listOf(
        Faq(question = context.getString(R.string.about_question1), answer = context.getString(R.string.about_answer1)),
        Faq(question = context.getString(R.string.about_question2), answer = context.getString(R.string.about_answer2)),
        Faq(question = context.getString(R.string.about_question3), answer = context.getString(R.string.about_answer3)),
        Faq(question = context.getString(R.string.about_question4), answer = context.getString(R.string.about_answer4)))) {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var systemUtil: SystemUtil

    override fun convert(helper: BaseViewHolder, item: Faq) {
        helper.itemView.about_item_faq_textView1.text = item.question
        helper.itemView.about_item_faq_textView2.text = item.answer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)

        var width = systemUtil.getSystemWidth()
        if (systemUtil.isOrientationPortrait()) {
            if (width >= 1280) width = 1280
        }

        when (systemUtil.isOrientationPortrait()) {
            true -> {
                holder.itemView.about_item_faq_textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 20).toFloat())
                holder.itemView.about_item_faq_textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 25).toFloat())
            }
            false -> {
                holder.itemView.about_item_faq_textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 40).toFloat())
                holder.itemView.about_item_faq_textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width / 45).toFloat())
            }
        }

        holder.itemView.about_item_faq_textView1.typeface = systemUtil.robotoCondensedBold
        holder.itemView.about_item_faq_textView2.typeface = systemUtil.robotoCondensedRegular
        return holder
    }

}