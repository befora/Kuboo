package com.sethchhim.kuboo_client.ui.log.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sethchhim.kuboo_client.BR
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.Log
import com.sethchhim.kuboo_client.databinding.LogItemBaseBinding
import com.sethchhim.kuboo_client.util.SystemUtil
import org.jetbrains.anko.layoutInflater
import javax.inject.Inject

class LogAdapter(list: List<Log>) : BaseQuickAdapter<Log, LogAdapter.LogHolder>(R.layout.log_item_base, list) {

    init {
        BaseApplication.appComponent.inject(this)
        setHasStableIds(true)
    }

    @Inject lateinit var context: Context
    @Inject lateinit var systemUtil: SystemUtil


    override fun convert(helper: LogHolder, item: Log) {
        val itemView = helper.itemView
        val binding = helper.binding
        binding.setVariable(BR.item, item)
    }

    override fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<LogItemBaseBinding>(context.layoutInflater, layoutResId, parent, false)
        val view = binding.root
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding)
        return view
    }

    inner class LogHolder(view: View) : BaseViewHolder(view) {
        val binding = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as LogItemBaseBinding
    }

}
